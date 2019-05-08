package comp1206.sushi.common;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import comp1206.sushi.common.Postcode;

public class Postcode extends Model implements Serializable
{

	private String name;
	private Map<String,Double> latLong;
	private Number distance;

	public Postcode(String code) {
		this.name = code;
		calculateLatLong();
		this.distance = Integer.valueOf(0);
	}
	

	
	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Number getDistance() {
		return this.distance;
	}

	public Map<String,Double> getLatLong() {
		this.calculateLatLong();
		return this.latLong;
	}
	
	public void calculateDistance(Restaurant restaurant)
	{
		Postcode destination = restaurant.getLocation();
		HashMap<String,Double> restaurantMap = getLatLongFromURL(destination);
		
		this.distance = calculateDistance(latLong.get("lat"),restaurantMap.get("lat"),latLong.get("long"),restaurantMap.get("long")).intValue();
	}
	public int getDistanceFrom(Postcode postcode)
	{
		HashMap<String,Double> destinationMap = new HashMap<>(postcode.getLatLong());
		
		return calculateDistance(latLong.get("lat"),destinationMap.get("lat"),latLong.get("long"),destinationMap.get("long")).intValue();
	}
	
	protected void calculateLatLong()
	{
		//This function needs implementing
		this.latLong = getLatLongFromURL(this);
	}
	
	public HashMap<String,Double> getLatLongFromURL(Postcode postcode)
	{
		String postString = postcode.getName().replaceAll("\\s","");
		HashMap<String,Double> map = new HashMap();
		try
		{
			
			URL url = new URL("https://www.southampton.ac.uk/~ob1a12/postcode/postcode.php?postcode="+postString);
			
			Scanner scanner = new Scanner(url.openStream());
			while (scanner.hasNext())
			{
				String inputString = scanner.next();
				if(inputString.contains("lat"))
				{
					String laT;
					String lonG;
					
					laT = inputString.substring(inputString.lastIndexOf("lat")+6,inputString.indexOf("long")-3);
					lonG = inputString.substring(inputString.lastIndexOf("long")+7,inputString.length()-2);
					
					map.put("lat",Double.valueOf(laT));
					map.put("long",Double.valueOf(lonG));
					
					return map;
				}
			}
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Taken from: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 *
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * @returns Distance in Meters
	 */
	public Double calculateDistance(double lat1, double lat2, double lon1,
								  double lon2) {
		
		final int R = 6371; // Radius of the earth
		
		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		Double distance = R * c * 1000; // convert to meters
		
		distance = Math.pow(distance, 2);
		
		return Math.sqrt(distance);
	}
}
