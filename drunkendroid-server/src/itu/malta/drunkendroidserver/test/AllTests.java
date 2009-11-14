package itu.malta.drunkendroidserver.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.Test;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MoodMapTest.class, TripResourceTest.class })
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for itu.malta.drunkendroidserver.test");
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}

}
