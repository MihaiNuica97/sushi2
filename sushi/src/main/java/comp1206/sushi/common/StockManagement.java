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
    private LinkedList<Dish>dishRestocks;
    private LinkedList<Dish>dishDeliveries;
    public StockManagement(Server server)
    {
        this.server = server;
        dishStock = new HashMap<>();
        ingredientStock = new HashMap<>();
        ingredientRestocks = new LinkedList<>();
        dishRestocks = new LinkedList<>();
        ingredientDeliveryQueue = new LinkedList<>();
        dishDeliveries = new LinkedList<>();
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
                boolean canRestock = true;
                for(Ingredient ingredient:dish.getRecipe().keySet())
                {
                    if(ingredientStock.get(ingredient).intValue() < dish.getRecipe().get(ingredient).intValue())
                    {
                        canRestock = false;
                    }
                }
                if(canRestock)
                {
                    for(Ingredient ingredient:dish.getRecipe().keySet())
                    {
                        ingredientStock.put(ingredient,ingredientStock.get(ingredient).intValue() - dish.getRecipe().get(ingredient).intValue());
                    }
                    dishStock.put(dish,dishStock.get(dish).floatValue() + dish.getRestockAmount().floatValue());
                }

                updateIngredientRestockQueue();
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
    
    public synchronized void deliveredOrder(Order order)
    {
        HashMap<Dish,Number> dishMap = order.getOrder();
        for(Dish dish: dishMap.keySet())
        {
            int delivered = dishMap.get(dish).intValue();
            Integer currentStock = null;
    
            for(Dish thisDish:getDishStock().keySet())
            {
                if(dish.getName().equals(thisDish.getName()))
                {
                    currentStock = getDishStock().get(thisDish).intValue();
                    updateDishStock(thisDish,currentStock-delivered);
                }
            }
            
            
        }
    }
    
    public synchronized boolean canDeliver(Order order)
    {
        HashMap<Dish,Number> dishMap = order.getOrder();
        
        for(Dish dish: dishMap.keySet())
        {
            int toDeliver = dishMap.get(dish).intValue();
            Integer currentStock = null;
            
            for(Dish thisDish:getDishStock().keySet())
            {
                if(dish.getName().equals(thisDish.getName()))
                {
                    currentStock = getDishStock().get(thisDish).intValue();
                    break;
                }
            }
            
        
            if(currentStock < toDeliver || currentStock == null)
                return false;
        }
        return true;
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
