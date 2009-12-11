package itu.dd.client.test;

import itu.dd.client.control.DataFacade;
import itu.dd.client.control.IDataFacade;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.*;
import android.test.AndroidTestCase;

public class DataFacadeTest extends AndroidTestCase {
	ILocalDataFacade dbh;
	IDataFacade data;

	protected void setUp() {
		dbh = new SqliteAdapter(this.getContext());
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
			assertTrue(testTrip1.getStartDate() != testTrip2.getStartDate());
		} finally {
			this.flushDB();
		}
	}

	private void flushDB() {
		DbHelper myHelper = DbHelper.getInstance(this.getContext());
		myHelper.flushDatabase();
	}

	private void insertTestData() {
		Trip t = dbh.startTrip("Test trip");
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
		Trip t2 = dbh.startTrip("Test trip");
		t2.setStartDate(1255816733L);
		MoodEvent r2_1 = new MoodEvent(new Long(1255816733),
				(Double) 35.908422, (Double) 14.502362, 270);
		dbh.addEvent(t2, r2_1);
	}
}
