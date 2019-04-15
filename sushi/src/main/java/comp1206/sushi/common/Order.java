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
        this.order = order;
    }
    
    public Order(User user)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.name = dtf.format(now);
        this.user = user;
        order = new HashMap<>();
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
        return 1;
    }
    
    @Override
    public String getName()
    {
        return this.name;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        notifyUpdate("status", this.status, status);
        this.status = status;
    }
    
}
