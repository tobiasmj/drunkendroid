package itu.malta.drunkendroid.test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.*;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.IWebserviceConnection;
import itu.malta.drunkendroid.tech.RESTServerFacade;
import itu.malta.drunkendroid.tech.WebserviceConnectionREST;
import android.test.AndroidTestCase;
import static org.easymock.EasyMock.*;

public class RESTconnectionTest extends AndroidTestCase {
	RESTServerFacade rest;

	protected void setUp(){
		 rest = new RESTServerFacade(this.getContext(), new WebserviceConnectionREST());
	}
	
	protected void tearDown(){
		rest = null;
	}
	
	public void testConsumeResponseWithStatus201() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IllegalStateException, IOException{
		//Build
		final String content = "<tripId>2343456</tripId>";
		//Make the private method accessible using reflection.
		Method consume = rest.getClass().getDeclaredMethod("consumeTripUploadResponse", HttpResponse.class);
		consume.setAccessible(true);
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
		
		expect(conn.postTrip((String)anyObject(), (String)anyObject())).andStubReturn(response);
		rest = new RESTServerFacade(this.getContext(), conn);
		//Verify
		Long tripIdResult = (Long) consume.invoke(rest, response);
		assertEquals(2343456, tripIdResult.intValue());
	}
	
	public void testConsumeResponseWithMalformedXML() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IllegalStateException, IOException{
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
		expect(conn.postTrip((String)anyObject(), (String)anyObject())).andStubReturn(response);
		replay(conn);
		
		//Create a new RESTServerHelper class to test with.
		rest = new RESTServerFacade(this.getContext(), conn);
		//Verify
		rest.uploadTrip(t);
		//Don't know how to do these assertions right yet.
		assertNull(t.getRemoteID());
	}
	
	public void testConsumeWithServerError() throws IllegalStateException, IOException{
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
		
		expect(conn.postTrip((String)anyObject(), (String)anyObject()))
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
