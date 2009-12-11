package itu.dd.client.test;

import java.util.Calendar;
import java.util.List;

import itu.dd.client.control.DataFacade;
import itu.dd.client.control.IDataFacade;
import itu.dd.client.domain.*;
import itu.dd.client.tech.DbHelper;
import itu.dd.client.tech.ILocalDataFacade;
import itu.dd.client.tech.SqliteAdapter;
import android.test.AndroidTestCase;

public class SqliteAdapterTest extends AndroidTestCase {
	ILocalDataFacade _dbh;

	protected void setUp() {
		_dbh = new SqliteAdapter(this.getContext());
	}

	protected void tearDown() {
		_dbh.closeFacade();
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
	}

	public void testInsertTrip() {
		this.insertTestData();
		assertTrue(true);
		this.flushDB();
	}

	public void testDeleteTrip() {
		try {
			this.insertTestData();
			Trip t = _dbh.getAllTrips().get(0);

			int count_before = _dbh.getAllTrips().size();
			_dbh.deleteTrip(t.getStartDate());
			int count_after = _dbh.getAllTrips().size();

			assertTrue((count_before - 1) == (count_after));
		} finally {
			this.flushDB();
		}
	}

	public void testGetAllLocalTrips() {
		this.insertTestData();

		List<Trip> trips = _dbh.getAllTrips();
		assertEquals(2, trips.size());

		this.flushDB();
	}

	public void testSelectTripByStartDate() {
		// new to get a list of trips first and then fetch one of thoose.. Since
		// the local facade
		// will set the datetime itself, so I can't predict it.
		try {
			this.insertTestData();

			List<Trip> returnedTrips = _dbh.getAllTrips();
			assertTrue(returnedTrips.size() > 0);
			Trip someTrip = returnedTrips.get(1);
			Long someTripStartDate = someTrip.getStartDate();

			Trip returnedTrip = _dbh.getTrip(someTripStartDate);
			assertEquals(someTripStartDate, returnedTrip.getStartDate());
		} finally {
			this.flushDB();
		}

	}

	public void testUpdateForeignId() {
		try {
			this.insertTestData();
			List<Trip> trips = _dbh.getAllTrips();
			Trip testTrip = trips.get(1);
			Long foreignId = new Long(123456789);
			Long testTripStartDate = testTrip.getStartDate();

			// Build
			testTrip.setRemoteId(foreignId);
			_dbh.addRemoteIdToTrip(testTrip);
			testTrip = _dbh.getTrip(testTripStartDate);

			// verify
			assertEquals(foreignId, testTrip.getRemoteId());
		} finally {
			this.flushDB();
		}
	}

	public void testGetCountFromTrip() {
		try {
			this.insertTestData();
			List<Trip> trips = _dbh.getAllTrips();
			Trip testTrip = trips.get(1);

			Trip controlTrip = _dbh.getTrip(testTrip.getStartDate());
			int eventCount = controlTrip.getEvents().size();
			int testCount = _dbh.getEventCount(testTrip);

			assertTrue(eventCount > 0);
			assertEquals(eventCount, testCount);
		} finally {
			this.flushDB();
		}
	}

	public void testGetActiveTripFromRepo() {
		IDataFacade data = new DataFacade(this.getContext());

		try {
			// Build
			this.insertTestData();

			// Execute
			Trip testTrip1 = data.getActiveTrip();
			data.closeTrip(testTrip1);
			// Verify
			assertNotNull(testTrip1);

			// Execute
			testTrip1 = data.getActiveTrip();
			data.closeTrip(testTrip1);
			// Verify
			assertNotNull(testTrip1);

			// Execute
			testTrip1 = data.getActiveTrip();
			// Verify
			assertNull(testTrip1);

		} finally {
			data.closeFacade();
		}
	}

	public void testGetActiveTripSizes() {
		try {
			// Build
			this.insertTestData();
			// Execute
			List<Trip> trips = _dbh.getActiveTrips();
			// Verify
			assertEquals(2, trips.size()); // from insertTestData

			// Execute
			_dbh.closeTrip(trips.get(0));
			trips = _dbh.getActiveTrips();
			// Verify
			assertEquals(1, trips.size());

			// Execute
			_dbh.closeTrip(trips.get(0));
			trips = _dbh.getActiveTrips();
			// Verify
			assertEquals(0, trips.size());
		} finally {
			this.flushDB();
		}
	}

	public void testWithoutLocation() {
		try {
			this.insertTestData();
			List<Trip> trips = _dbh.getAllTrips();
			Trip testTrip = trips.get(1);
			int testTripStartCount = _dbh.getEventCount(testTrip);
			// Create a few events without locations
			Long currentTime1 = Calendar.getInstance().getTimeInMillis();
			Long currentTime2 = currentTime1 + 10;
			Event e1 = new MoodEvent(currentTime1, null, null, 60);
			Event e2 = new LocationEvent(currentTime2, null, null);
			// Add them
			_dbh.addEvent(testTrip, e1);
			_dbh.addEvent(testTrip, e2);
			// Now update them.
			Double latitude = 36.008165;
			Double longitude = 14.703580;
			List<Event> updatedEvents = _dbh.updateEventsWithoutLocation(
					testTrip, latitude, longitude);

			// Build verifications objects.
			testTrip = _dbh.getTrip(testTrip.getStartDate());
			List<Event> testEvents = testTrip.getEvents();

			// Verify
			assertEquals(2, updatedEvents.size());
			assertEquals(testTripStartCount + 2, _dbh.getEventCount(testTrip));
			for (Event e : testEvents) {
				if (e.getDateTime() == currentTime1 || e.getDateTime() == currentTime2) {
					assertEquals(latitude, e.getLatitude());
					assertEquals(longitude, e.getLongitude());
				}
			}
		} finally {
			this.flushDB();
		}
	}
}
