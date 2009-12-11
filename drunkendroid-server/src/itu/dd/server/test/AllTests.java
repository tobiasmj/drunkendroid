package itu.dd.server.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * jUnit test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { MoodmapTest.class, RepositoryTest.class, ConverterTest.class })
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for itu.malta.drunkendroidserver.test");
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}

}
