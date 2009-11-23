package itu.malta.drunkendroid.test;

import java.util.Calendar;
import java.util.List;
import itu.malta.drunkendroid.control.ILocalDataFacade;
import itu.malta.drunkendroid.domain.*;
import itu.malta.drunkendroid.tech.DBHelper;
import itu.malta.drunkendroid.tech.LocalDataFacadeForSQLite;
import android.test.AndroidTestCase;

public class LocalSQLTest extends AndroidTestCase {
	ILocalDataFacade dbh;

	protected void setUp(){
		 dbh = new LocalDataFacadeForSQLite(this.getContext());
	}
	
	protected void tearDown(){
			dbh.closeFacade();
	}
	
	private void flushDB(){
		DBHelper myHelper = DBHelper.getInstance(this.getContext());
		myHelper.flushDatabase();	
	}
	
	private void insertTestData(){
		Trip t = dbh.startTrip();
		// ReadingEvent 1
		ReadingEvent r1 = new ReadingEvent(new Long(1255816133), (Double)35.908422, (Double)14.502362, 110);
		// ReadingEvent 2
		ReadingEvent r2 = new ReadingEvent(new Long(1255816433), (Double)35.909141, (Double)14.503580, 95);
		// ReadingEvent 3	
		ReadingEvent r3 = new ReadingEvent(new Long(1255816733), (Double)35.909275, (Double)14.502825, 62);
		dbh.addEvent(t, r1);
		dbh.addEvent(t, r2);
		dbh.addEvent(t, r3);
		dbh.closeTrip(t);
		
		// ReadingEvent 1
		Trip t2 = dbh.startTrip();
		t2.setDateInMilliSec(1255816733);
		ReadingEvent r2_1 = new ReadingEvent(new Long(1255816733), (Double)35.908422, (Double)14.502362, 270);
		dbh.addEvent(t2, r2_1);
	}
	
	public void testInsertTrip(){
		this.insertTestData();
		assertTrue(true);
		this.flushDB();
	}
	
	public void testGetAllLocalTrips(){
		this.insertTestData();
		
		List<Trip> trips = dbh.getAllTrips();
		assertEquals(2, trips.size());
		
		this.flushDB();
	}
	
	public void testSelectTripByStartDate(){
		//new to get a list of trips first and then fetch one of thoose.. Since the local facade
		//will set the datetime itself, so I can't predict it.
		try{
			this.insertTestData();
			
			List<Trip> returnedTrips = dbh.getAllTrips();
			assertTrue(returnedTrips.size() > 0);
			Trip someTrip = returnedTrips.get(1);
			long someTripStartDate = someTrip.getStartDate().getTimeInMillis();
			
			Trip returnedTrip = dbh.getTrip(someTripStartDate);
			assertEquals(someTripStartDate, returnedTrip.getStartDate().getTimeInMillis());
		}
		finally{
			this.flushDB();
		}
		
	}
	
	public void testUpdateForeignId(){
		try{
			this.insertTestData();
			List<Trip> trips = dbh.getAllTrips();
			Trip testTrip = trips.get(1);
			Long foreignId = new Long(123456789);
			Long testTripStartDate = testTrip.getStartDate().getTimeInMillis();
			
			//Build
			testTrip.setRemoteID(foreignId);
			dbh.addRemoteIdToTrip(testTrip);
			testTrip = dbh.getTrip(testTripStartDate);
			
			//verify
			assertEquals(foreignId, testTrip.getRemoteID());
		}
		finally{
			this.flushDB();
		}
	}
	
	public void testGetCountFromTrip(){
		try{
			this.insertTestData();
			List<Trip> trips = dbh.getAllTrips();
			Trip testTrip = trips.get(1);
			
			Trip controlTrip = dbh.getTrip(testTrip.getStartDate().getTimeInMillis());
			int eventCount = controlTrip.getTripEvents().size();
			int testCount = dbh.getEventCount(testTrip);
			
			assertTrue(eventCount > 0);
			assertEquals(eventCount, testCount);
		}
		finally{
			this.flushDB();
		}
	}
	
	public void testGetActiveTrip(){
		try{
			
		}
		finally{
			this.flushDB();
		}
	}
	
	public void testWithoutLocation(){
		try{
			this.insertTestData();
			List<Trip> trips = dbh.getAllTrips();
			Trip testTrip = trips.get(1);
			int testTripStartCount = dbh.getEventCount(testTrip);
			//Create a few events without locations
			Long currentTime1 = Calendar.getInstance().getTimeInMillis();
			Long currentTime2 = currentTime1 + 10;
			Event e1 = new ReadingEvent(
					currentTime1, 
					null, 
					null,
					60);
			Event e2 = new LocationEvent(
					currentTime2,
					null,
					null);
			//Add them
			dbh.addEvent(testTrip, e1);
			dbh.addEvent(testTrip, e2);
			//Now update them.	
			Double latitude = 36.008165;
			Double longitude = 14.703580;
			List<Event> updatedEvents = dbh.updateEventsWithoutLocation(testTrip, latitude, longitude);
			
			//Build verifications objects.
			testTrip = dbh.getTrip(testTrip.getStartDate().getTimeInMillis());
			List<Event> testEvents = testTrip.getTripEvents();
			
			//Verify
			assertEquals(2, updatedEvents.size());
			assertEquals(testTripStartCount + 2, dbh.getEventCount(testTrip));
			for(Event e : testEvents){
				if(e.dateTime == currentTime1 || e.dateTime == currentTime2){
					assertEquals(latitude, e.latitude);
					assertEquals(longitude, e.longitude);
				}
			}
		}
		finally{
			this.flushDB();
		}
	}
}
