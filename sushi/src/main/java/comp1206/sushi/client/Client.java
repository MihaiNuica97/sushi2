package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements ClientInterface
{
    
    private static final Logger logger = LogManager.getLogger("Client");
    
    public Restaurant restaurant;
    public ArrayList<Dish> dishes = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<Supplier> suppliers = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Postcode> postcodes = new ArrayList<>();
    private ArrayList<UpdateListener> listeners = new ArrayList<>();
    
    public Client()
    {
        logger.info("Starting up client...");
    
        Postcode restaurantPostcode = new Postcode("SO17 1BJ");
        restaurant = new Restaurant("Mock Restaurant",restaurantPostcode);
    
        Postcode postcode1 = new Postcode("SO17 1TJ");
        Postcode postcode2 = new Postcode("SO17 1BX");
        Postcode postcode3 = new Postcode("SO17 2NJ");
        Postcode postcode4 = new Postcode("SO17 1TW");
        Postcode postcode5 = new Postcode("SO17 2LB");
        postcodes.add(postcode1);
        postcodes.add(postcode2);
        postcodes.add(postcode3);
        postcodes.add(postcode4);
        postcodes.add(postcode5);
    
    
        Supplier supplier1 = new Supplier("Supplier 1",postcode1);
        Supplier supplier2 = new Supplier("Supplier 2",postcode2);
        Supplier supplier3 = new Supplier("Supplier 3",postcode3);
        suppliers.add(supplier1);
        suppliers.add(supplier2);
        suppliers.add(supplier3);
        
  
        Dish dish1 = new Dish("Dish 1","Dish 1",1,1,10);
        Dish dish2 = new Dish("Dish 2","Dish 2",2,1,10);
        Dish dish3 = new Dish("Dish 3","Dish 3",3,1,10);
        dishes.add(dish1);
        dishes.add(dish2);
        dishes.add(dish3);
        
        User user1 = register("Bulangiu","laba","Dreacu'",postcode1);
        User user2 = register("1","1","Dreacu'",postcode2);
    
    
        Order order1 = new Order(user1);
        order1.addDish(dish1,3);
        orders.add(order1);
        
        
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
        User user = new User(username,password,address,postcode);
        
        for(User thisUser: users)
        {
            if(thisUser.getName().equals(user.getName()))
            {
                System.out.println("User name already exists!");
                return null;
            }
        }
        System.out.println("Added user " + user.getName() + " with password " + user.getPassword());
        users.add(user);
        return user;
    }
    
    @Override
    public User login(String username, String password)
    {
        for(User thisUser: users)
        {
            if(thisUser.getName().equals(username) && thisUser.getPassword().equals(password))
            {
                return thisUser;
            }
        }
        
        System.out.println("Invalid user credentials");
        return null;
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
        orders.remove(orders.indexOf(order));
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
    
}
