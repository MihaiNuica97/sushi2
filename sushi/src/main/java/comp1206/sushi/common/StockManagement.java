package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.HashMap;

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
    
    public void verifyIngredientsStock()
    {
        for(Ingredient ingredient: ingredientStock.keySet())
        {
            if(ingredientStock.get(ingredient).intValue() < ingredient.getRestockThreshold().intValue())
            {
                System.out.println(ingredient.getName() + " needs to be restocked!");
            }
        }
    }
    
    public void verifyDishStock()
    {
        for(Dish dish: dishStock.keySet())
        {
            if(dishStock.get(dish).intValue() < dish.getRestockThreshold().intValue())
            {
                System.out.println(dish.getName() + " needs to be restocked!");
            }
        }
    }
    
    public void updateDishStock(Dish dish, Number stock)
    {
        dishStock.put(dish,stock);
        verifyDishStock();
    }
    public void updateIngredientStock(Ingredient ingredient, Number stock)
    {
        ingredientStock.put(ingredient,stock);
        verifyIngredientsStock();
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
