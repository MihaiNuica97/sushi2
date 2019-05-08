package comp1206.sushi.common;

import comp1206.sushi.common.Drone;

public class Drone extends Model {

	private Number speed;
	private Number progress;
	
	private Number capacity;
	private Number battery;
	
	private String status;
	
	private Postcode source;
	private Postcode destination;

	public Drone(Number speed) {
		this.setSpeed(speed);
		this.setCapacity(1);
		this.setBattery(100);
		this.setStatus("Idle");
	}

	public Number getSpeed() {
		return speed;
	}

	
	public Number getProgress() {
		return progress;
	}
	
	public void setProgress(Number progress) {
		this.progress = progress;
	}
	
	public void setSpeed(Number speed) {
		this.speed = speed;
	}
	
	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	
	private void restock(StockManagement stockManagement,Ingredient ingredient)
    {
        
        new Trip(this,ingredient.getSupplier().getPostcode());
        new Trip(this,stockManagement.getServer().getRestaurantPostcode());
        
        stockManagement.restockIngredient(ingredient);
    }
    
    private void deliver(Order order)
    {
    
    }
    
    class Trip
    {
        
        public Trip(Drone drone, Postcode destination)
        {
            drone.setDestination(destination);
            drone.setStatus("En route to " + destination.getName());
            this.go(drone);
            drone.setStatus("Idle");
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            drone.setSource(destination);
        }
        
        private void go(Drone drone)
        {
            Postcode source = drone.getSource();
            Postcode destination = drone.getDestination();
            
            drone.setProgress(0);
            
            int distance = destination.getDistanceFrom(source);
            int progressNow = 0;
            while (progressNow < distance)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                progressNow += drone.getSpeed().intValue();
                System.out.println(drone.getName());
                System.out.println(distance);
                System.out.println(progressNow);
                System.out.println(drone.getProgress());
                System.out.println();
                
                drone.setProgress(progressNow*100/distance);
                notifyUpdate();
            }
            drone.setProgress(100);

    
        }
    }
    
	public void run(StockManagement stockManagement)
	{
		class DroneThread extends Thread
        {
            private DroneThread()
            {
            
            }
            
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    Ingredient toRestock = stockManagement.verifyIngredientsStock();
                    if(toRestock !=null)
                    {
                        System.out.println("Found ingredient " + toRestock.getName() + " needs restocking");
                        System.out.println();
                        restock(stockManagement,toRestock);
                    }
                    
                    //TODO IMPLEMENT ORDER DELIVERY
                    
                }
            }
        }
        DroneThread droneThread = new DroneThread();
        droneThread.start();
	}
	
}
