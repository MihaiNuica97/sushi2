package comp1206.sushi.common;

import comp1206.sushi.common.Staff;

import java.util.Random;

public class Staff extends Model {

	private String name;
	private String status;
	private Number fatigue;
	
	public Staff(String name) {
		this.setName(name);
		this.setFatigue(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getFatigue() {
		return fatigue;
	}

	public void setFatigue(Number fatigue) {
		this.fatigue = fatigue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	public void verifyStock(StockManagement stockManagement,Staff staff)
	{
		class StaffThread extends Thread
		{
			String name;
			private StaffThread()
			{
				name = staff.getName();
			}
			public void run()
			{
				while(true)
				{
					Random rand = new Random();
					if(rand.nextBoolean())
					{
						staff.setStatus("Working");
						notifyUpdate();
                        try
                        {
                            Thread.sleep(rand.nextInt(40001) + 2000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
						stockManagement.verifyDishStock();
                        this.run();
					}
					else
					{
						staff.setStatus("Idle");
						notifyUpdate();
					}
				}
			}
		}
		StaffThread staffThread = new StaffThread();
		staffThread.start();
		
	}

}
