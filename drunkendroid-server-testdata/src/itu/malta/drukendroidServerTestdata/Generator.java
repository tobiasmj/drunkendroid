package itu.malta.drukendroidServerTestdata;

import itu.dd.server.control.Repository;
import itu.dd.server.domain.MoodEvent;
import itu.dd.server.domain.Moodmap;
import itu.dd.server.domain.Trip;
import itu.dd.server.tech.DatabaseConnection;

import java.sql.SQLException;
import java.util.Random;


public class Generator {
	private double latMax, latMin;
	private double longMax, longMin;

	private int startTimeStamp, endTimeStamp, trips, moodReadingsPrTrip;
	public Generator(double ULCLat, double ULCLong, double LRCLat, double LRCLong, int startTimeStamp, int endTimeStamp, int trips, int moodReadingsPrTrip){
		this.startTimeStamp = startTimeStamp;
		this.endTimeStamp = endTimeStamp;
		this.trips = trips;
		this.moodReadingsPrTrip = moodReadingsPrTrip;
		
		if(ULCLat > LRCLat) {
			latMax = ULCLat;
			latMin = LRCLat;
		} else {
			latMax = LRCLat;
			latMin = ULCLat;
		}
		if(ULCLong > LRCLong) {
			longMax = ULCLong;
			longMin = LRCLong;
		} else {
			longMax = LRCLong;
			longMin = ULCLong;
		}
		
		generateTrips();
	}
	private void generateTrips() {
		Random generator = new Random();
		 Trip tripComm = null;
		 for(int i = 0; i < trips; i++) {
			 tripComm = new Trip("987654132",(long)startTimeStamp,(long)endTimeStamp,"Test data");

			 MoodEvent insMood; 
			 for (int k = 0; k < moodReadingsPrTrip; k++) {
				 long moodTimeStamp = generator.nextInt(endTimeStamp-startTimeStamp) + startTimeStamp;
				 double latitude = generator.nextDouble()*(latMax-latMin)+latMin;
				 double longitude = generator.nextDouble()*(longMax-longMin)+longMin;
				 int mood = generator.nextInt(255);
				 insMood = new MoodEvent(moodTimeStamp, latitude, longitude, mood);
				 tripComm.addEvent(insMood);
			 }

			 try {

				Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
				 rep.insertTrip(tripComm);
			 } catch(SQLException se) {
				 se.printStackTrace(); 
			 }
		 }
	}
}
