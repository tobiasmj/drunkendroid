package itu.malta.drunkendroidserver.test;


import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Assert;
import itu.malta.drunkendroidserver.control.Repository;
import itu.malta.drunkendroidserver.domain.Call;
import itu.malta.drunkendroidserver.domain.GridCell;
import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.MoodMap;
import itu.malta.drunkendroidserver.domain.Reading;
import itu.malta.drunkendroidserver.domain.Sms;
import itu.malta.drunkendroidserver.domain.Trip;
import itu.malta.drunkendroidserver.interfaces.IEvent;
import itu.malta.drunkendroidserver.mock.MockCalculateMoodMapDatabaseConnection;
import itu.malta.drunkendroidserver.mock.MockGetTripDatabaseConnection;
import itu.malta.drunkendroidserver.mock.MockInsertCallDatabaseConnection;
import itu.malta.drunkendroidserver.mock.MockInsertLocationDatabaseConnection;
import itu.malta.drunkendroidserver.mock.MockInsertReadingDatabaseConnection;
import itu.malta.drunkendroidserver.mock.MockInsertSmsDatabaseConnection;
import itu.malta.drunkendroidserver.mock.MockInsertTripDatabaseConnection;
import itu.malta.drunkendroidserver.mock.MockUpdateTripDatabaseConnection;

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
		Sms sms = new Sms(1,10,10,"004551883250", "004551883250","test message");
		sms.setTripId(1);
		rep.insertSms(sms);
	}
	/**
	 * Test method for insertCall	
	 */
	@Test
	public void testInsertCall() throws SQLException {
		Repository rep = new Repository(MockInsertCallDatabaseConnection.getInstance().getConn());
		Call call = new Call(1,10,10,"004551883250", "004551883250",2);
		call.setTripId(1);
		rep.insertCall(call);
	}
	
	/**
	 * Test method for insertLocation	
	 */
	@Test
	public void testInsertLocation() throws SQLException {
		Repository rep = new Repository(MockInsertLocationDatabaseConnection.getInstance().getConn());
		Location location = new Location(1,10,10);
		location.setTripId(1);
		rep.insertLocation(location);
	}
	/**
	 * Test method for insertReading
	 * @throws SQLException 
	 */
	@Test
	public void testInsertReading() throws SQLException {
		Repository rep = new Repository(MockInsertReadingDatabaseConnection.getInstance().getConn());
		Reading reading = new Reading(1, 10, 10, 100);
		reading.setTripId(1);
		rep.insertReading(reading);
	}
	/**
	 * Test method for getMoodMap
	 * @throws SQLException 
	 */
	@Test
	public void testGetMoodMap() throws SQLException {
		Repository rep = new Repository(MockCalculateMoodMapDatabaseConnection.getInstance().getConn());
		MoodMap mm = new MoodMap(1L,10L,5.0D,5.0D,10.0D,10.0D);
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

		Reading eReading = new Reading(1L,1D,2D,1);
		Location eLocation = new Location(1L,1D,2D);
		Call eCall = new Call(1L,1D,2D,"004551883250","004551883250",2L);
		Sms eSms = new Sms(1L,1D,2D,"004551883250","004551883250","test message");
		
		Assert.assertEquals(eTrip.getStartTime(), it.getStartTime());
		Assert.assertEquals(eTrip.getEndTime(), it.getEndTime());
		Assert.assertEquals(eTrip.getName(), it.getName());
		Assert.assertEquals(eTrip.getTripId(), it.getTripId());
		while(eTrip.moreEvents()) {
			IEvent event = eTrip.getNextEvent();
			if(Reading.class.isInstance(event)) {
				Reading rEvent = (Reading)event;
				Assert.assertEquals(eReading.getLatitude(), rEvent.getLatitude());				
				Assert.assertEquals(eReading.getLongitude(), rEvent.getLongitude());				
				Assert.assertEquals(eReading.getMood(), rEvent.getMood());				
				Assert.assertEquals(eReading.getTimeStamp(), rEvent.getTimeStamp());				

			} else if (Location.class.isInstance(event)) {
				Location lEvent = (Location)event;
				Assert.assertEquals(eLocation.getLatitude(), lEvent.getLatitude());				
				Assert.assertEquals(eLocation.getLongitude(), lEvent.getLongitude());				
				Assert.assertEquals(eLocation.getTimeStamp(), lEvent.getTimeStamp());				
			} else if (Call.class.isInstance(event)) {
				Call cEvent = (Call)event;
				Assert.assertEquals(eCall.getLatitude(), cEvent.getLatitude());				
				Assert.assertEquals(eCall.getLongitude(), cEvent.getLongitude());				
				Assert.assertEquals(eCall.getTimeStamp(), cEvent.getTimeStamp());
				Assert.assertEquals(eCall.getCaller(), cEvent.getCaller());
				Assert.assertEquals(eCall.getReciever(), cEvent.getReciever());
				Assert.assertEquals(eCall.getEndTime(), cEvent.getEndTime());
			} else if (Sms.class.isInstance(event)) {
				Sms sEvent = (Sms)event;
				Assert.assertEquals(eSms.getLatitude(), sEvent.getLatitude());				
				Assert.assertEquals(eSms.getLongitude(), sEvent.getLongitude());				
				Assert.assertEquals(eSms.getTimeStamp(), sEvent.getTimeStamp());
				Assert.assertEquals(eSms.getSender(), sEvent.getSender());
				Assert.assertEquals(eSms.getReciever(), sEvent.getReciever());
				Assert.assertEquals(eSms.getMessage(), sEvent.getMessage());	
			}
		}
		
		
		
	}
}
