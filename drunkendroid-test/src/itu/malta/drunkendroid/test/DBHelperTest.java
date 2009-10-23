package itu.malta.drunkendroid.test;

import java.util.ArrayList;
import java.util.Calendar;

import itu.malta.drunkendroid.dal.entities.NewTrip;
import itu.malta.drunkendroid.dal.entities.OldTrip;
import itu.malta.drunkendroid.dal.entities.Trip;
import itu.malta.drunkendroid.tech.DBHelper;
import android.test.AndroidTestCase;

public class DBHelperTest extends AndroidTestCase {
	DBHelper dbh;

	protected void setUp(){
		 dbh = new DBHelper(this.getContext());
	}
	
	protected void tearDown(){
			dbh.cleanup();
	}
	
	private void insertTestData(){
		NewTrip testTrip = new NewTrip();
		// Reading 1
		NewTrip.NewReading r1 = testTrip.newReading();
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(1255816133);
		r1.setDate(c1);
		r1.setLatitude(35.908422);
		r1.setLongitude(14.502362);
		r1.setMood((short)110);
		// Reading 2
		NewTrip.NewReading r2 = testTrip.newReading();
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(1255816433);
		r2.setDate(c1);
		r2.setLatitude(35.909141);
		r2.setLongitude(14.503580);
		r2.setMood((short)95);
		// Reading 3
		NewTrip.NewReading r3 = testTrip.newReading();
		Calendar c3 = Calendar.getInstance();
		c3.setTimeInMillis(1255816733);
		r3.setDate(c1);
		r3.setLatitude(35.909275);
		r3.setLongitude(14.502825);
		r3.setMood((short)62);
		// Add the readings to the trip
		testTrip.AddReading(r1);
		testTrip.AddReading(r2);
		testTrip.AddReading(r3);
		dbh.insert(testTrip);
	}
	
	public void testInsertTrip(){
		try{
			this.insertTestData();
		}
		catch(Exception e){
			assertFalse(true);
		}
		assertTrue(true);
	}
	
	public void testSelectTripByStartDate(){
		try{
			final long startDate = 1255816133;
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(startDate);
			
			OldTrip returnedTrip;
			returnedTrip = dbh.selectTripByStartDate(c);
			
			assertEquals(returnedTrip.getTripReadings().size(), 3);
			assertEquals(startDate, returnedTrip.getStartDate().getTimeInMillis());
		}
		finally{
			dbh.flushDatabase();
		}
		
	}
	
	public void testGetAllLocalTrips(){
		ArrayList<Trip> trips = dbh.selectAllTrips();
		assertEquals(trips.size(), 2);
		
	}
}
