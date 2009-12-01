package itu.malta.drunkendroidserver.test;



import java.util.LinkedList;
import junit.framework.Assert;
import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.Reading;
import itu.malta.drunkendroidserver.domain.Trip;
import itu.malta.drunkendroidserver.interfaces.IEvent;
import itu.malta.drunkendroidserver.util.xstreem.converters.EventConverter;
import itu.malta.drunkendroidserver.util.xstreem.converters.TripConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
/**
 * JUnit tests for testing the XStream converters.
 */
public class ConverterTest {
	XStream xStream;
	String TripXML, eventsXML;
	Trip trip;
	Reading reading;
	Location loc;
	LinkedList<IEvent> events;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		trip = new Trip(1255816133L, 1255816133L, "ginsaturday");
		events = new LinkedList<IEvent>();
		reading = new Reading(1255816133L,35.908422D,14.502362D, 124);
		loc = new Location(1255816133L,14.502362D,35.908422D);
		events.add(reading);
		events.add(loc);
		trip.addEvent(reading);
		trip.addEvent(loc);
		
		TripXML = 
			"<trip>\n" +
	  		"  <events>\n" +
	    	"    <event>\n" +
	     	"      <eventType>reading</eventType>\n" +
	     	"      <dateTime>1255816133</dateTime>\n" +
	     	"      <longitude>14.502362</longitude>\n" +
	     	"      <latitude>35.908422</latitude>\n" +
	     	"      <data>\n" +
	      	"        <mood>124</mood>\n" +
	     	"      </data>\n" +
	   		"    </event>\n" +
	    	"    <event>\n" +
	     	"      <eventType>location</eventType>\n" +
	     	"      <dateTime>1255816133</dateTime>\n" +
	     	"      <longitude>14.502362</longitude>\n" +
	     	"      <latitude>35.908422</latitude>\n" +
	   		"    </event>\n" +
	  		"  </events>\n" +
	  		"  <startDateTime>1255816133</startDateTime>\n" +
	  		"  <endDateTime>1255816133</endDateTime>\n" +
	  		"  <name>ginsaturday</name>\n" +
			"</trip>";
		eventsXML = 
			"<events>\n" +
			"  <event>\n" +
	     	"    <eventType>reading</eventType>\n" +
	     	"    <dateTime>1255816133</dateTime>\n" +
	     	"    <longitude>14.502362</longitude>\n" +
	     	"    <latitude>35.908422</latitude>\n" +
	     	"    <data>\n" +
	      	"      <mood>124</mood>\n" +
	     	"    </data>\n" +
	   		"  </event>\n" +
			"  <event>\n" +
			"    <eventType>location</eventType>\n" +
			"    <dateTime>1255816133</dateTime>\n" +
			"    <longitude>14.502362</longitude>\n" +
			"    <latitude>35.908422</latitude>\n" +
			"  </event>\n" +
	  		"</events>";
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link TripConverter#marshal(Object value, HierarchicalStreamWriter writer,
	 *		MarshallingContext context)}
	 * 	 
	 * */
	@Test
	public void testTripMarshal() {
		xStream = new XStream();
		xStream.registerConverter(new TripConverter());
		xStream.alias("trip", Trip.class);
		String XMLOutput = xStream.toXML(trip);
		Assert.assertEquals(TripXML, XMLOutput);
	}
	/**
	 * Test method for {@link TripConverter#unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context)}
	 * 	 
	 * */
	@Test 
	public void testTripUnMarshal() {
		xStream = new XStream();
		xStream.registerConverter(new TripConverter());
		xStream.alias("trip", Trip.class);
		
		Trip testTrip = (Trip)xStream.fromXML(TripXML);
		Assert.assertEquals(trip.getName(), testTrip.getName());
		Assert.assertEquals(trip.getEndTime(), testTrip.getEndTime());
		Assert.assertEquals(trip.getStartTime(), testTrip.getStartTime());
		
		while (trip.moreEvents()) {
			IEvent testEvent = trip.getNextEvent();
			if (testEvent.getClass().equals(Reading.class)) {
				Reading readingTestEvent = (Reading) testEvent;
				Assert.assertEquals(reading.getTimeStamp(), readingTestEvent.getTimeStamp());
				Assert.assertEquals(reading.getType(), readingTestEvent.getType());
				Assert.assertEquals(reading.getLatitude(), readingTestEvent.getLatitude());
				Assert.assertEquals(reading.getLongitude(), readingTestEvent.getLongitude());	
				Assert.assertEquals(reading.getMood(), readingTestEvent.getMood());
			} else if (testEvent.getClass().equals(Location.class)) {

				Location locationTestEvent = (Location) testEvent;
				Assert.assertEquals(loc.getTimeStamp(), locationTestEvent.getTimeStamp());
				Assert.assertEquals(loc.getType(), locationTestEvent.getType());
				Assert.assertEquals(loc.getLatitude(), locationTestEvent.getLatitude());
				Assert.assertEquals(loc.getLongitude(), locationTestEvent.getLongitude());
			}
		}
		
	}
	/**
	 * Test method for {@link EventConverter#marshal(Object value, HierarchicalStreamWriter writer,MarshallingContext context)
	 * */
	@Test
	public void testEventMarshal() {
		XStream xStream = new XStream();
		xStream.registerConverter(new EventConverter());
		xStream.alias("event", Reading.class);
		xStream.alias("event", Location.class);
		xStream.alias("events", LinkedList.class);
		
		String XmlOutput = xStream.toXML(events);
		Assert.assertEquals(eventsXML, XmlOutput);
	}
	
	/**
	 * Test method for {@link EventConverter#unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context)}
	 * 	 
	 * */
	@Test
	public void testEventUnMarshal() {
		XStream xStream = new XStream();
		xStream.registerConverter(new EventConverter());
		xStream.alias("events", Reading.class);
		xStream.alias("events", Location.class);

		
		Object uncastedEvents = xStream.fromXML(eventsXML);

		LinkedList<?> events = (LinkedList<?>)uncastedEvents; 
		
		IEvent event;
		
		for (int i = 0; i < events.size(); i++ ) {
			event = (IEvent) events.get(i);
			
			if(Reading.class.isInstance(event)) {
				Reading rEvent = (Reading)event;
				Assert.assertEquals(reading.getTimeStamp(), rEvent.getTimeStamp());
				Assert.assertEquals(reading.getType(), rEvent.getType());
				Assert.assertEquals(reading.getLatitude(), rEvent.getLatitude());
				Assert.assertEquals(reading.getLongitude(), rEvent.getLongitude());
				Assert.assertEquals(reading.getMood(), rEvent.getMood());
			} else if (Location.class.isInstance(event)) {
				Location lEvent = (Location)event;
				Assert.assertEquals(loc.getTimeStamp(), lEvent.getTimeStamp());
				Assert.assertEquals(loc.getType(), lEvent.getType());
				Assert.assertEquals(loc.getLatitude(), lEvent.getLatitude());
				Assert.assertEquals(loc.getLongitude(), lEvent.getLongitude());
			}
		}
	}
}
