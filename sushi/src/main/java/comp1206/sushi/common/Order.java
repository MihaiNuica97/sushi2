package comp1206.sushi.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model
{
    
    private String status;
    private User user;
    private HashMap<Dish, Number> order;
    
    public Order(User user, HashMap<Dish, Number> order)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.name = dtf.format(now);
        this.user = user;
        this.order = new HashMap<>();
        this.order.putAll(order);
        this.status = "Incomplete";
    }
    
    public Order(User user)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.name = dtf.format(now);
        this.user = user;
        order = new HashMap<>();
        this.status = "Incomplete";
    }
    
    public Order()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.name = dtf.format(now);
        order = new HashMap<>();
    }
    
    public HashMap<Dish, Number> getOrder()
    {
        return order;
    }
    
    public void addDish(Dish dish, Number count)
    {
        order.put(dish, count);
    }
    
    
    public Number getDistance()
    {
        return user.getDistance();
    }
    
    @Override
    public String getName()
    {
        return this.name;
    }
    
    public synchronized String  getStatus()
    {
        return status;
    }
    
    public synchronized void setStatus(String status)
    {
        notifyUpdate("status", this.status, status);
        this.status = status;
    }
    
    public Number getOrderCost()
    {
        float sum = 0;
        
        for(Dish dish: order.keySet())
        {
            sum += dish.getPrice().floatValue()*order.get(dish).intValue();
        }
        return sum;
    }
    
    public User getUser()
    {
        return user;
    }
    
    public void setUser(User user)
    {
        this.user = user;
    }
}
