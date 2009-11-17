package itu.malta.drunkendroid.test;

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
}
