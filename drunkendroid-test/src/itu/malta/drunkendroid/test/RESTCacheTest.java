package itu.malta.drunkendroid.test;

import itu.malta.drunkendroid.control.ILocalDataFacade;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.DBHelper;
import itu.malta.drunkendroid.tech.IRESTCache;
import itu.malta.drunkendroid.tech.LocalDataFacadeForSQLite;
import android.test.AndroidTestCase;

public class RESTCacheTest extends AndroidTestCase {
	IRESTCache cache = null;
	ILocalDataFacade _dbh = new LocalDataFacadeForSQLite(mContext);

	protected void setUp() {
		// Nothing yet
	}

	protected void tearDown() {
		// Nothing yet
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
	}

	public void testInsertTrip() {
		this.insertTestData();
		assertTrue(true);
		this.flushDB();
	}

	public void testUPLOAD() {
		try {
			// Build it up!
			insertTestData();
			Trip t = _dbh.getTrip(1255816733L);

		} finally {
			flushDB();
		}
	}
}
