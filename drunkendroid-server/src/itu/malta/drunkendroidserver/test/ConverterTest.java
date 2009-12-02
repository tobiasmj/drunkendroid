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
	private XStream _xStream;
	private String _tripXML, _eventsXML;
	private Trip _trip;
	private Reading _reading;
	private Location _loc;
	private Call _call1, _call2;
	private Sms _sms1, _sms2;
	private LinkedList<IEvent> _events;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		_trip = new Trip(1255816133L, 1255816133L, "ginsaturday");
		_events = new LinkedList<IEvent>();
		_reading = new Reading(1255816133L,35.908422D,14.502362D, 124);
		_loc = new Location(1255816133L,14.502362D,35.908422D);
		_call1 = new Call(1255816133L,35.908422D,14.502362D,"004551883250","0",12558161155L);
		_call2 = new Call(1255816133L,35.908422D,14.502362D,"0","004551883250",12558161155L);
		_sms1 = new Sms(1255816133L,35.908422D,14.502362D,"004551883250","0","test message");
		_sms2 = new Sms(1255816133L,35.908422D,14.502362D,"0","004551883250","test message");
		
		_events.add(_reading);
		_events.add(_loc);
		_events.add(_call1);
		_events.add(_call2);
		_events.add(_sms1);
		_events.add(_sms2);
		
		_trip.addEvent(_reading);
		_trip.addEvent(_loc);
		_trip.addEvent(_call1);
		_trip.addEvent(_call2);
		_trip.addEvent(_sms1);
		_trip.addEvent(_sms2);
		
		_tripXML = 
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
		_eventsXML = 
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
		_xStream = new XStream();
		_xStream.registerConverter(new TripConverter());
		_xStream.alias("trip", Trip.class);
		String XMLOutput = _xStream.toXML(_trip);
		Assert.assertEquals(_tripXML, XMLOutput);
	}
	/**
	 * Test method for {@link TripConverter#unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context)}
	 * 	 
	 * */
	@Test 
	public void testTripUnMarshal() {
		_xStream = new XStream();
		_xStream.registerConverter(new TripConverter());
		_xStream.alias("trip", Trip.class);
		
		Trip testTrip = (Trip)_xStream.fromXML(_tripXML);
		Assert.assertEquals(_trip.getName(), testTrip.getName());
		Assert.assertEquals(_trip.getEndTime(), testTrip.getEndTime());
		Assert.assertEquals(_trip.getStartTime(), testTrip.getStartTime());
		
		while (_trip.moreEvents()) {
			IEvent testEvent = _trip.getNextEvent();
			if (testEvent.getClass().equals(Reading.class)) {
				Reading readingTestEvent = (Reading) testEvent;
				Assert.assertEquals(_reading.getTimeStamp(), readingTestEvent.getTimeStamp());
				Assert.assertEquals(_reading.getType(), readingTestEvent.getType());
				Assert.assertEquals(_reading.getLatitude(), readingTestEvent.getLatitude());
				Assert.assertEquals(_reading.getLongitude(), readingTestEvent.getLongitude());	
				Assert.assertEquals(_reading.getMood(), readingTestEvent.getMood());
			} else if (testEvent.getClass().equals(Location.class)) {

				Location locationTestEvent = (Location) testEvent;
				Assert.assertEquals(_loc.getTimeStamp(), locationTestEvent.getTimeStamp());
				Assert.assertEquals(_loc.getType(), locationTestEvent.getType());
				Assert.assertEquals(_loc.getLatitude(), locationTestEvent.getLatitude());
				Assert.assertEquals(_loc.getLongitude(), locationTestEvent.getLongitude());
			} else if (testEvent.getClass().equals(Call.class)) {
				
				Call callEvent = (Call) testEvent;
				Assert.assertEquals(_call1.getTimeStamp(), callEvent.getTimeStamp());
				Assert.assertEquals(_call1.getType(), callEvent.getType());
				Assert.assertEquals(_call1.getLatitude(), callEvent.getLatitude());
				Assert.assertEquals(_call1.getLongitude(), callEvent.getLongitude());
				if(callEvent.getCaller().equals("0")) {
					Assert.assertEquals(_call2.getReciever(),callEvent.getReciever());
					
				} else if (callEvent.getReciever().equals("0")) {
					Assert.assertEquals(_call1.getCaller(),callEvent.getCaller());
				}
				Assert.assertEquals(_call1.getEndTime(),callEvent.getEndTime());
			} else if (testEvent.getClass().equals(Sms.class)) {
				
				Sms smsEvent = (Sms) testEvent;
				Assert.assertEquals(_sms1.getTimeStamp(), smsEvent.getTimeStamp());
				Assert.assertEquals(_sms1.getType(), smsEvent.getType());
				Assert.assertEquals(_sms1.getLatitude(), smsEvent.getLatitude());
				Assert.assertEquals(_sms1.getLongitude(), smsEvent.getLongitude());
				if(smsEvent.getSender().equals("0")) {
					Assert.assertEquals(_sms2.getReciever(),smsEvent.getReciever());
					
				} else if (smsEvent.getReciever().equals("0")) {
					Assert.assertEquals(_sms1.getSender(),smsEvent.getSender());
				}
				Assert.assertEquals(_sms1.getMessage(),smsEvent.getMessage());
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
		
		String XmlOutput = xStream.toXML(_events);
		Assert.assertEquals(_eventsXML, XmlOutput);
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

		
		Object uncastedEvents = xStream.fromXML(_eventsXML);

		LinkedList<?> events = (LinkedList<?>)uncastedEvents; 
		
		IEvent event;
		
		for (int i = 0; i < events.size(); i++ ) {
			event = (IEvent) events.get(i);
			
			if(Reading.class.isInstance(event)) {
				Reading rEvent = (Reading)event;
				Assert.assertEquals(_reading.getTimeStamp(), rEvent.getTimeStamp());
				Assert.assertEquals(_reading.getType(), rEvent.getType());
				Assert.assertEquals(_reading.getLatitude(), rEvent.getLatitude());
				Assert.assertEquals(_reading.getLongitude(), rEvent.getLongitude());
				Assert.assertEquals(_reading.getMood(), rEvent.getMood());
			} else if (Location.class.isInstance(event)) {
				Location lEvent = (Location)event;
				Assert.assertEquals(_loc.getTimeStamp(), lEvent.getTimeStamp());
				Assert.assertEquals(_loc.getType(), lEvent.getType());
				Assert.assertEquals(_loc.getLatitude(), lEvent.getLatitude());
				Assert.assertEquals(_loc.getLongitude(), lEvent.getLongitude());
			}else if (Call.class.isInstance(event)) {
				
				Call callEvent = (Call) event;
				Assert.assertEquals(_call1.getTimeStamp(), callEvent.getTimeStamp());
				Assert.assertEquals(_call1.getType(), callEvent.getType());
				Assert.assertEquals(_call1.getLatitude(), callEvent.getLatitude());
				Assert.assertEquals(_call1.getLongitude(), callEvent.getLongitude());
				if(callEvent.getCaller().equals("0")) {
					Assert.assertEquals(_call2.getReciever(),callEvent.getReciever());
					
				} else if (callEvent.getReciever().equals("0")) {
					Assert.assertEquals(_call1.getCaller(),callEvent.getCaller());
				}
				Assert.assertEquals(_call1.getEndTime(),callEvent.getEndTime());
			} else if (Sms.class.isInstance(event)) {
				
				Sms smsEvent = (Sms)event;
				Assert.assertEquals(_sms1.getTimeStamp(), smsEvent.getTimeStamp());
				Assert.assertEquals(_sms1.getType(), smsEvent.getType());
				Assert.assertEquals(_sms1.getLatitude(), smsEvent.getLatitude());
				Assert.assertEquals(_sms1.getLongitude(), smsEvent.getLongitude());
				if(smsEvent.getSender().equals("0")) {
					Assert.assertEquals(_sms2.getReciever(),smsEvent.getReciever());
					
				} else if (smsEvent.getReciever().equals("0")) {
					Assert.assertEquals(_sms1.getSender(),smsEvent.getSender());
				}
				Assert.assertEquals(_sms1.getMessage(),smsEvent.getMessage());
			}
		}
	}
}
