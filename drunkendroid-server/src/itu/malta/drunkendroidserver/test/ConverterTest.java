package itu.malta.drunkendroidserver.test;



import java.util.LinkedList;
import junit.framework.Assert;
import itu.malta.drunkendroidserver.domain.Call;
import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.Reading;
import itu.malta.drunkendroidserver.domain.Sms;
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
	Call call1, call2;
	Sms sms1, sms2;
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
		call1 = new Call(1255816133L,35.908422D,14.502362D,"004551883250","0",12558161155L);
		call2 = new Call(1255816133L,35.908422D,14.502362D,"0","004551883250",12558161155L);
		sms1 = new Sms(1255816133L,35.908422D,14.502362D,"004551883250","0","test message");
		sms2 = new Sms(1255816133L,35.908422D,14.502362D,"0","004551883250","test message");
		
		events.add(reading);
		events.add(loc);
		events.add(call1);
		events.add(call2);
		events.add(sms1);
		events.add(sms2);
		
		trip.addEvent(reading);
		trip.addEvent(loc);
		trip.addEvent(call1);
		trip.addEvent(call2);
		trip.addEvent(sms1);
		trip.addEvent(sms2);
		
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
	    	"    <event>\n" +
	     	"      <eventType>call</eventType>\n" +
	     	"      <dateTime>1255816133</dateTime>\n" +
	     	"      <longitude>14.502362</longitude>\n" +
	     	"      <latitude>35.908422</latitude>\n" +
	     	"      <data>\n" +
	      	"        <caller>004551883250</caller>\n" +
	      	"        <endTime>12558161155</endTime>\n" +
	     	"      </data>\n" +
	   		"    </event>\n" +
	   		"    <event>\n" +
	     	"      <eventType>call</eventType>\n" +
	     	"      <dateTime>1255816133</dateTime>\n" +
	     	"      <longitude>14.502362</longitude>\n" +
	     	"      <latitude>35.908422</latitude>\n" +
	     	"      <data>\n" +
	      	"        <reciever>004551883250</reciever>\n" +
	      	"        <endTime>12558161155</endTime>\n" +
	     	"      </data>\n" +
	   		"    </event>\n" +
	    	"    <event>\n" +
	     	"      <eventType>SMS</eventType>\n" +
	     	"      <dateTime>1255816133</dateTime>\n" +
	     	"      <longitude>14.502362</longitude>\n" +
	     	"      <latitude>35.908422</latitude>\n" +
	     	"      <data>\n" +
	      	"        <sender>004551883250</sender>\n" +
	      	"        <message>test message</message>\n" +
	     	"      </data>\n" +
	   		"    </event>\n" +
	   		"    <event>\n" +
	     	"      <eventType>SMS</eventType>\n" +
	     	"      <dateTime>1255816133</dateTime>\n" +
	     	"      <longitude>14.502362</longitude>\n" +
	     	"      <latitude>35.908422</latitude>\n" +
	     	"      <data>\n" +
	      	"        <reciever>004551883250</reciever>\n" +
	      	"        <message>test message</message>\n" +
	     	"      </data>\n" +
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
	    	"  <event>\n" +
	     	"    <eventType>call</eventType>\n" +
	     	"    <dateTime>1255816133</dateTime>\n" +
	     	"    <longitude>14.502362</longitude>\n" +
	     	"    <latitude>35.908422</latitude>\n" +
	     	"    <data>\n" +
	      	"      <caller>004551883250</caller>\n" +
	      	"      <endTime>12558161155</endTime>\n" +
	     	"    </data>\n" +
	   		"  </event>\n" +
	   		"  <event>\n" +
	     	"    <eventType>call</eventType>\n" +
	     	"    <dateTime>1255816133</dateTime>\n" +
	     	"    <longitude>14.502362</longitude>\n" +
	     	"    <latitude>35.908422</latitude>\n" +
	     	"    <data>\n" +
	      	"      <reciever>004551883250</reciever>\n" +
	      	"      <endTime>12558161155</endTime>\n" +
	     	"    </data>\n" +
	   		"  </event>\n" +
	    	"  <event>\n" +
	     	"    <eventType>SMS</eventType>\n" +
	     	"    <dateTime>1255816133</dateTime>\n" +
	     	"    <longitude>14.502362</longitude>\n" +
	     	"    <latitude>35.908422</latitude>\n" +
	     	"    <data>\n" +
	      	"      <sender>004551883250</sender>\n" +
	      	"      <message>test message</message>\n" +
	     	"    </data>\n" +
	   		"  </event>\n" +
	   		"  <event>\n" +
	     	"    <eventType>SMS</eventType>\n" +
	     	"    <dateTime>1255816133</dateTime>\n" +
	     	"    <longitude>14.502362</longitude>\n" +
	     	"    <latitude>35.908422</latitude>\n" +
	     	"    <data>\n" +
	      	"      <reciever>004551883250</reciever>\n" +
	      	"      <message>test message</message>\n" +
	     	"    </data>\n" +
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
			} else if (testEvent.getClass().equals(Call.class)) {
				
				Call callEvent = (Call) testEvent;
				Assert.assertEquals(call1.getTimeStamp(), callEvent.getTimeStamp());
				Assert.assertEquals(call1.getType(), callEvent.getType());
				Assert.assertEquals(call1.getLatitude(), callEvent.getLatitude());
				Assert.assertEquals(call1.getLongitude(), callEvent.getLongitude());
				if(callEvent.getCaller().equals("0")) {
					Assert.assertEquals(call2.getReciever(),callEvent.getReciever());
					
				} else if (callEvent.getReciever().equals("0")) {
					Assert.assertEquals(call1.getCaller(),callEvent.getCaller());
				}
				Assert.assertEquals(call1.getEndTime(),callEvent.getEndTime());
			} else if (testEvent.getClass().equals(Sms.class)) {
				
				Sms smsEvent = (Sms) testEvent;
				Assert.assertEquals(sms1.getTimeStamp(), smsEvent.getTimeStamp());
				Assert.assertEquals(sms1.getType(), smsEvent.getType());
				Assert.assertEquals(sms1.getLatitude(), smsEvent.getLatitude());
				Assert.assertEquals(sms1.getLongitude(), smsEvent.getLongitude());
				if(smsEvent.getSender().equals("0")) {
					Assert.assertEquals(sms2.getReciever(),smsEvent.getReciever());
					
				} else if (smsEvent.getReciever().equals("0")) {
					Assert.assertEquals(sms1.getSender(),smsEvent.getSender());
				}
				Assert.assertEquals(sms1.getMessage(),smsEvent.getMessage());
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
		xStream.alias("event", Call.class);
		xStream.alias("event", Sms.class);
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
		xStream.alias("events", Call.class);
		xStream.alias("events", Sms.class);

		
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
			}else if (Call.class.isInstance(event)) {
				
				Call callEvent = (Call) event;
				Assert.assertEquals(call1.getTimeStamp(), callEvent.getTimeStamp());
				Assert.assertEquals(call1.getType(), callEvent.getType());
				Assert.assertEquals(call1.getLatitude(), callEvent.getLatitude());
				Assert.assertEquals(call1.getLongitude(), callEvent.getLongitude());
				if(callEvent.getCaller().equals("0")) {
					Assert.assertEquals(call2.getReciever(),callEvent.getReciever());
					
				} else if (callEvent.getReciever().equals("0")) {
					Assert.assertEquals(call1.getCaller(),callEvent.getCaller());
				}
				Assert.assertEquals(call1.getEndTime(),callEvent.getEndTime());
			} else if (Sms.class.isInstance(event)) {
				
				Sms smsEvent = (Sms)event;
				Assert.assertEquals(sms1.getTimeStamp(), smsEvent.getTimeStamp());
				Assert.assertEquals(sms1.getType(), smsEvent.getType());
				Assert.assertEquals(sms1.getLatitude(), smsEvent.getLatitude());
				Assert.assertEquals(sms1.getLongitude(), smsEvent.getLongitude());
				if(smsEvent.getSender().equals("0")) {
					Assert.assertEquals(sms2.getReciever(),smsEvent.getReciever());
					
				} else if (smsEvent.getReciever().equals("0")) {
					Assert.assertEquals(sms1.getSender(),smsEvent.getSender());
				}
				Assert.assertEquals(sms1.getMessage(),smsEvent.getMessage());
			}
		}
	}
}
