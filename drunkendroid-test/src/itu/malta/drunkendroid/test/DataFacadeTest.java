package itu.malta.drunkendroid.test;

import itu.malta.drunkendroid.control.DataFacade;
import itu.malta.drunkendroid.control.IDataFacade;
import itu.malta.drunkendroid.control.ILocalDataFacade;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.*;
import android.test.AndroidTestCase;

public class DataFacadeTest extends AndroidTestCase {
	ILocalDataFacade dbh;
	IDataFacade data;
	
	protected void setUp(){
		 dbh = new LocalDataFacadeForSQLite(this.getContext());
		 data = new DataFacade(this.getContext());
	}
	
	protected void tearDown(){
		data.closeFacade();
		dbh.closeFacade();
	}
	
	/**
	 * We insert two trips which should both be active, then retrieve and close both.
	 * 
	 */
	public void testGetActiveTrip(){
		try{
			//Setup
			this.insertTestData();
			//Build
			Trip testTrip1 = data.getActiveTrip();
			data.closeTrip(testTrip1); 
			Trip testTrip2 = data.getActiveTrip();
			data.closeTrip(testTrip2); //It seems this is already closed.
			Trip nullTrip = data.getActiveTrip();
			//Verify
			assertNull(nullTrip);
			assertTrue(
					testTrip1.getStartDate() 
					!= 
					testTrip2.getStartDate());
		}
		finally{
			this.flushDB();
		}
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
		//dbh.closeTrip(t);
		
		// ReadingEvent 1
		Trip t2 = dbh.startTrip();
		t2.setDateInMilliSec(1255816733);
		ReadingEvent r2_1 = new ReadingEvent(new Long(1255816733), (Double)35.908422, (Double)14.502362, 270);
		dbh.addEvent(t2, r2_1);
	}
}
