package itu.malta.drunkendroid.test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.*;

import itu.malta.drunkendroid.tech.IWebserviceConnection;
import itu.malta.drunkendroid.tech.RESTServerHelper;
import itu.malta.drunkendroid.tech.WebserviceConnection;
import android.test.AndroidTestCase;
import static org.easymock.EasyMock.*;

public class RESTconnectionTest extends AndroidTestCase {
	RESTServerHelper rest;

	protected void setUp(){
		 rest = new RESTServerHelper(this.getContext(), new WebserviceConnection());
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
		rest = new RESTServerHelper(this.getContext(), conn);
		//Verify
		Long tripIdResult = (Long) consume.invoke(rest, response);
		assertEquals(2343456, tripIdResult.intValue());
	}
	
	public void testConsumeResponseWithMalformedXML() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IllegalStateException, IOException{
		//Build
		final String content = "<error><code>1</code><message>Payload not text/XML</message></error>";
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
		
		expect(status.getStatusCode()).andStubReturn(400);
		replay(status);
		
		expect(response.getStatusLine()).andStubReturn(status);
		expect(response.getEntity()).andStubReturn(entity);
		replay(response);
		
		expect(conn.postTrip((String)anyObject(), (String)anyObject())).andStubReturn(response);
		rest = new RESTServerHelper(this.getContext(), conn);
		//Verify
		Long tripIdResult = (Long) consume.invoke(rest, response);
		//Don't know how to do these assertions right yet.
		assertFalse(true);
	}
}
