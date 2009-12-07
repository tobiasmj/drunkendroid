package itu.malta.drunkendroid.test;

import itu.malta.drunkendroid.control.DataFacade;
import itu.malta.drunkendroid.control.IDataFacade;
import itu.malta.drunkendroid.control.ILocalDataFacade;
import itu.malta.drunkendroid.domain.MoodEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.*;
import android.test.AndroidTestCase;

public class DataFacadeTest extends AndroidTestCase {
	ILocalDataFacade dbh;
	IDataFacade data;

	protected void setUp() {
		dbh = new LocalDataFacadeForSQLite(this.getContext());
		data = new DataFacade(this.getContext());
	}

	protected void tearDown() {
		data.closeFacade();
		dbh.closeFacade();
	}

	/**
	 * We insert two trips which should both be active, then retrieve and close
	 * both.
	 * 
	 */
	public void testGetActiveTrip() {
		try {
			// Setup
			this.insertTestData();
			// Build
			Trip testTrip1 = data.getActiveTrip();
			data.closeTrip(testTrip1);
			Trip testTrip2 = data.getActiveTrip();
			data.closeTrip(testTrip2); // It seems this is already closed.
			Trip nullTrip = data.getActiveTrip();
			// Verify
			assertNull(nullTrip);
			assertTrue(testTrip1.startDate != testTrip2.startDate);
		} finally {
			this.flushDB();
		}
	}

	private void flushDB() {
		DBHelper myHelper = DBHelper.getInstance(this.getContext());
		myHelper.flushDatabase();
	}

	private void insertTestData() {
		Trip t = dbh.startTrip();
		// ReadingEvent 1
		MoodEvent r1 = new MoodEvent(new Long(1255816133),
				(Double) 35.908422, (Double) 14.502362, 110);
		// ReadingEvent 2
		MoodEvent r2 = new MoodEvent(new Long(1255816433),
				(Double) 35.909141, (Double) 14.503580, 95);
		// MoodEvent 3
		MoodEvent r3 = new MoodEvent(new Long(1255816733),
				(Double) 35.909275, (Double) 14.502825, 62);
		dbh.addEvent(t, r1);
		dbh.addEvent(t, r2);
		dbh.addEvent(t, r3);
		// dbh.closeTrip(t);

		// MoodEvent 1
		Trip t2 = dbh.startTrip();
		t2.startDate = 1255816733L;
		MoodEvent r2_1 = new MoodEvent(new Long(1255816733),
				(Double) 35.908422, (Double) 14.502362, 270);
		dbh.addEvent(t2, r2_1);
	}
}
