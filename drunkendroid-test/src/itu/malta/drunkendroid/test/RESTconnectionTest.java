package itu.malta.drunkendroid.test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.easymock.Capture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import itu.malta.drunkendroid.control.IRemoteDataFacade;
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.LocationEvent;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.IWebserviceConnection;
import itu.malta.drunkendroid.tech.RESTServerFacade;
import itu.malta.drunkendroid.tech.WebserviceConnectionREST;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.test.AndroidTestCase;
import static org.easymock.EasyMock.*;

public class RESTconnectionTest extends AndroidTestCase {
	RESTServerFacade rest = null;

	protected void setUp(){
		
	}
	
	protected void tearDown(){
	}
	
	public void testConsumePostResponseWithStatus201() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IllegalStateException, IOException{
		//Build
		final String content = "<tripId>2343456</tripId>";
		//Build an inputStream from to provide the mock object with.
		ByteArrayInputStream bstream = new ByteArrayInputStream(content.getBytes());
		
		//Build a mock up of the HttpResponse.
		HttpResponse response = createMock(HttpResponse.class);
		HttpEntity entity = createMock(HttpEntity.class);
		StatusLine status = createMock(StatusLine.class);
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		
		expect(entity.getContent()).andStubReturn(bstream);
		replay(entity);
		
		expect(status.getStatusCode()).andStubReturn(201);
		replay(status);
		
		expect(response.getStatusLine()).andStubReturn(status);
		expect(response.getEntity()).andStubReturn(entity);
		replay(response);
		
		expect(conn.post((String)anyObject(), (String)anyObject())).andStubReturn(response);
		rest = new RESTServerFacade(this.getContext(), conn);
		//Make the private method accessible using reflection.
		Method consume = rest.getClass().getDeclaredMethod("consumeTripUploadResponse", HttpResponse.class);
		consume.setAccessible(true);
		
		//Verify
		Long tripIdResult = (Long) consume.invoke(rest, response);
		assertEquals(2343456, tripIdResult.intValue());
	}
	
	public void testConsumePostResponseWithMalformedXML() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IllegalStateException, IOException{
		//Build
		final String content = "<error><code>1</code><message>Payload not text/XML</message></error>";
		//Build an inputStream from to provide the mock object with.
		ByteArrayInputStream bstream = new ByteArrayInputStream(content.getBytes());
		
		//Build a trip to hand over to the uploadMethod
		Trip t = generateTrip();
		
		//Build a mock up of the HttpResponse.
		HttpResponse response = createMock(HttpResponse.class);
		HttpEntity entity = createMock(HttpEntity.class);
		StatusLine status = createMock(StatusLine.class);
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		expect(entity.getContent()).andStubReturn(bstream);
		replay(entity);
		expect(status.getStatusCode()).andStubReturn(400);
		replay(status);
		expect(response.getStatusLine()).andStubReturn(status);
		expect(response.getEntity()).andStubReturn(entity);
		replay(response);
		expect(conn.post((String)anyObject(), (String)anyObject())).andStubReturn(response);
		replay(conn);
		
		//Create a new RESTServerHelper class to test with.
		rest = new RESTServerFacade(this.getContext(), conn);
		//Verify
		rest.uploadTrip(t);
		//Don't know how to do these assertions right yet.
		assertNull(t.getRemoteID());
	}
	
	public void testConsumePostWithServerError() throws IllegalStateException, IOException{
		//Build
		final String content1 = "<error><code>7</code><message>Dummy server error. Unit testing</message></error>";
		//Build an inputStream from to provide the mock object with.
		ByteArrayInputStream bstream1 = new ByteArrayInputStream(content1.getBytes());
		//Build a secound stream
		final String content2 = "<tripId>2343456</tripId>";
		ByteArrayInputStream bstream2 = new ByteArrayInputStream(content2.getBytes());
		
		//Build a trip to hand over to the uploadMethod
		Trip t = generateTrip();
		
		//Build a mock up of the HttpResponse.
		HttpResponse response1 = createMock(HttpResponse.class);
		HttpEntity entity1 = createMock(HttpEntity.class);
		StatusLine status1 = createMock(StatusLine.class);
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		//First try with the server
		expect(entity1.getContent()).andStubReturn(bstream1);
		replay(entity1);
		expect(status1.getStatusCode()).andStubReturn(500);
		replay(status1);
		expect(response1.getStatusLine()).andStubReturn(status1);
		expect(response1.getEntity()).andStubReturn(entity1);
		replay(response1);
		//Second try with the server
		HttpResponse response2 = createMock(HttpResponse.class);
		HttpEntity entity2 = createMock(HttpEntity.class);
		StatusLine status2 = createMock(StatusLine.class);
		
		expect(entity2.getContent()).andStubReturn(bstream2);
		replay(entity2);
		expect(status2.getStatusCode()).andStubReturn(201);
		replay(status2);
		expect(response2.getStatusLine()).andStubReturn(status2);
		expect(response2.getEntity()).andStubReturn(entity2);
		replay(response2);
		
		expect(conn.post((String)anyObject(), (String)anyObject()))
			.andReturn(response1)
			.andReturn(response2);
		replay(conn);
		
		//Create a new RESTServerHelper class to test with.
		rest = new RESTServerFacade(this.getContext(), conn);
		//Verify
		rest.uploadTrip(t);
		Long tripIdResult = t.getRemoteID();
		verify(conn);
		//Don't know how to do these assertions right yet.
		assertEquals(2343456, tripIdResult.intValue());
	
	}
	
