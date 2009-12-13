package itu.dd.server.test;


import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Assert;
import itu.dd.server.control.Repository;
import itu.dd.server.domain.CallEvent;
import itu.dd.server.domain.GridCell;
import itu.dd.server.domain.LocationEvent;
import itu.dd.server.domain.MoodEvent;
import itu.dd.server.domain.Moodmap;
import itu.dd.server.domain.SmsEvent;
import itu.dd.server.domain.Trip;
import itu.dd.server.interfaces.IEvent;
import itu.dd.server.mock.MockCalculateMoodMapDatabaseConnection;
import itu.dd.server.mock.MockGetTripDatabaseConnection;
import itu.dd.server.mock.MockInsertCallDatabaseConnection;
import itu.dd.server.mock.MockInsertLocationDatabaseConnection;
import itu.dd.server.mock.MockInsertMoodDatabaseConnection;
import itu.dd.server.mock.MockInsertSmsDatabaseConnection;
import itu.dd.server.mock.MockInsertTripDatabaseConnection;
import itu.dd.server.mock.MockUpdateTripDatabaseConnection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class RepositoryTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	/**
	 * Test method for insertSms	
	 */
	@Test
	public void testInsertSms() throws SQLException {
		Repository rep = new Repository(MockInsertSmsDatabaseConnection.getInstance().getConn());
		SmsEvent sms = new SmsEvent(1,10,10,"004551883250", "004551883250","test message");
		sms.setTripId(1);
		rep.insertSms(sms);
	}
	/**
	 * Test method for insertCall	
	 */
	@Test
	public void testInsertCall() throws SQLException {
		Repository rep = new Repository(MockInsertCallDatabaseConnection.getInstance().getConn());
		CallEvent call = new CallEvent(1,10,10,"004551883250", "004551883250",2);
		call.setTripId(1);
		rep.insertCall(call);
	}

	/**
	 * Test method for insertLocation	
	 */
	@Test
	public void testInsertLocation() throws SQLException {
		Repository rep = new Repository(MockInsertLocationDatabaseConnection.getInstance().getConn());
		LocationEvent location = new LocationEvent(1,10,10);
		location.setTripId(1);
		rep.insertLocation(location);
	}
	/**
	 * Test method for insertMood
	 * @throws SQLException 
	 */
	@Test
	public void testInsertMood() throws SQLException {
		Repository rep = new Repository(MockInsertMoodDatabaseConnection.getInstance().getConn());
		MoodEvent mood = new MoodEvent(1, 10, 10, 100);
		mood.setTripId(1);
		rep.insertMood(mood);
	}
	/**
	 * Test method for getMoodMap
	 * @throws SQLException 
	 */
	@Test
	public void testGetMoodMap() throws SQLException {
		Repository rep = new Repository(MockCalculateMoodMapDatabaseConnection.getInstance().getConn());
		Moodmap mm = new Moodmap(1L,10L,5.0D,5.0D,10.0D,10.0D,60,60);
		GridCell[][] gc = rep.calculateMoodMap(mm);

		Assert.assertTrue(GridCell.class.isInstance(gc[0][0]));
		Assert.assertTrue(GridCell.class.isInstance(gc[59][59]));

	}

	/**
	 * Test method for inserting a trip
	 * */
	@Test
	public void testInsert() throws SQLException {
		Repository rep = new Repository(MockInsertTripDatabaseConnection.getInstance().getConn());
		Trip it = new Trip("111111",(long)123456,(long)123456,"Test trip");
		Assert.assertEquals((long) 42, rep.insertTrip(it));
	}

	/**
	 * Test method for updating a trip
	 */
	@Test
	public void testUpdate() throws SQLException{
		Repository rep = new Repository(MockUpdateTripDatabaseConnection.getInstance().getConn());
		Trip it = new Trip("1",(long)1,(long)2,"testTrip");
		it.setTripId(1);
		rep.updateTrip(it);
		//Assert.assertTrue(true);			
	}
	/**
	 * Test method for getting a trip 
	 * @throws IOException
	 * @throws SQLException
	 */
	@Test
	public void testGet() throws SQLException, IOException {
		Repository rep = new Repository(MockGetTripDatabaseConnection.getInstance().getConn());
		Trip it = new Trip(1);
		it = rep.getTrip(it);

		Trip eTrip = new Trip(1L, 2L, "testTrip");
		eTrip.setTripId(1);

		MoodEvent eMood = new MoodEvent(1L,1D,2D,1);
		LocationEvent eLocation = new LocationEvent(1L,1D,2D);
		CallEvent eCall = new CallEvent(1L,1D,2D,"004551883250","004551883250",2L);
		SmsEvent eSms = new SmsEvent(1L,1D,2D,"004551883250","004551883250","test message");

		Assert.assertEquals(eTrip.getStartTime(), it.getStartTime());
		Assert.assertEquals(eTrip.getEndTime(), it.getEndTime());
		Assert.assertEquals(eTrip.getName(), it.getName());
		Assert.assertEquals(eTrip.getTripId(), it.getTripId());
		while(eTrip.moreEvents()) {
			IEvent event = eTrip.getNextEvent();
			if(MoodEvent.class.isInstance(event)) {
				MoodEvent mEvent = (MoodEvent)event;
				Assert.assertEquals(eMood.getLatitude(), mEvent.getLatitude());				
				Assert.assertEquals(eMood.getLongitude(), mEvent.getLongitude());				
				Assert.assertEquals(eMood.getMood(), mEvent.getMood());				
				Assert.assertEquals(eMood.getTimeStamp(), mEvent.getTimeStamp());				

			} else if (LocationEvent.class.isInstance(event)) {
				LocationEvent lEvent = (LocationEvent)event;
				Assert.assertEquals(eLocation.getLatitude(), lEvent.getLatitude());				
				Assert.assertEquals(eLocation.getLongitude(), lEvent.getLongitude());				
				Assert.assertEquals(eLocation.getTimeStamp(), lEvent.getTimeStamp());				
			} else if (CallEvent.class.isInstance(event)) {
				CallEvent cEvent = (CallEvent)event;
				Assert.assertEquals(eCall.getLatitude(), cEvent.getLatitude());				
				Assert.assertEquals(eCall.getLongitude(), cEvent.getLongitude());				
				Assert.assertEquals(eCall.getTimeStamp(), cEvent.getTimeStamp());
				Assert.assertEquals(eCall.getCaller(), cEvent.getCaller());
				Assert.assertEquals(eCall.getreceiver(), cEvent.getreceiver());
				Assert.assertEquals(eCall.getEndTime(), cEvent.getEndTime());
			} else if (SmsEvent.class.isInstance(event)) {
				SmsEvent sEvent = (SmsEvent)event;
				Assert.assertEquals(eSms.getLatitude(), sEvent.getLatitude());				
				Assert.assertEquals(eSms.getLongitude(), sEvent.getLongitude());				
				Assert.assertEquals(eSms.getTimeStamp(), sEvent.getTimeStamp());
				Assert.assertEquals(eSms.getSender(), sEvent.getSender());
				Assert.assertEquals(eSms.getreceiver(), sEvent.getreceiver());
				Assert.assertEquals(eSms.getMessage(), sEvent.getMessage());	
			}
		}



	}
}
