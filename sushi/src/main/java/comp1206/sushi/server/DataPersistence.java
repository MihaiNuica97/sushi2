package comp1206.sushi.server;

import comp1206.sushi.common.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class DataPersistence
{
    private Server server;
    private File file;
    BufferedWriter writer;
    public DataPersistence(Server server)
    {
        this.server = server;
        file = new File("ServerState.txt");

        try
        {
            writer = new BufferedWriter(new FileWriter("ServerState.txt"));
            if (file.createNewFile())
            {
                System.out.println("File is created!");
            }
            else
            {
                System.out.println("File already exists.");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void saveState()
    {
        try
        {
            file = new File("ServerState.txt");
    
            try
            {
                writer = new BufferedWriter(new FileWriter("ServerState.txt"));
                file.createNewFile();
                
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            //RESTAURANT
            if(server.getRestaurant()!=null)
            {
                writeLine("Postcode", server.getRestaurantPostcode());
                writer.newLine();
                writeLine("Restaurant", server.getRestaurant());
                writer.newLine();
            }
            
            //POSTCODES
            if(!server.getPostcodes().isEmpty())
            {
                for (Postcode postcode : server.getPostcodes())
                {
                    if (!postcode.getName().equals(server.getRestaurantPostcode().getName()))
                    {
                        writeLine("Postcode", postcode);
                    }
                }
                writer.newLine();
            }
            
            //SUPPLIERS
            if(!server.getSuppliers().isEmpty())
            {
                for (Supplier supplier : server.getSuppliers())
                {
                    writeLine("Supplier", supplier);
                }
                writer.newLine();
            }
            
            //INGREDIENTS
            if(!server.getIngredients().isEmpty())
            {
                for (Ingredient ingredient : server.getIngredients())
                {
                    writeLine("Ingredient", ingredient);
                }
                writer.newLine();
            }
            
            //DISHES
            if(!server.getDishes().isEmpty())
            {
                for (Dish dish : server.getDishes())
                {
                    writeLine("Dish", dish);
                }
                writer.newLine();
            }
            //USERS
            if(!server.getUsers().isEmpty())
            {
                for (User user : server.getUsers())
                {
                    writeLine("User", user);
                }
                writer.newLine();
            }
            //ORDERS
            if(!server.getOrders().isEmpty())
            {
                for (Order order : server.getOrders())
                {
                    writeLine("Order", order);
                }
                writer.newLine();
            }
            //STOCK
            StockManagement stock = server.getStockManagement();
            
            
            HashMap<Ingredient,Number> ingredientStock = stock.getIngredientStock();
            HashMap<Dish,Number> dishStock = stock.getDishStock();
            
            if(!ingredientStock.isEmpty())
            {
                writeIngredientStockLine(ingredientStock);
            }
            
            if(!dishStock.isEmpty())
            {
                writeDishStockLine(dishStock);
            }
            //STAFF
            if(!server.getStaff().isEmpty())
            {
                for (Staff staff : server.getStaff())
                {
                    writeLine("Staff", staff);
                }
                writer.newLine();
            }
            //DRONES
            if(!server.getDrones().isEmpty())
            {
                for (Drone drone : server.getDrones())
                {
                    writeLine("Drone", drone);
                }
            }
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
    }
    
    private void writeIngredientStockLine(HashMap<Ingredient,Number> map)throws IOException
    {
        String finalLine = "";
        for(Ingredient ingredient: map.keySet())
        {
            finalLine = "STOCK:"+ingredient.getName()
                    +":"+map.get(ingredient).intValue();
            writer.write(finalLine);
            writer.newLine();
        }
        writer.newLine();
    }
    
    private void writeDishStockLine(HashMap<Dish,Number> map) throws IOException
    {
        String finalLine = "";
        for(Dish dish: map.keySet())
        {
            finalLine = "STOCK:"+dish.getName()
                    +":"+map.get(dish).intValue();
            writer.write(finalLine);
            writer.newLine();
        }
        writer.newLine();
    
    }

    
    private void writeLine(String lineType, Model model)throws IOException
    {
        String finalLine = "";
        
        switch (lineType)
        {
            case"Postcode":
                finalLine = "POSTCODE:" + model.getName();
                break;
                
            case"Restaurant":
                finalLine = "RESTAURANT:" +
                        model.getName() + ":" +
                        ((Restaurant) model).getLocation();
                break;
                
            case"Supplier":
                finalLine = "SUPPLIER:"
                        + model.getName()+":"
                        + ((Supplier) model).getPostcode();
                break;
    
            case"Ingredient":
                finalLine = "INGREDIENT:"
                        + model.getName()+":"
                        + ((Ingredient) model).getUnit()+":"
                        + ((Ingredient) model).getSupplier().getName()+":"
                        + ((Ingredient) model).getRestockThreshold()+":"
                        + ((Ingredient) model).getRestockAmount()+":"
                        + ((Ingredient) model).getWeight();
                break;
                
            case"Dish":
                finalLine = "DISH:"
                        + model.getName()+":"
                        + ((Dish) model).getDescription()+":"
                        + ((Dish) model).getPrice().intValue()+":"
                        + ((Dish) model).getRestockThreshold().intValue()+":"
                        + ((Dish) model).getRestockAmount().intValue()+":";
                for(Ingredient ingredient:((Dish) model).getRecipe().keySet())
                {
                    finalLine = finalLine.concat(((Dish) model).getRecipe().get(ingredient).intValue()
                            +" * "+
                            ingredient.getName()+",");
                }
                //delete last comma
                finalLine = finalLine.substring(0,finalLine.length()-1);
                break;
                
            case"User":
                finalLine = "USER:"
                        +model.getName()+":"
                        +((User) model).getPassword()+":"
                        +((User) model).getAddress()+":"
                        +((User) model).getPostcode();
                break;
                
            case"Staff":
                finalLine = "STAFF:"
                        +model.getName();
                break;
                
            case"Drone":
                finalLine = "DRONE:"
                        +((Drone) model).getSpeed().intValue();
                break;
                
            case"Order":
                finalLine = "ORDER:"
                        +((Order) model).getUser()+":";
                for(Dish dish:((Order) model).getOrder().keySet())
                {
                    finalLine = finalLine.concat(((Order) model).getOrder().get(dish).intValue()
                                                +" * "+dish.getName()+",");
                }
                finalLine = finalLine.substring(0,finalLine.length()-1);
                break;
        }
        
        
        writer.write(finalLine);
        writer.newLine();
    }
    
}
