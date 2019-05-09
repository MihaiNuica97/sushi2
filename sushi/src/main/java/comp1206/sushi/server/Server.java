package comp1206.sushi.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JOptionPane;

import comp1206.sushi.client.Client;
import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
public class Server implements ServerInterface, Serializable {

    private static final Logger logger = LogManager.getLogger("Server");
	
	public Restaurant restaurant;
	public ArrayList<Dish> dishes = new ArrayList<Dish>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<Staff> staff = new ArrayList<Staff>();
	public ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private Configuration config;
	private String path = "sushi/Configuration.txt";
	private ServerSocket serverSocket;
	private StockManagement stockManagement = new StockManagement(this);
	private Comms comms;
	
	public Server()
	{
        logger.info("Starting up server...");
		comms = new Comms(1030, this);
		comms.openServerSocket();
		loadConfiguration(path);
		serverSocket = comms.getServerSocket();
		comms.openIncomingCommsSocket();
		
		
		
	}
	
	@Override
	public List<Dish> getDishes() {
		return this.dishes;
	}

	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		
		Dish newDish = new Dish(name,description,price,restockThreshold,restockAmount);
		comms.broadcastMessage(new Message(newDish,"Add Dish"));
		this.dishes.add(newDish);
		this.notifyUpdate();
		return newDish;
	}
	
	@Override
	public void removeDish(Dish dish) {
		comms.broadcastMessage(new Message(dish,"Remove Dish"));
		this.dishes.remove(dish);
		this.notifyUpdate();
	}

	@Override
	public Map<Dish, Number> getDishStockLevels() {
		return stockManagement.getDishStock();
	}
	
	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		
	}

	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		
	}
	
	@Override
	public void setStock(Dish dish, Number stock) {
		stockManagement.updateDishStock(dish,stock);
	}

	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		stockManagement.updateIngredientStock(ingredient,stock);
	}

	@Override
	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public Ingredient addIngredient(String name, String unit, Supplier supplier,
			Number restockThreshold, Number restockAmount, Number weight) {
		Ingredient mockIngredient = new Ingredient(name,unit,supplier,restockThreshold,restockAmount,weight);
		this.ingredients.add(mockIngredient);
		this.notifyUpdate();
		return mockIngredient;
	}

	@Override
	public void removeIngredient(Ingredient ingredient) {
		int index = this.ingredients.indexOf(ingredient);
		this.ingredients.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Supplier> getSuppliers() {
		return this.suppliers;
	}

	@Override
	public Supplier addSupplier(String name, Postcode postcode) {
	    for(Postcode thisPostcode: postcodes)
        {
            if(thisPostcode.getName().equals(postcode.getName()))
            {
                Supplier mock = new Supplier(name,thisPostcode);
                this.suppliers.add(mock);
                return mock;
            }
        }
		Supplier mock = new Supplier(name,postcode);
		this.suppliers.add(mock);
		return mock;
	}


	@Override
	public void removeSupplier(Supplier supplier) {
		int index = this.suppliers.indexOf(supplier);
		this.suppliers.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Drone> getDrones() {
		return this.drones;
	}

	@Override
	public Drone addDrone(Number speed) {
		Drone mock = new Drone(speed);
		this.drones.add(mock);
		mock.setSource(restaurant.getLocation());
		mock.run(stockManagement);
		return mock;
	}

	@Override
	public void removeDrone(Drone drone) {
		int index = this.drones.indexOf(drone);
		this.drones.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Staff> getStaff() {
		return this.staff;
	}

	@Override
	public Staff addStaff(String name) {
		Staff thisStaff = new Staff(name);
		staff.add(thisStaff);
		thisStaff.verifyStock(stockManagement,thisStaff);
		return thisStaff;
	}

	@Override
	public void removeStaff(Staff staff) {
		this.staff.remove(staff);
		this.notifyUpdate();
	}

	@Override
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public void removeOrder(Order order) {
		User user = order.getUser();
		System.out.println("Deleting order");
		if(user != null)
		{
			comms.sendMessageToUser(order.getUser(),new Message(order,"Remove Order"));
		}
		orders.remove(order);
		this.notifyUpdate();
	}
	
	public synchronized Order getNextOrderToDeliver()
	{
		for(Order order: getOrders())
		{
			if(!isOrderComplete(order))
			{
				if(!order.getStatus().equals("Delivering") && stockManagement.canDeliver(order))
				{
					order.setStatus("Delivering");
					notifyUpdate();
					getOrderStatus(order);
					return order;
				}
			}
		}
		return null;
	}
	@Override
	public Number getOrderCost(Order order)
	{
		return order.getOrderCost();
	}

	@Override
	public Map<Ingredient, Number> getIngredientStockLevels() {
		return stockManagement.getIngredientStock();
	}

	@Override
	public Number getSupplierDistance(Supplier supplier) {
		return supplier.getDistance();
	}

	@Override
	public Number getDroneSpeed(Drone drone) {
		return drone.getSpeed();
	}

	@Override
	public Number getOrderDistance(Order order) {
		return order.getDistance();
	}

	@Override
	public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
		if(quantity.equals(0)) {
			removeIngredientFromDish(dish,ingredient);
		} else {
			dish.getRecipe().put(ingredient,quantity);
		}
	}

	@Override
	public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
		dish.getRecipe().remove(ingredient);
		this.notifyUpdate();
	}

	@Override
	public Map<Ingredient, Number> getRecipe(Dish dish) {
		return dish.getRecipe();
	}

	@Override
	public List<Postcode> getPostcodes() {
		return this.postcodes;
	}

	@Override
	public Postcode addPostcode(String code) {
		Postcode mock = new Postcode(code);
		mock.calculateDistance(restaurant);
		this.postcodes.add(mock);
		if(comms!=null)
        {
            comms.broadcastMessage(mock);
        }
		this.notifyUpdate();
		return mock;
	}

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		this.postcodes.remove(postcode);
		this.notifyUpdate();
	}

	@Override
	public List<User> getUsers() {
		return this.users;
	}
	
	@Override
	public void removeUser(User user) {
		this.users.remove(user);
		this.notifyUpdate();
	}

	@Override
	public void loadConfiguration(String filename) {
		
		System.out.println("Loaded configuration: " + filename);
		config = new Configuration(filename);
		config.readLines();
		
		restaurant = config.getRestaurant();
        for(Postcode postcode: config.getPostcodes())
        {
            addPostcode(postcode.getName());
        }
        
        for(Dish dish:config.getDishes())
		{
			dishes.add(dish);
		}
        
		ingredients = config.getIngredients();
		orders = config.getOrders();
		
        for (Drone thisDrone: config.getDrones())
        {
            addDrone(thisDrone.getSpeed());
        }
		for (Staff thisStaff: config.getStaff())
		{
			addStaff(thisStaff.getName());
		}
        for (Supplier supplier: config.getSuppliers())
        {
            addSupplier(supplier.getName(),supplier.getPostcode());
        }
        
		for(User user: config.getUsers())
        {
            for(Postcode postcode: postcodes)
            {
                if(user.getPostcode().getName().equals(postcode.getName()))
                {
                    users.add(new User(user.getName(),user.getPassword(),user.getAddress(),postcode));
                }
            }
        }
		

		for(Ingredient ingredient: ingredients)
		{
			setStock(ingredient,0);
		}
		for(Ingredient ingredient: config.getIngredientStock().keySet())
		{
			setStock(ingredient, config.getIngredientStock().get(ingredient));
		}
		for(Dish dish: dishes)
		{
			setStock(dish,0);
		}
		
		for(Dish dish: config.getDishStock().keySet())
		{
			setStock(dish,config.getDishStock().get(dish));
		}
		
		
		notifyUpdate();
	}

	@Override
	public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
		for(Entry<Ingredient, Number> recipeItem : recipe.entrySet()) {
			addIngredientToDish(dish,recipeItem.getKey(),recipeItem.getValue());
		}
		this.notifyUpdate();
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return order.getStatus().equals("Complete");
	}

	@Override
	public String getOrderStatus(Order order)
	{
		for(Order thisOrder: getOrders())
		{
			if(thisOrder.getName().equals(order.getName()))
			{
				comms.sendMessageToUser(thisOrder.getUser(), new Message("name:"+thisOrder.getName()+"status:"+thisOrder.getStatus(),"Update Order"));
				return thisOrder.getStatus();
			}
		}
		return null;
	}
	
	@Override
	public String getDroneStatus(Drone drone) {
		return drone.getStatus();
	}
	
	@Override
	public String getStaffStatus(Staff staff) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Working";
		}
	}

	@Override
	public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
		dish.setRestockThreshold(restockThreshold);
		dish.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
		ingredient.setRestockThreshold(restockThreshold);
		ingredient.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public Number getRestockThreshold(Dish dish) {
		return dish.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Dish dish) {
		return dish.getRestockAmount();
	}

	@Override
	public Number getRestockThreshold(Ingredient ingredient) {
		return ingredient.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Ingredient ingredient) {
		return ingredient.getRestockAmount();
	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

	@Override
	public Postcode getDroneSource(Drone drone) {
		return drone.getSource();
	}

	@Override
	public Postcode getDroneDestination(Drone drone) {
		return drone.getDestination();
	}

	@Override
	public Number getDroneProgress(Drone drone) {
		return drone.getProgress();
	}

	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		return restaurant.getLocation();
	}
	
	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}
	
	public StockManagement getStockManagement()
	{
		return stockManagement;
	}
}
