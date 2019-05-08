package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class StockManagement
{
    private Server server;
    private HashMap<Dish, Number> dishStock;
    private HashMap<Ingredient, Number> ingredientStock;
    private LinkedList<Ingredient>ingredientRestocks;
    private LinkedList<Ingredient>ingredientDeliveryQueue;
    private HashMap<Dish,Integer> dishRestocks;
    public StockManagement(Server server)
    {
        this.server = server;
        dishStock = new HashMap<>();
        ingredientStock = new HashMap<>();
        ingredientRestocks = new LinkedList<>();
        dishRestocks = new HashMap<>();
        ingredientDeliveryQueue = new LinkedList<>();
    }
    
    public synchronized Ingredient verifyIngredientsStock()
    {
        updateIngredientRestockQueue();
        for(Ingredient ingredient: ingredientStock.keySet())
        {
            if(ingredientRestocks.contains(ingredient))
            {
                ingredientRestocks.remove(ingredient);
                ingredientDeliveryQueue.add(ingredient);
                return ingredient;
            }
        }
        return null;
    }
    
    private synchronized void updateIngredientRestockQueue()
    {
        ingredientRestocks.clear();
        for(Ingredient ingredient: ingredientStock.keySet())
        {
            if((ingredientStock.get(ingredient).intValue() < ingredient.getRestockThreshold().intValue()) && !ingredientDeliveryQueue.contains(ingredient))
            {
                    ingredientRestocks.add(ingredient);
            }
        }
        
    }
    
    public synchronized void verifyDishStock()
    {
        for(Dish dish: dishStock.keySet())
        {
            if(dishStock.get(dish).floatValue() < dish.getRestockThreshold().floatValue())
            {
                dishStock.put(dish,dishStock.get(dish).floatValue() + dish.getRestockAmount().floatValue());
                System.out.println(dish.getName() + " has been restocked!");
                server.notifyUpdate();
            }
        }
    }
    
    public void updateDishStock(Dish dish, Number stock)
    {
        dishStock.put(dish,stock);
    }
    public synchronized void updateIngredientStock(Ingredient ingredient, Number stock)
    {
        ingredientStock.put(ingredient,stock);
        updateIngredientRestockQueue();
    }
    
    public void restockDish()
    {
    
    }
    public synchronized void restockIngredient(Ingredient ingredient)
    {
        ingredientStock.put(ingredient,(Number)(ingredientStock.get(ingredient).intValue() + ingredient.getRestockAmount().intValue()));
        ingredientDeliveryQueue.remove(ingredient);
        updateIngredientRestockQueue();
    }
    
    public HashMap<Dish, Number> getDishStock()
    {
        return dishStock;
    }
    public HashMap<Ingredient, Number> getIngredientStock()
    {
        return ingredientStock;
    }
    public void setDishStock(HashMap<Dish, Number> dishStock)
    {
        this.dishStock = dishStock;
    }
    
    public void setIngredientStock(HashMap<Ingredient, Number> ingredientStock)
    {
        this.ingredientStock = ingredientStock;
        updateIngredientRestockQueue();
    }
    
    public Server getServer()
    {
        return server;
    }
    
    //TODO IMPLEMENT RESTOCK QUEUE FOR DISHES
}
