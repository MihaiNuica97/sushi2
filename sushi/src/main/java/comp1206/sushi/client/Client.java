package comp1206.sushi.client;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements ClientInterface, Serializable
{
    
    private static final Logger logger = LogManager.getLogger("Client");
    
    public Restaurant restaurant;
    public ArrayList<Dish> dishes = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Postcode> postcodes = new ArrayList<>();
    private ArrayList<UpdateListener> listeners = new ArrayList<>();
//    private Socket clientSocket;
    private ClientComms comms;
    private IncomingCommsThread commsThread;
    
    private User loggedUser;
    
    public Client()
    {
        logger.info("Starting up client...");
    
        comms = new ClientComms(1030);
        getDataFromServer();
        
    }
    
    @Override
    public Restaurant getRestaurant()
    {
        return restaurant;
    }
    
    @Override
    public String getRestaurantName()
    {
        return restaurant.getName();
    }
    
    @Override
    public Postcode getRestaurantPostcode()
    {
        return restaurant.getLocation();
    }
    
    @Override
    public User register(String username, String password, String address, Postcode postcode)
    {
        User user = new User(username, password, address, postcode);
        comms.sendMesage(new Message(user,"Register"));
    
        Message message = comms.receiveMessage();
        
        if(message.getInstructions().equals("Already exists"))
        {
            System.out.println("User name already exists!");
            return null;
        }
        else if(message.getInstructions().equals("Registered"))
        {
            System.out.println("Added user " + user.getName() + " with password " + user.getPassword());
            loggedUser = user;
            
            commsThread = new IncomingCommsThread();
            commsThread.start();
            
            return user;
        }
        return null;
    }
    
    public void getDataFromServer()
    {
        Message initMessage = comms.receiveMessage();
        dishes = new ArrayList<>();
        dishes = (ArrayList)initMessage.getObject();
    
        initMessage = comms.receiveMessage();
        restaurant = (Restaurant)initMessage.getObject();
        
        initMessage = comms.receiveMessage();
        postcodes = (ArrayList)initMessage.getObject();
    }
    
    @Override
    public User login(String username, String password)
    {
        User thisUser = new User(username,password,null,null);
        
    
        comms.sendMesage(new Message(thisUser,"Login"));
        System.out.println("Sent " +  thisUser);
        
        Message message = comms.receiveMessage();
        System.out.println(message.getInstructions());
        loggedUser = (User)message.getObject();
        
        if (message.getInstructions().equals("Login Successful"))
        {
            System.out.println("User " + loggedUser.getName() + " has logged in");
            commsThread = new IncomingCommsThread();
            commsThread.start();
            return loggedUser;
        }
        else
        {
            System.out.println("Invalid user credentials");
            return null;
        }
    }
    
    
    @Override
    public List<Postcode> getPostcodes()
    {
        return postcodes;
    }
    
    @Override
    public List<Dish> getDishes()
    {
        return dishes;
    }
    
    @Override
    public String getDishDescription(Dish dish)
    {
        for(Dish thisDish: dishes)
        {
            if(thisDish.getName().equals(dish.getName()))
            {
                return dish.getDescription();
            }
        }
        System.out.println("Dish not found");
        return null;
    }
    
    @Override
    public Number getDishPrice(Dish dish)
    {
        for(Dish thisDish: dishes)
        {
            if(thisDish.getName().equals(dish.getName()))
            {
                return dish.getPrice();
            }
        }
        System.out.println("Dish not found");
        return null;
    }
    
    @Override
    public Map<Dish, Number> getBasket(User user)
    {
        return user.getBasket();
    }
    
    @Override
    public Number getBasketCost(User user)
    {
        return user.getBasketCost();
    }
    
    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity)
    {
        user.addToBasket(dish,quantity);
    }
    
    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity)
    {
        user.updateDishInBasket(dish, quantity);
    }
    
    @Override
    public Order checkoutBasket(User user)
    {
        Order order = new Order(user,user.getBasket());
        orders.add(order);
        comms.sendMesage(new Message(order, "Add Order"));
        user.clearBasket();
        return order;
    }
    
    @Override
    public void clearBasket(User user)
    {
        user.clearBasket();
    }
    
    @Override
    public List<Order> getOrders(User user)
    {
        return orders;
    }
    
    @Override
    public boolean isOrderComplete(Order order)
    {
        return getOrderStatus(order).equals("Complete");
    }
    
    @Override
    public String getOrderStatus(Order order)
    {
        order.setUser(loggedUser);
        comms.sendMesage(new Message(order, "Order Status Check"));
        return order.getStatus();
    }
    
    @Override
    public Number getOrderCost(Order order)
    {
        return order.getOrderCost();
    }
    
    @Override
    public void cancelOrder(Order order)
    {
        order.setStatus("Cancelled");
        comms.sendMesage(new Message(order, "Cancel Order"));
        orders.remove(order);
    }
    
    @Override
    public void addUpdateListener(UpdateListener listener)
    {
        listeners.add(listener);
    }
    
    @Override
    public void notifyUpdate()
    {
        for(UpdateListener listener : listeners)
        {
            listener.updated(new UpdateEvent());
        }
    }
    
    
    
    class IncomingCommsThread extends Thread
    {
        IncomingCommsThread()
        {
        
        }
        public void run()
        {
            
            while (true)
            {
                Message message = comms.receiveMessage();
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println(message.getInstructions());
                parseMessage(message);
            }
            
        }
        void parseMessage(Message message)
        {
            switch (message.getInstructions())
            {
                case "Update Order":
                    Order order = (Order)message.getObject();
                    for(Order thisOrder: orders)
                    {
                        if(thisOrder.getName().equals(order.getName()))
                        {
                            System.out.println("Updating order: " + thisOrder.getName());
                            thisOrder.setStatus(order.getStatus());
                            notifyUpdate();
                        }
                    }
                    System.out.println(order.getName());
                    break;
                    
                case "Remove Order":
                    Order orderToDelete = (Order)message.getObject();
                    System.out.println(orderToDelete.getName());
                    for(Order thisOrder: orders)
                    {
                        if(thisOrder.getName().equals(orderToDelete.getName()))
                        {
                            orders.remove(thisOrder);
                            notifyUpdate();
                            break;
                        }
                    }
                    System.out.println("Couldn't find order");
                    break;
                    
                case "Add Dish":
                    Dish dishToAdd = (Dish)message.getObject();
                    dishes.add(dishToAdd);
                    System.out.println("Added dish " + dishToAdd.getName());
                    System.out.println();
                    notifyUpdate();
                    break;
    
                case "Remove Dish":
                    Dish dishToDelete = (Dish)message.getObject();
                    for(Dish thisDish: dishes)
                    {
                        if(thisDish.getName().equals(dishToDelete.getName()))
                        {
                            dishes.remove(thisDish);
                            notifyUpdate();
                            break;
                        }
                    }
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
    }
    
    

    
}
