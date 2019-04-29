package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.HashMap;
import java.util.Random;

public class StockManagement
{
    private Server server;
    private HashMap<Dish, Number> dishStock;
    private HashMap<Ingredient, Number> ingredientStock;
    
    public StockManagement(Server server)
    {
        this.server = server;
        dishStock = new HashMap<>();
        ingredientStock = new HashMap<>();
    }
    
    public synchronized void verifyIngredientsStock()
    {
        for(Ingredient ingredient: ingredientStock.keySet())
        {
            if(ingredientStock.get(ingredient).floatValue() < ingredient.getRestockThreshold().floatValue())
            {
                Random random = new Random();
                try
                {
                    Thread.sleep(random.nextInt(40) + 2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                ingredientStock.put(ingredient,ingredientStock.get(ingredient).floatValue() + ingredient.getRestockAmount().floatValue());
                System.out.println(ingredient.getName() + " has been restocked!");
                server.notifyUpdate();
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
    public void updateIngredientStock(Ingredient ingredient, Number stock)
    {
        ingredientStock.put(ingredient,stock);
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
    }
    

}