	public void testBuildXMLFromStandAloneEvent(){
		Trip t = this.generateTrip();
		t.setRemoteID(42L); //just a random test number
		Event e1 = new LocationEvent(Calendar.getInstance().getTimeInMillis(), (Double)34.123456, (Double)14.123456);
		//Expected results
		String expectedURI;
		TelephonyManager mgr = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
		if(mgr.getDeviceId() == null || mgr.getDeviceId() == "null"){
			expectedURI = "trip/event/null/42"; //Null because the IMEI number is not known in the standard emulator.
		}
		else{
			expectedURI = "trip/event/" + mgr.getDeviceId() + "/42";
		}
		String expectedXML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +
				"<events>" +
					"<event>" +
						"<eventType>event</eventType>" +
						"<dateTime>"+ String.valueOf(e1.dateTime) +"</dateTime>" +
						"<longitude>"+ String.valueOf(e1.longitude) +"</longitude>" +
						"<latitude>"+ String.valueOf(e1.latitude)+"</latitude>" +
					"<data />" +
					"</event>" +
				"</events>";
		
		//Build a mock of IWebserviceConnection
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		HttpResponse response = createMock(HttpResponse.class);
		StatusLine statusline = createMock(StatusLine.class);
		expect(statusline.getStatusCode()).andStubReturn(new Integer(200));
		expect(response.getStatusLine()).andStubReturn(statusline);
		
		Capture<String> uri = new Capture<String>();
		Capture<String> xmlContent = new Capture<String>();
		expect(conn.post(capture(uri), capture(xmlContent))).andStubReturn(response);
		
		replay(statusline);
		replay(response);
		replay(conn);
		
		rest = new RESTServerFacade(this.getContext(), conn);
		
		ArrayList<Event> eventList1 = new ArrayList<Event>();
		eventList1.add(e1);
		//Execute
		rest.updateTrip(t, eventList1);
		
		//Verify
		assertTrue(uri.hasCaptured());
		assertTrue(xmlContent.hasCaptured());
		assertEquals(expectedURI, uri.getValue());
		assertEquals(expectedXML, xmlContent.getValue());
	}

	public void testBuildXMLFromStandAloneReadingEvent(){
		Trip t = this.generateTrip();
		t.setRemoteID(42L); //just a random test number
		int mood = 90;
		ReadingEvent e1 = new ReadingEvent(Calendar.getInstance().getTimeInMillis(), (Double)34.123456, (Double)14.123456, mood);
		//Expected results
		String expectedURI;
		TelephonyManager mgr = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
		if(mgr.getDeviceId() == null || mgr.getDeviceId() == "null"){
			expectedURI = "trip/event/null/42"; //Null because the IMEI number is not known in the standard emulator.
		}
		else{
			expectedURI = "trip/event/" + mgr.getDeviceId() + "/42";
		}
		String expectedXML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +
				"<events>" +
					"<event>" +
						"<eventType>reading</eventType>" +
						"<dateTime>"+ String.valueOf(e1.dateTime) +"</dateTime>" +
						"<longitude>"+ String.valueOf(e1.longitude) +"</longitude>" +
						"<latitude>"+ String.valueOf(e1.latitude)+"</latitude>" +
					"<data>" +
					"<mood>" + String.valueOf(e1.mood) +"</mood>"+
					"</data>" +
					"</event>" +
				"</events>";
		
		//Build a mock of IWebserviceConnection
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		HttpResponse response = createMock(HttpResponse.class);
		StatusLine statusline = createMock(StatusLine.class);
		expect(statusline.getStatusCode()).andStubReturn(new Integer(200));
		expect(response.getStatusLine()).andStubReturn(statusline);
		
		Capture<String> uri = new Capture<String>();
		Capture<String> xmlContent = new Capture<String>();
		expect(conn.post(capture(uri), capture(xmlContent))).andStubReturn(response);
		
		replay(statusline);
		replay(response);
		replay(conn);
		
		rest = new RESTServerFacade(this.getContext(), conn);
		
		ArrayList<Event> eventList1 = new ArrayList<Event>();
		eventList1.add(e1);
		//Execute
		rest.updateTrip(t, eventList1);
		
		//Verify
		assertTrue(uri.hasCaptured());
		assertTrue(xmlContent.hasCaptured());
		assertEquals(expectedURI, uri.getValue());
		assertEquals(expectedXML, xmlContent.getValue());
	}

