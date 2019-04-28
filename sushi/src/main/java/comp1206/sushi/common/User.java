package comp1206.sushi.common;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;

import java.util.HashMap;

public class User extends Model {
	
	private String name;
	private String password;
	private String address;
	private Postcode postcode;
	private HashMap<Dish, Number> basket;
	
	
	public User(String username, String password, String address, Postcode postcode) {
		this.name = username;
		this.password = password;
		this.address = address;
		this.postcode = postcode;
		this.basket = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getDistance() {
		return postcode.getDistance();
	}

	public Postcode getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	public String getPassword()
	{
		return this.password;
	}
	
	public HashMap<Dish, Number> getBasket()
		{
			return basket;
		}
		
	public void addToBasket(Dish dish, Number quantity)
	{
		if(basket.containsKey(dish))
		{
			basket.put(dish,(Integer)basket.get(dish) + (Integer)quantity);
		}
		else
		{
			basket.put(dish, quantity);
		}
	}
	public void updateDishInBasket(Dish dish, Number quantity)
	{
		basket.put(dish, quantity);
	}
	public void clearBasket()
	{
		basket = new HashMap<>();
	}
	
	public Number getBasketCost()
	{
		float cost = 0;
		
		for(Number price: basket.values())
		{
			cost += price.floatValue();
		}
		return (Number)cost;
	}
	
}
