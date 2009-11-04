package itu.malta.drunkendroid.test;

import java.util.ArrayList;
import java.util.Calendar;

import itu.malta.drunkendroid.domain.TripRepository;
import itu.malta.drunkendroid.domain.entities.*;
import android.test.AndroidTestCase;

public class DBHelperTest extends AndroidTestCase {
	TripRepository dbh;

	protected void setUp(){
		 dbh = new TripRepository(this.getContext());
	}
	
	protected void tearDown(){
			dbh.cleanup();
	}
	
	private void insertTestData(){
		// Reading 1
		Reading r1 = new Reading();
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(1255816133);
		r1.setDate(c1);
		r1.setLatitude(35.908422);
		r1.setLongitude(14.502362);
		r1.setMood((short)110);
		// Reading 2
		Reading r2 = new Reading();
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(1255816433);
		r2.setDate(c1);
		r2.setLatitude(35.909141);
		r2.setLongitude(14.503580);
		r2.setMood((short)95);
		// Reading 3
		Reading r3 = new Reading();
		Calendar c3 = Calendar.getInstance();
		c3.setTimeInMillis(1255816733);
		r3.setDate(c1);
		r3.setLatitude(35.909275);
		r3.setLongitude(14.502825);
		r3.setMood((short)62);
		// Add the readings to the trip
		dbh.insert(r1);
		dbh.insert(r2);
		dbh.insert(r3);
		dbh.endTrip();
		
		// Reading 1
		Reading r2_1 = new Reading();
		Calendar c2_1 = Calendar.getInstance();
		c2_1.setTimeInMillis(1257815633);
		r2_1.setDate(c2_1);
		r2_1.setLatitude(35.908422);
		r2_1.setLongitude(14.502362);
		r2_1.setMood((short)270);
		dbh.insert(r2_1);
	}
	
	public void testInsertTrip(){
		this.insertTestData();
		assertTrue(true);
		dbh.flushDatabase();
	}
	
	public void testGetAllLocalTrips(){
		this.insertTestData();
		
		ArrayList<Trip> trips = dbh.getAllTrips();
		assertEquals(2, trips.size());
		
		dbh.flushDatabase();
	}
	
	public void testSelectTripByStartDate(){
		try{
			this.insertTestData();
			final long startDate = 1255816133;
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(startDate);
			
			Trip returnedTrip;
			returnedTrip = dbh.getTripByStartTime(c);
			
			assertEquals(3, returnedTrip.getTripReadings().size());
			assertEquals(startDate, returnedTrip.getStartDate().getTimeInMillis());
		}
		finally{
			dbh.flushDatabase();
		}
		
	}
}
