package comp1206.sushi.server;

import comp1206.sushi.common.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Configuration
{
    
    private String path;
    private String currentLine;
    private File cfgFile;
    private String[] lineArray;
    private BufferedReader reader;
    
    //internal list of models
  
    private ArrayList<Dish> dishes = new ArrayList<Dish>();
    private ArrayList<Drone> drones = new ArrayList<Drone>();
    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private ArrayList<Order> orders = new ArrayList<Order>();
    private ArrayList<Staff> staff = new ArrayList<Staff>();
    private ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
    
    
    public Configuration(String path)
    {
        this.path = path;
        cfgFile = new File(path);
        try
        {
            reader = new BufferedReader(new FileReader(cfgFile));
        } catch (FileNotFoundException e)
        {
            System.out.println("Error! Config file not found");
        }
    }
    
    public void readLines()
    {
        try
        {
            while ((currentLine = reader.readLine()) != null)
            {
                parseLines();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("End of file reached");
    }
    
    private void parseLines()
    {
        System.out.println(currentLine);
        lineArray = currentLine.split(":");
        String category = lineArray[0].toLowerCase();
        switch (category)
        {
            case "postcode":
                postcodes.add(new Postcode(lineArray[1]));
                break;
            case "restaurant":
                parseRestaurant(lineArray[1], lineArray[2]);
                break;
            case "supplier":
                parseSupplier(lineArray[1], lineArray[2]);
                break;
            case "ingredient":
                parseIngredient(lineArray[1], lineArray[2], lineArray[3],
                        lineArray[4], lineArray[5], lineArray[6]);
                break;
            case "dish":
                parseDish(lineArray);
                break;
            case "user":
                parseUser(lineArray[1], lineArray[2], lineArray[3], lineArray[4]);
                break;
            case "staff":
                staff.add(new Staff(lineArray[1]));
                break;
            case "drone":
                drones.add(new Drone((Number) Float.parseFloat(lineArray[1])));
                break;
            case "order":
                parseOrder(lineArray);
                break;
            case "stock":
                parseStock(lineArray);
                break;
        }
    }
    private void parseStock(String[] lineArray)
    {
        String itemStr = lineArray[1];
        Integer stockCount = Integer.parseInt(lineArray[2]);
        
        if(findDish(itemStr)!= null)
        {
            System.out.println("Restocking dish: " + itemStr + " by amount: " + stockCount );
        }
        else if(findIngredient(itemStr)!= null)
        {
            System.out.println("Restocking ingredient: " + itemStr + " by amount: " + stockCount );
        }
        //TODO: implement stocking from cfg
    }
    
    private void parseOrder(String[] lineArray)
    {
        String userStr = lineArray[1];
        String[] dishesStr = lineArray[2].split(",");
        Order thisOrder = new Order();
        for (User user : users)
        {
            if (user.getName().equals(userStr))
            {
                thisOrder = new Order(user);
            }
        }
    
        //add dishes
        for (String dishSplit : dishesStr)
        {
            Integer count = Integer.parseInt(dishSplit.split("\\*")[0].trim());
            String dishStr = dishSplit.split("\\*")[1].trim();
    
            thisOrder.addDish(findDish(dishStr), count);
        }
        orders.add(thisOrder);
    }
    
    private void parseUser(String name, String password, String address, String postcodeStr)
    {
        users.add(new User(name, password, address, findPostcode(postcodeStr)));
    }
    
    private void parseDish(String[] lineArray)
    {
        String name = lineArray[1];
        String desc = lineArray[2];
        Float price = Float.parseFloat(lineArray[3]);
        Float restockT = Float.parseFloat(lineArray[4]);
        Float restockA = Float.parseFloat(lineArray[5]);
        String[] items = lineArray[6].split(",");
        HashMap<Ingredient, Number> recipe = new HashMap<>();
        
        Dish thisDish = new Dish(name, desc, price, restockT, restockA);
        
        //add recipe
        for (String item : items)
        {
            String itemStr = item.split("\\*")[1].trim();
            Integer itemCount = Integer.parseInt(item.split("\\*")[0].trim());
            
            for (Ingredient ingredient : ingredients)
            {
                if (ingredient.getName().equals(itemStr))
                {
                    recipe.put(ingredient, (Number) itemCount);
                }
            }
        }
        thisDish.setRecipe(recipe);
        dishes.add(thisDish);
        
    }
    
    private void parseIngredient(String name, String unit, String supplierstr,
                                 String restockT, String restockA, String weight)
    {
        
        for (Supplier supplier : suppliers)
        {
            if (supplier.getName().equals(supplierstr))
            {
                ingredients.add(new Ingredient(name, unit, supplier, Integer.parseInt(restockT), Integer.parseInt(restockA), Integer.parseInt(weight)));
            }
        }
    }
    
    private void parseSupplier(String name, String postcodeStr)
    {
        for (Postcode postcode : postcodes)
        {
            if (postcode.getName().equals(postcodeStr))
            {
                suppliers.add(new Supplier(name, postcode));
            }
        }
    }
    
    private void parseRestaurant(String name, String postcodeStr)
    {
        restaurant = new Restaurant(name, findPostcode(postcodeStr));
    }
    
    private Ingredient findIngredient(String ingredientStr)
    {
        for (Ingredient ingredient : ingredients)
        {
            if (ingredient.getName().equals(ingredientStr))
            {
                return ingredient;
            }
        }
        return null;
    }
    
    private Dish findDish(String dishStr)
    {
        for (Dish dish : dishes)
        {
            if (dish.getName().equals(dishStr))
            {
                return dish;
            }
        }
        return null;
    }
    
    private Postcode findPostcode(String postcodeStr)
    {
        for (Postcode postcode : postcodes)
        {
            if (postcode.getName().equals(postcodeStr))
            {
                return postcode;
            }
        }
        return null;
    }
    private Restaurant restaurant;
    
    public Restaurant getRestaurant()
    {
        return restaurant;
    }
    
    public ArrayList<Dish> getDishes()
    {
        return dishes;
    }
    
    public ArrayList<Drone> getDrones()
    {
        return drones;
    }
    
    public ArrayList<Ingredient> getIngredients()
    {
        return ingredients;
    }
    
    public ArrayList<Order> getOrders()
    {
        return orders;
    }
    
    public ArrayList<Staff> getStaff()
    {
        return staff;
    }
    
    public ArrayList<Supplier> getSuppliers()
    {
        return suppliers;
    }
    
    public ArrayList<User> getUsers()
    {
        return users;
    }
    
    public ArrayList<Postcode> getPostcodes()
    {
        return postcodes;
    }
    
    
}
