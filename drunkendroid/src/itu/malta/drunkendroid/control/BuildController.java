package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.dal.entities.Trip;

public class BuildController {

	private Trip tripInstance;
	
	public void addReading(int mood, double longitude, double latitude)
	{
		/** TODO Add a reading to the current trip
		 * 
		 */
	}
	
	public Trip getTripInstance()
	{
		/** TODO Create a singleton method to get the current tripInstance
		if(tripInstance == null)
			tripInstance = new Trip();
		*/
		return tripInstance;
	}
	
	public void closeTrip(Trip trip)
	{
		/** TODO Close the current trip
		 * 
		 */
	}
	
}