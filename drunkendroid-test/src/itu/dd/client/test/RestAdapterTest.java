package itu.dd.client.test;

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

import itu.dd.client.domain.Event;
import itu.dd.client.domain.LocationEvent;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.IRemoteDataFacade;
import itu.dd.client.tech.IWebserviceConnection;
import itu.dd.client.tech.RestAdapter;
import itu.dd.client.tech.exception.CommunicationException;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.test.AndroidTestCase;
import static org.easymock.EasyMock.*;

public class RestAdapterTest extends AndroidTestCase {
	RestAdapter rest = null;

	protected void setUp() {

	}

	protected void tearDown() {
	}

	public void testConsumePostResponseWithStatus201()
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IllegalStateException, IOException,
			CommunicationException {
		// Build
		final String content = "<tripId>2343456</tripId>";
		// Build an inputStream from to provide the mock object with.
		ByteArrayInputStream bstream = new ByteArrayInputStream(content
				.getBytes());

		// Build a mock up of the HttpResponse.
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

		expect(conn.post((String) anyObject(), (String) anyObject()))
				.andStubReturn(response);
		rest = new RestAdapter(this.getContext(), conn);
		// Make the private method accessible using reflection.
		Method consume = rest.getClass().getDeclaredMethod(
				"consumeTripUploadResponse", HttpResponse.class);
		consume.setAccessible(true);

		// Verify
		Long tripIdResult = (Long) consume.invoke(rest, response);
		assertEquals(2343456, tripIdResult.intValue());
	}

	public void testConsumePostResponseWithMalformedXML()
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IllegalStateException, IOException,
			CommunicationException {
		// Build
		final String content = "<error><code>1</code><message>Payload not text/XML</message></error>";
		// Build an inputStream from to provide the mock object with.
		ByteArrayInputStream bstream = new ByteArrayInputStream(content
				.getBytes());

		// Build a trip to hand over to the uploadMethod
		Trip t = generateTrip();

		// Build a mock up of the HttpResponse.
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
		expect(conn.post((String) anyObject(), (String) anyObject()))
				.andStubReturn(response);
		replay(conn);

		// Create a new RESTServerHelper class to test with.
		rest = new RestAdapter(this.getContext(), conn);
		// Verify
		rest.uploadTrip(t);
		// Don't know how to do these assertions right yet.
		assertNull(t.getRemoteId());
	}

	public void testConsumePostWithServerError() throws IllegalStateException,
			IOException, CommunicationException {
		// Build
		final String content1 = "<error><code>7</code><message>Dummy server error. Unit testing</message></error>";
		// Build an inputStream from to provide the mock object with.
		ByteArrayInputStream bstream1 = new ByteArrayInputStream(content1
				.getBytes());
		// Build a secound stream
		final String content2 = "<tripId>2343456</tripId>";
		ByteArrayInputStream bstream2 = new ByteArrayInputStream(content2
				.getBytes());

		// Build a trip to hand over to the uploadMethod
		Trip t = generateTrip();

		// Build a mock up of the HttpResponse.
		HttpResponse response1 = createMock(HttpResponse.class);
		HttpEntity entity1 = createMock(HttpEntity.class);
		StatusLine status1 = createMock(StatusLine.class);
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		// First try with the server
		expect(entity1.getContent()).andStubReturn(bstream1);
		replay(entity1);
		expect(status1.getStatusCode()).andStubReturn(500);
		replay(status1);
		expect(response1.getStatusLine()).andStubReturn(status1);
		expect(response1.getEntity()).andStubReturn(entity1);
		replay(response1);
		// Second try with the server
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

		expect(conn.post((String) anyObject(), (String) anyObject()))
				.andReturn(response1).andReturn(response2);
		replay(conn);

		// Create a new RESTServerHelper class to test with.
		rest = new RestAdapter(this.getContext(), conn);
		// Verify
		rest.uploadTrip(t);
		Long tripIdResult = t.getRemoteId();
		verify(conn);
		// Don't know how to do these assertions right yet.
		assertEquals(2343456, tripIdResult.intValue());

	}

	public void testBuildXMLFromStandAloneEvent() throws CommunicationException {
		Trip t = this.generateTrip();
		t.setRemoteId(42L); // just a random test number
		Event e1 = new LocationEvent(Calendar.getInstance().getTimeInMillis(),
				(Double) 34.123456, (Double) 14.123456);
		// Expected results
		String expectedURI;
		TelephonyManager mgr = (TelephonyManager) getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (mgr.getDeviceId() == null || mgr.getDeviceId() == "null") {
			expectedURI = "trip/event/null/42"; // Null because the IMEI number
												// is not known in the standard
												// emulator.
		} else {
			expectedURI = "trip/event/" + mgr.getDeviceId() + "/42";
		}
		String expectedXML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"
				+ "<events>"
				+ "<event>"
				+ "<eventType>event</eventType>"
				+ "<dateTime>"
				+ String.valueOf(e1.getDateTime())
				+ "</dateTime>"
				+ "<longitude>"
				+ String.valueOf(e1.getLongitude())
				+ "</longitude>"
				+ "<latitude>"
				+ String.valueOf(e1.getLatitude())
				+ "</latitude>"
				+ "<data />" + "</event>" + "</events>";

		// Build a mock of IWebserviceConnection
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		HttpResponse response = createMock(HttpResponse.class);
		StatusLine statusline = createMock(StatusLine.class);
		expect(statusline.getStatusCode()).andStubReturn(new Integer(200));
		expect(response.getStatusLine()).andStubReturn(statusline);

		Capture<String> uri = new Capture<String>();
		Capture<String> xmlContent = new Capture<String>();
		expect(conn.post(capture(uri), capture(xmlContent))).andStubReturn(
				response);

		replay(statusline);
		replay(response);
		replay(conn);

		rest = new RestAdapter(this.getContext(), conn);

		ArrayList<Event> eventList1 = new ArrayList<Event>();
		eventList1.add(e1);
		// Execute
		rest.updateTrip(t, eventList1);

		// Verify
		assertTrue(uri.hasCaptured());
		assertTrue(xmlContent.hasCaptured());
		assertEquals(expectedURI, uri.getValue());
		assertEquals(expectedXML, xmlContent.getValue());
	}

