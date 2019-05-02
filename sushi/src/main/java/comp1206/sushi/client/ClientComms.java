package comp1206.sushi.client;

import comp1206.sushi.common.Comms;
import comp1206.sushi.common.Message;
import comp1206.sushi.common.User;

import java.io.*;
import java.net.*;

public class ClientComms
{
    private int port;
    private ServerConnection server;
    private boolean connected;
    
    public ClientComms(int port)
    {
        connected = false;
        this.port = port;
        openSocket();
    }
    
    public void openSocket()
    {
        try
        {
            server = new ServerConnection(new Socket("localhost",port));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public Socket getSocket()
    {
        return server.getSocket();
    }
    
    public ObjectOutputStream getOutputStream()
    {
        return server.getOutputStream();
    }
    
    public ObjectInputStream getInputStream()
    {
        return server.getInputStream();
    }
    
    public void sendObject(Object object)
    {
        ObjectOutputStream stream = server.getOutputStream();
        if(connected)
        {
            try
            {
                stream.writeObject(object);
                stream.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void sendMesage(Message message)
    {
        sendObject(message);
    }
    
    public Object listenForInput()
    {
        
        while(connected)
        {
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return receiveObject();
        }
        return null;
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
            } catch(SocketException e)
            {
                connected = false;
            }catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            return null;
    }
    
    public Message receiveMessage()
    {
        Object obj = receiveObject();
        if(obj!=null)
        {
            return (Message) obj;
        }
        else return null;
        }
    
    public Message listenForMessages()
    {
        Object obj = listenForInput();
        return (Message) obj;
    }
    class ServerConnection
    {
        private Socket socket;
        private ObjectOutputStream outputStream = null;
        private ObjectInputStream inputStream = null;
        
        
        ServerConnection(Socket socket)
        {
            this.socket = socket;
            try
            {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.flush();
                inputStream = new ObjectInputStream(socket.getInputStream());
                connected = true;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Socket getSocket()
        {
            return socket;
        }
    
        ObjectOutputStream getOutputStream()
        {
            return outputStream;
        }
    
        ObjectInputStream getInputStream()
        {
            return inputStream;
        }
        
    }
    
    

}
