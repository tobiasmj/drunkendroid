package itu.malta.drunkendroidserver.test;


import java.sql.SQLException;

import junit.framework.Assert;
import itu.malta.drunkendroidserver.Trip;
import itu.malta.drunkendroidserver.control.Repository;
import itu.malta.drunkendroidserver.mock.MockInsertTripDatabaseConnection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TripResourceTest {

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
	 * Test method for 
	 * 	 
	 * */
	@Test
	public void testInsert() {
		Repository rep = new Repository(MockInsertTripDatabaseConnection.getInstance().getConn());
		Trip it = new Trip("111111",(long)123456,(long)123456,"Test trip");
		try {
			Assert.assertEquals((long) 42, rep.insertTrip(it));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