	public void testBuildXMLFromStandAloneMoodEvent()
			throws CommunicationException {
		Trip t = this.generateTrip();
		t.setRemoteId(42L); // just a random test number
		int mood = 90;
		MoodEvent e1 = new MoodEvent(Calendar.getInstance()
				.getTimeInMillis(), (Double) 34.123456, (Double) 14.123456,
				mood);
		// Expected results
		String expectedURI;
		TelephonyManager mgr = (TelephonyManager) getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (mgr.getDeviceId() == null || mgr.getDeviceId() == "null") {
			expectedURI = "trip/event/null/42"; // Null because the IMEI number
												// is not known in the standard
												// emulator.
		} else {
			expectedURI = "trip/event/" + mgr.getDeviceId() + "/42";
		}
		String expectedXML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"
				+ "<events>"
				+ "<event>"
				+ "<eventType>mood</eventType>"
				+ "<dateTime>"
				+ String.valueOf(e1.getDateTime())
				+ "</dateTime>"
				+ "<longitude>"
				+ String.valueOf(e1.getLongitude())
				+ "</longitude>"
				+ "<latitude>"
				+ String.valueOf(e1.getLatitude())
				+ "</latitude>"
				+ "<data>"
				+ "<mood>"
				+ String.valueOf(e1.getMood())
				+ "</mood>"
				+ "</data>" + "</event>" + "</events>";

		// Build a mock of IWebserviceConnection
		IWebserviceConnection conn = createMock(IWebserviceConnection.class);
		HttpResponse response = createMock(HttpResponse.class);
		StatusLine statusline = createMock(StatusLine.class);
		expect(statusline.getStatusCode()).andStubReturn(new Integer(200));
		expect(response.getStatusLine()).andStubReturn(statusline);

		Capture<String> uri = new Capture<String>();
		Capture<String> xmlContent = new Capture<String>();
		expect(conn.post(capture(uri), capture(xmlContent))).andStubReturn(
				response);

		replay(statusline);
		replay(response);
		replay(conn);

		rest = new RestAdapter(this.getContext(), conn);

		ArrayList<Event> eventList1 = new ArrayList<Event>();
		eventList1.add(e1);
		// Execute
		rest.updateTrip(t, eventList1);

		// Verify
		assertTrue(uri.hasCaptured());
		assertTrue(xmlContent.hasCaptured());
		assertEquals(expectedURI, uri.getValue());
		assertEquals(expectedXML, xmlContent.getValue());
	}

	public void testGetMoodEvents() throws IllegalStateException,
			IOException, CommunicationException {
		try {
			// Build up the test
			final String content1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
					+ "<points>"
					+ "<p value=\"140\" long=\"14.487617755003868\" lat=\"35.9232054039299\" />"
					+ "<p value=\"130\" long=\"14.487617755003868\" lat=\"35.92350484235794\" />"
					+ "</points>";
			// Build an inputStream from to provide the mock object with.
			ByteArrayInputStream bstream1 = new ByteArrayInputStream(content1
					.getBytes());

			// Build a mock of IWebserviceConnection
			IWebserviceConnection conn = createMock(IWebserviceConnection.class);
			HttpResponse response = createMock(HttpResponse.class);
			StatusLine statusline = createMock(StatusLine.class);
			HttpEntity entity = createMock(HttpEntity.class);

			// capture the uri for later processing.
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

			IRemoteDataFacade rest = new RestAdapter(this.getContext(),
					conn);

			// Execute
			// These values are parameters for the server, since we use a mock,
			// they don't matter.
			Long starTime = 0L;
			Long endTime = 0L;
			Double ulLatitude = 0D;
			Double ulLongitude = 0D;
			Double lrLatitude = 0D;
			Double lrLongitude = 0D;
			List<MoodEvent> result = rest.getReadingEvents(starTime,
					endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);

			// verify
			MoodEvent r1 = result.get(0);
			MoodEvent r2 = result.get(1);
			// Assert moods
			assertEquals(r1.getMood(), 140);
			assertEquals(r2.getMood(), 130);
			// Assert latitudes
			assertEquals(35.9232054039299D, r1.getLatitude());
			assertEquals(35.92350484235794D, r2.getLatitude());
			// Assert longitudes
			assertEquals(14.487617755003868D, r1.getLongitude());
			assertEquals(14.487617755003868D, r2.getLongitude());
		} finally {
			// do nothing really.
		}
	}

	private Trip generateTrip() {
		Trip t = new Trip();
		t.setName("TestName");
		// MoodEvent 1
		MoodEvent r1 = new MoodEvent(new Long(1255816133),
				(Double) 35.908422, (Double) 14.502362, 110);
		// MoodEvent 2
		MoodEvent r2 = new MoodEvent(new Long(1255816433),
				(Double) 35.909141, (Double) 14.503580, 95);
		// MoodEvent 3
		MoodEvent r3 = new MoodEvent(new Long(1255816733),
				(Double) 35.909275, (Double) 14.502825, 62);
		t.getEvents().add(r1);
		t.getEvents().add(r2);
		t.getEvents().add(r3);
		t.setStartDate(1255816133L);
		return t;
	}
}
