package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Comms implements Serializable
{
    private ServerSocket serverSocket;
    private HashMap<User, ConnectedClient> openConnections;
    private Server server;
    
    private int port;
    public Comms(int port, Server server)
    {
        this.port = port;
        openConnections = new HashMap<>();
        this.server = server;
    }
    
    public void openIncomingCommsSocket()
    {
        IncomingCommsThread incomingComms = new IncomingCommsThread(this);
        incomingComms.start();
    }
    
    public void openServerSocket()
    {
        try
        {
            serverSocket = new ServerSocket(port);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }
    
    public HashMap<User, ConnectedClient> getOpenConnections()
    {
        return openConnections;
    }
    
    public void broadcastMessage(Object object)
    {
        if(!openConnections.values().isEmpty())
        {
            for (ConnectedClient client : openConnections.values())
            {
                if (client != null)
                {
                    client.sendObject(object);
                }
            }
        }
    }
    public void sendMessageToUser(User user, Message message)
    {
        ConnectedClient client = null;
        for(User thisUser: getOpenConnections().keySet())
        {
            if(thisUser.getName().equals(user.getName()))
            {
                System.out.println("Sending message to " + thisUser.getName());
                client = getOpenConnections().get(thisUser);
            }
        }
        if(client != null)
        {
            client.sendMessage(message);
        }
        
    }

    
    public User verifyUser(User user)
    {
        ArrayList<User> users = new ArrayList<>(server.getUsers());
        for(User thisUser: users)
        {
            if(thisUser.getName().equals(user.getName())
                && thisUser.getPassword().equals(user.getPassword()))
            {
                return thisUser;
            }
        }
        return null;
    }
    


    
    class ConnectedClient
    {
        private Socket socket;
        private ObjectInputStream inputStream = null;
        private ObjectOutputStream outputStream = null;
        
        
        ConnectedClient(Socket socket)
        {
            this.socket = socket;
            try
            {
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    
        public void initialize()
        {
            sendMessage(new Message(server.getRestaurant(),"Init"));
            sendMessage(new Message(new ArrayList<>(server.getDishes()),"Init"));
            sendMessage(new Message(new ArrayList<>(server.getPostcodes()),"Init"));
        }
    
        public void sendObject(Object object)
        {
            try
            {
                outputStream.writeObject(object);
                outputStream.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        public void sendMessage(Message message)
        {
            sendObject(message);
        }
        
        
        public Object receiveObject()
        {
            try
            {
                Object object = this.getInputStream().readObject();
                return object;
            } catch(EOFException e)
            {
//            System.out.println("Waiting for input...");
            } catch(SocketException ignored)
            {
            }catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    
        public Message receiveMessage()
        {
            return (Message)this.receiveObject();
        }
        public void listenForMessages()
        {
            class incomingMsgThread extends Thread
            {
                Socket socket;
                ConnectedClient client;
                public incomingMsgThread(ConnectedClient client)
                {
                    this.socket = client.getSocket();
                    this.client = client;
                }
                public void run()
                {
                    while (socket!=null && !socket.isClosed())
                    {
                        Message message = client.receiveMessage();
                        if(message != null)
                        {
                            System.out.println(message.getInstructions());
                            client.parseMsg(message);
                        }
                    }
                   
                    try
                    {
                        client.getInputStream().close();
                        client.getOutputStream().close();
                        client.socket.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    this.interrupt();
                }
            }
            new incomingMsgThread(this).start();
        }
        void parseMsg(Message message)
        {
            switch (message.getInstructions())
            {
                case "Add Order":
                    Order orderToAdd = (Order)message.getObject();
                    server.orders.add(orderToAdd);
                    break;
            
                case "Order Status Check":
                    Order orderToCheck = (Order)message.getObject();
                    server.getOrderStatus(orderToCheck);
                    if(orderToCheck == null || !server.getOrders().contains(orderToCheck))
                    {
                        sendMessage(new Message(orderToCheck,"Remove Order"));
                    }
                    break;
            
                case "Cancel Order":
                    Order orderToRemove = (Order)message.getObject();
                    server.orders.remove(orderToRemove);
                    break;
            
                case "5":
                    break;
            
                case "6":
                    break;
            
                case "7":
                    break;
            
                case "8":
                    break;
            }
        }
        
        Socket getSocket()
        {
            return this.socket;
        }
        
        ObjectOutputStream getOutputStream()
        {
            return  this.outputStream;
        }
    
        ObjectInputStream getInputStream()
        {
            return this.inputStream;
        }
    }
    
    class IncomingCommsThread extends Thread
    {
        Comms thisComms;
        IncomingCommsThread(Comms comms)
        {
            thisComms = comms;
        }
        public void run()
        {
            try
            {
               while(true)
               {
                   ConnectedClient client = new ConnectedClient(serverSocket.accept());
                   client.initialize();
                   boolean connected = true;
                   while (connected)
                   {
                       //Connecting client: also handles the login sequence
        
        
                       Message message = client.receiveMessage();
                       User loggedUser = (User) message.getObject();
                       if (message.getInstructions().equals("Login"))
                       {
                           if (verifyUser(loggedUser) != null)
                           {
                               openConnections.put(loggedUser, client);
                               System.out.println("User " + loggedUser.getName() + " has connected");
                               client.sendMessage(new Message(verifyUser(loggedUser), "Login Successful"));
                               client.listenForMessages();
                               connected = false;
                           }
                           else
                           {
                               client.sendMessage(new Message(loggedUser, "Credentials invalid"));
                           }
                       }
                       else if (message.getInstructions().equals("Register"))
                       {
                           if (verifyUser(loggedUser) == null)
                           {
                               client.sendMessage(new Message(loggedUser, "Registered"));
                               openConnections.put(loggedUser, client);
                               server.getUsers().add(loggedUser);
                               System.out.println("User " + loggedUser.getName() + " has registered");
                               client.listenForMessages();
                               connected = false;
                           }
                           else
                           {
                               client.sendMessage(new Message(loggedUser, "Already exists"));
                           }
                       }
                   }
               }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
}
