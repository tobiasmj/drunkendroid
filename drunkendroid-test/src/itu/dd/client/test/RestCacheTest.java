package itu.dd.client.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.DbHelper;
import itu.dd.client.tech.ILocalDataFacade;
import itu.dd.client.tech.IRestCache;
import itu.dd.client.tech.SqliteAdapter;
import itu.dd.client.tech.RestCache;
import itu.dd.client.tech.RestConnection;
import android.test.AndroidTestCase;

public class RestCacheTest extends AndroidTestCase {
	private static final String GETUPDATECANDIDATES = "getUpdateCandidates";
	private static final String SETTRIPPROS = "setTripProcessedAndUpdateForeignId";
	private static final String GETUPLOADCANDIDATES = "getUploadCandidates";
	private static final String SETEVENTPROS = "setEventProcessed";

	IRestCache cache = null;
	ILocalDataFacade _dbh = null;

	protected void setUp() {
		// Nothing yet
		_dbh = new SqliteAdapter(this.getContext());
	}

	protected void tearDown() {
		// Nothing yet
		_dbh = null;
	}

	private void flushDB() {
		DbHelper myHelper = DbHelper.getInstance(this.getContext());
		myHelper.flushDatabase();
	}

	private void insertTestData() {
		Trip t = _dbh.startTrip("Test trip");
		// MoodEvent 1
		MoodEvent r1 = new MoodEvent(new Long(1255816133),
				(Double) 35.908422, (Double) 14.502362, 110);
		// MoodEvent 2
		MoodEvent r2 = new MoodEvent(new Long(1255816433),
				(Double) 35.909141, (Double) 14.503580, 95);
		// MoodEvent 3
		MoodEvent r3 = new MoodEvent(new Long(1255816733),
				(Double) 35.909275, (Double) 14.502825, 62);
		_dbh.addEvent(t, r1);
		_dbh.addEvent(t, r2);
		_dbh.addEvent(t, r3);
		// dbh.closeTrip(t);

		// MoodEvent 1
		Trip t2 = _dbh.startTrip("Test trip");
		t2.setStartDate(1255816733L);
		MoodEvent r2_1 = new MoodEvent(new Long(1255816733),
				(Double) 35.908422, (Double) 14.502362, 270);
		_dbh.addEvent(t2, r2_1);
		MoodEvent r2_2 = new MoodEvent(new Long(1255816780),
				(Double) 35.808422, (Double) 15.502362, 270);
		_dbh.addEvent(t2, r2_2);
	}

	public void testSetProcessedWithUploadCalls() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		insertTestData();
		
		cache = new RestCache(mContext, new RestConnection());
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
			Trip t =_dbh.getTrip(candidates.get(0).getStartDate());
			
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
		
		cache = new RestCache(mContext, new RestConnection());
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
			int originalSize = t_real.getEvents().size();
			Event e = t_real.getEvents().get(0);
			setEventProcessed.invoke(cache, e);
			
			candiTrips = (List<Trip>) getUpdateCandidates.invoke(cache, null);
			t_real = candiTrips.get(0);
			int newSize = t_real.getEvents().size();
			
			//verify
			assertEquals(originalSize, newSize+1);
		} finally {
			flushDB();
			cache = null;
		}
	}
}
