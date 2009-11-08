package itu.malta.drunkendroid.test;

import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import static org.easymock.EasyMock.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.*;
import itu.malta.drunkendroid.domain.*;
import itu.malta.drunkendroid.tech.RESTServerHelper;
import android.test.AndroidTestCase;

public class RESTconnectionTest extends AndroidTestCase {
	RESTServerHelper rest;

	protected void setUp(){
		 rest = new RESTServerHelper(this.getContext());
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
		
		expect(entity.getContent()).andStubReturn(bstream);
		replay(entity);
		
		expect(status.getStatusCode()).andStubReturn(201);
		replay(status);
		
		expect(response.getStatusLine()).andStubReturn(status);
		expect(response.getEntity()).andStubReturn(entity);
		replay(response);
		
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
		
		expect(entity.getContent()).andStubReturn(bstream);
		replay(entity);
		
		expect(status.getStatusCode()).andStubReturn(400);
		replay(status);
		
		expect(response.getStatusLine()).andStubReturn(status);
		expect(response.getEntity()).andStubReturn(entity);
		replay(response);
		
		//Verify
		Long tripIdResult = (Long) consume.invoke(rest, response);
		assertEquals(2343456, tripIdResult.intValue());
	}
//	
//	public void testUploadTrip(){
//		Trip t = new Trip();
//		// Reading 1
//		Reading r1 = new Reading();
//		Calendar c1 = Calendar.getInstance();
//		c1.setTimeInMillis(1255816133);
//		r1.setDate(c1);
//		r1.setLatitude(35.908422);
//		r1.setLongitude(14.502362);
//		r1.setMood((short)110);
//		// Reading 2
//		Reading r2 = new Reading();
//		Calendar c2 = Calendar.getInstance();
//		c2.setTimeInMillis(1255816433);
//		r2.setDate(c1);
//		r2.setLatitude(35.909141);
//		r2.setLongitude(14.503580);
//		r2.setMood((short)95);
//		// Reading 3
//		Reading r3 = new Reading();
//		Calendar c3 = Calendar.getInstance();
//		c3.setTimeInMillis(1255816733);
//		r3.setDate(c1);
//		r3.setLatitude(35.909275);
//		r3.setLongitude(14.502825);
//		r3.setMood((short)62);
//		t.AddReading(r1);
//		t.AddReading(r2);
//		t.AddReading(r3);
//		t.setDateInMilliSec(r1.getDate().getTimeInMillis());
//		
//		//Done building the trip
//		Long l = null;
//		try{
//			l = rest.uploadTrip(t);
//		}
//		catch(Exception e){
//			assertFalse(true);
//		}
//		
//		assertTrue( l != null && 0 < l.intValue());
//	}
}
