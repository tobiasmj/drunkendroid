package itu.malta.drunkendroid.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import itu.malta.drunkendroid.control.ILocalDataFacade;
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.DBHelper;
import itu.malta.drunkendroid.tech.IRESTCache;
import itu.malta.drunkendroid.tech.LocalDataFacadeForSQLite;
import itu.malta.drunkendroid.tech.RESTCache;
import itu.malta.drunkendroid.tech.WebserviceConnectionREST;
import android.test.AndroidTestCase;

public class RESTCacheTest extends AndroidTestCase {
	private static final String GETUPDATECANDIDATES = "getUpdateCandidates";
	private static final String SETTRIPPROS = "setTripProcessedAndUpdateForeignId";
	private static final String GETUPLOADCANDIDATES = "getUploadCandidates";
	private static final String SETEVENTPROS = "setEventProcessed";

	IRESTCache cache = null;
	ILocalDataFacade _dbh = null;

	protected void setUp() {
		// Nothing yet
		_dbh = new LocalDataFacadeForSQLite(this.getContext());
	}

	protected void tearDown() {
		// Nothing yet
		_dbh = null;
	}

	private void flushDB() {
		DBHelper myHelper = DBHelper.getInstance(this.getContext());
		myHelper.flushDatabase();
	}

	private void insertTestData() {
		Trip t = _dbh.startTrip();
		// ReadingEvent 1
		ReadingEvent r1 = new ReadingEvent(new Long(1255816133),
				(Double) 35.908422, (Double) 14.502362, 110);
		// ReadingEvent 2
		ReadingEvent r2 = new ReadingEvent(new Long(1255816433),
				(Double) 35.909141, (Double) 14.503580, 95);
		// ReadingEvent 3
		ReadingEvent r3 = new ReadingEvent(new Long(1255816733),
				(Double) 35.909275, (Double) 14.502825, 62);
		_dbh.addEvent(t, r1);
		_dbh.addEvent(t, r2);
		_dbh.addEvent(t, r3);
		// dbh.closeTrip(t);

		// ReadingEvent 1
		Trip t2 = _dbh.startTrip();
		t2.startDate = 1255816733L;
		ReadingEvent r2_1 = new ReadingEvent(new Long(1255816733),
				(Double) 35.908422, (Double) 14.502362, 270);
		_dbh.addEvent(t2, r2_1);
		ReadingEvent r2_2 = new ReadingEvent(new Long(1255816780),
				(Double) 35.808422, (Double) 15.502362, 270);
		_dbh.addEvent(t2, r2_2);
	}

	public void testSetProcessedWithUploadCalls() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		insertTestData();
		
		cache = new RESTCache(mContext, new WebserviceConnectionREST());
		//Make some methods available using reflection
		Method getUploadCandidates = cache.getClass().getDeclaredMethod(GETUPLOADCANDIDATES);
		getUploadCandidates.setAccessible(true);
		
		Method setTripProcessed = cache.getClass().getDeclaredMethod(SETTRIPPROS, Trip.class);
		setTripProcessed.setAccessible(true);
		
		
		try {
			// Build it up!
			List<Trip> candidates = (List<Trip>) getUploadCandidates.invoke(cache, null);
			
			//Verify mid way
			assertEquals(2, candidates.size());
			//Build
			//Get a trip.
			Trip t =_dbh.getTrip(candidates.get(0).startDate);
			
			//Execute
			setTripProcessed.invoke(cache, t);
			
			//Verify
			candidates = (List<Trip>) getUploadCandidates.invoke(cache, null);
			assertEquals(1, candidates.size());
			

		} finally {
			flushDB();
			cache = null;
		}
	}
	
	public void testSetProcessedWithUpdateCalls() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		insertTestData();
		
		cache = new RESTCache(mContext, new WebserviceConnectionREST());
		//Make some methods available using reflection
		
		/**
		 * We use the upload candidates method since none of the trips in the test have been processed.
		 */
		Method getUploadCandidates = cache.getClass().getDeclaredMethod(GETUPLOADCANDIDATES);
		getUploadCandidates.setAccessible(true);
		
		Method getUpdateCandidates = cache.getClass().getDeclaredMethod(GETUPDATECANDIDATES);
		getUpdateCandidates.setAccessible(true);
			
		Method setEventProcessed = cache.getClass().getDeclaredMethod(SETEVENTPROS, Event.class);
		setEventProcessed.setAccessible(true);
		
		Method setTripProcessed = cache.getClass().getDeclaredMethod(SETTRIPPROS, Trip.class);
		setTripProcessed.setAccessible(true);
		
		try {
			// Build it up!
			List<Trip> candidates = (List<Trip>) getUploadCandidates.invoke(cache, null);
			Trip t = candidates.get(0);
			//Process it, in order to let get Update candidates find it.
			setTripProcessed.invoke(cache, t);
			//Verify that this trick has worked out.
			candidates = (List<Trip>) getUploadCandidates.invoke(cache, null);
			assertEquals(1, candidates.size());
			
			//Build it up for real.
			//These trips should be set to online
			List<Trip> candiTrips = (List<Trip>) getUpdateCandidates.invoke(cache, null);
			Trip t_real = candiTrips.get(0);
			int originalSize = t_real.events.size();
			Event e = t_real.events.get(0);
			setEventProcessed.invoke(cache, e);
			
			candiTrips = (List<Trip>) getUpdateCandidates.invoke(cache, null);
			t_real = candiTrips.get(0);
			int newSize = t_real.events.size();
			
			//verify
			assertEquals(originalSize, newSize+1);
		} finally {
			flushDB();
			cache = null;
		}
	}
}