	public void testGetReadingEvents() throws IllegalStateException, IOException{
		try{
			//Build up the test
			final String content1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
									"<MoodMap>" + 
										"<MoodMapReading>" +
											"<MoodMapValue>140</MoodMapValue>" +
											"<MoodMapLongitude>14.487617755003868</MoodMapLongitude>"+
											"<MoodMapLatitude>35.9232054039299</MoodMapLatitude>"+
										"</MoodMapReading>"+
										"<MoodMapReading>"+
										"<MoodMapValue>130</MoodMapValue>"+
										"<MoodMapLongitude>14.487617755003868</MoodMapLongitude>"+
										"<MoodMapLatitude>35.92350484235794</MoodMapLatitude>"+
										"</MoodMapReading>"+
										"</MoodMap>";
			//Build an inputStream from to provide the mock object with.
			ByteArrayInputStream bstream1 = new ByteArrayInputStream(content1.getBytes());
			
			//Build a mock of IWebserviceConnection
			IWebserviceConnection conn = createMock(IWebserviceConnection.class);
			HttpResponse response = createMock(HttpResponse.class);
			StatusLine statusline = createMock(StatusLine.class);
			HttpEntity entity = createMock(HttpEntity.class);
			
			//capture the uri for later processing.
			Capture<String> uri = new Capture<String>();
		
			expect(statusline.getStatusCode()).andStubReturn(new Integer(201));
			expect(response.getStatusLine()).andStubReturn(statusline);
			expect(conn.get(capture(uri))).andStubReturn(response);
			expect(response.getEntity()).andStubReturn(entity);
			expect(entity.getContent()).andStubReturn(bstream1);
			
			replay(statusline);
			replay(response);
			replay(conn);
			replay(entity);
			
			IRemoteDataFacade rest = new RESTServerFacade(this.getContext(), conn);
			
			//Execute
			//These values are parameters for the server, since we use a mock, they don't matter.
			Long starTime = 0L;
			Long endTime = 0L;
			Double ulLatitude = 0D;
			Double ulLongitude = 0D;
			Double lrLatitude = 0D;
			Double lrLongitude = 0D;
			List<ReadingEvent> result = rest.getReadingEvents(starTime, endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);
			
			//verify
			ReadingEvent r1 = result.get(0);
			ReadingEvent r2 = result.get(1);
			//Assert moods
			assertEquals(r1.mood, 140);
			assertEquals(r2.mood, 130);
			//Assert latitudes
			assertEquals(35.9232054039299D, r1.latitude);
			assertEquals(35.92350484235794D, r2.latitude);
			//Assert longitudes
			assertEquals(14.487617755003868D, r1.longitude);
			assertEquals(14.487617755003868D, r2.longitude);
		}
		finally{
			//do nothing really.
		}
	}
	
	private Trip generateTrip() {
		Trip t = new Trip();
		// ReadingEvent 1
		ReadingEvent r1 = new ReadingEvent(new Long(1255816133), (Double)35.908422, (Double)14.502362, 110);
		// ReadingEvent 2
		ReadingEvent r2 = new ReadingEvent(new Long(1255816433), (Double)35.909141, (Double)14.503580, 95);
		// ReadingEvent 3	
		ReadingEvent r3 = new ReadingEvent(new Long(1255816733), (Double)35.909275, (Double)14.502825, 62);
		t.AddEvent(r1);
		t.AddEvent(r2);
		t.AddEvent(r3);
		t.setDateInMilliSec(1255816133);
		return t;
	}
}
