package itu.dd.client.tech;

import itu.dd.client.domain.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;


import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public final class XmlBuilder {
	private static final String TRIP = "trip";
	private static final String EVENT = "event";
	private static final String EVENTTYPE = "eventType";
	private static final String DATETIME = "dateTime";
	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String DATA = "data";
	private static final String MOOD = "mood";
	private static final String STARTDATETIME = "startDateTime";
	private static final String EVENTS = "events";
	private static final String TRIP_NAME = "name";
	private static final String SENDER = "sender";
	private static final String RECEIVER = "receiver";
	private static final String MESSAGE = "message";

	static String buildXmlFromEvents(List<Event> eventList) throws IOException{
		XmlSerializer serializer = Xml.newSerializer();
		final StringWriter writer = new StringWriter();

		serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", EVENTS);
        int length = eventList.size();
        for(int i = 0; i < length; i++)
        	XmlBuilder.addEventXml(serializer, eventList.get(i));
        serializer.endTag("", EVENTS);
        serializer.endDocument();
       
        return writer.toString();
	}

	static void addReadingXml(XmlSerializer serializer, MoodEvent r)
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", MOOD);
		serializer.text(String.valueOf(r.getMood()));
		serializer.endTag("", MOOD);
	}

	static void addIncomingCallXml(XmlSerializer serializer,
			IncomingCallEvent i) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", SENDER);
		serializer.text(String.valueOf(i.getPhonenumber()));
		serializer.endTag("", SENDER);
	}

	static void addOutgoingCallXml(XmlSerializer serializer,
			OutgoingCallEvent o) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", RECEIVER);
		serializer.text(String.valueOf(o.getPhonenumber()));
		serializer.endTag("", RECEIVER);
	}
	
	static void addSMSXml(XmlSerializer serializer,
			SmsEvent e) throws IllegalArgumentException,
			IllegalStateException, IOException {
		if(IncomingSmsEvent.class.isInstance(e)) {
			serializer.startTag("", SENDER);
			serializer.text(String.valueOf(e.getPhonenumber()));
			serializer.endTag("", SENDER);
		} else if(OutgoingSmsEvent.class.isInstance(e)) {
			serializer.startTag("", RECEIVER);
			serializer.text(String.valueOf(e.getPhonenumber()));
			serializer.endTag("", RECEIVER);
		}
		serializer.startTag("", MESSAGE);
		serializer.text(String.valueOf(e.getTextMessage()));
		serializer.endTag("", MESSAGE);
	}

	static void addEventXml(XmlSerializer serializer, Event e)
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", EVENT);
		serializer.startTag("", EVENTTYPE);
		// Type specific
		if (LocationEvent.class.isInstance(e))
			serializer.text("event");
		else if (MoodEvent.class.isInstance(e))
			serializer.text("mood");
		else if (CallEvent.class.isInstance(e))
			serializer.text("call");
		else if (SmsEvent.class.isInstance(e))
			serializer.text("sms");

		serializer.endTag("", EVENTTYPE);
		serializer.startTag("", DATETIME);
		serializer.text(String.valueOf(e.getDateTime()));
		serializer.endTag("", DATETIME);
		serializer.startTag("", LONGITUDE);
		serializer.text(String.valueOf(e.getLongitude()));
		serializer.endTag("", LONGITUDE);
		serializer.startTag("", LATITUDE);
		serializer.text(String.valueOf(e.getLatitude()));
		serializer.endTag("", LATITUDE);
		serializer.startTag("", DATA);
		// Type specific
		if (MoodEvent.class.isInstance(e))
			addReadingXml(serializer, (MoodEvent) e);
		else if (IncomingCallEvent.class.isInstance(e))
			addIncomingCallXml(serializer, (IncomingCallEvent) e);
		else if (OutgoingCallEvent.class.isInstance(e))
			addOutgoingCallXml(serializer, (OutgoingCallEvent) e);
		else if (IncomingSmsEvent.class.isInstance(e))
			addSMSXml(serializer, (SmsEvent) e);
		else if (OutgoingSmsEvent.class.isInstance(e))
			addSMSXml(serializer, (SmsEvent) e);
		
		serializer.endTag("", DATA);
		serializer.endTag("", EVENT);
	}

	static String buildXmlFromTrip(Trip t) throws IOException {
		XmlSerializer serializer = Xml.newSerializer();
		final StringWriter writer = new StringWriter();

		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", TRIP);
		serializer.startTag("", EVENTS);
		for (Event e : t.getEvents()) {
			addEventXml(serializer, e);
		}
		serializer.endTag("", EVENTS);
		serializer.startTag("", STARTDATETIME);
		serializer.text(String.valueOf(t.getStartDate()));
		serializer.endTag("", STARTDATETIME);
		serializer.startTag("", TRIP_NAME);
		serializer.text(t.getName());
		serializer.endTag("", TRIP_NAME);
		serializer.endTag("", TRIP);
		serializer.endDocument();

		return writer.toString();
	}
}
