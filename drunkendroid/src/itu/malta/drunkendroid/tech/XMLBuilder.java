package itu.malta.drunkendroid.tech;

import itu.malta.drunkendroid.domain.*;
import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public final class XMLBuilder {
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

	protected static String buildXmlFromStandAloneEvent(Event e)
			throws IOException {
		XmlSerializer serializer = Xml.newSerializer();
		final StringWriter writer = new StringWriter();

		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", EVENTS);
		XMLBuilder.addEventXml(serializer, e);
		serializer.endTag("", EVENTS);
		serializer.endDocument();

		return writer.toString();
	}

	protected static void addReadingXml(XmlSerializer serializer, ReadingEvent r)
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", MOOD);
		serializer.text(String.valueOf(r.mood));
		serializer.endTag("", MOOD);
	}

	protected static void addIncomingCallXml(XmlSerializer serializer,
			IncomingCallEvent i) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", SENDER);
		serializer.text(String.valueOf(i.getPhonenumber()));
		serializer.endTag("", SENDER);
	}

	protected static void addOutgoingCallXml(XmlSerializer serializer,
			OutgoingCallEvent o) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", RECEIVER);
		serializer.text(String.valueOf(o.getPhonenumber()));
		serializer.endTag("", RECEIVER);
	}
	
	protected static void addSMSXml(XmlSerializer serializer,
			SMSEvent e) throws IllegalArgumentException,
			IllegalStateException, IOException {
		if(IncomingSMSEvent.class.isInstance(e)) {
			serializer.startTag("", SENDER);
			serializer.text(String.valueOf(e.getPhonenumber()));
			serializer.endTag("", SENDER);
		} else if(OutgoingSMSEvent.class.isInstance(e)) {
			serializer.startTag("", RECEIVER);
			serializer.text(String.valueOf(e.getPhonenumber()));
			serializer.endTag("", RECEIVER);
		}
		serializer.startTag("", MESSAGE);
		serializer.text(String.valueOf(e.getTextMessage()));
		serializer.endTag("", MESSAGE);
	}

	protected static void addEventXml(XmlSerializer serializer, Event e)
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", EVENT);
		serializer.startTag("", EVENTTYPE);
		// Type specific
		if (LocationEvent.class.isInstance(e))
			serializer.text("event");
		else if (ReadingEvent.class.isInstance(e))
			serializer.text("reading");
		else if (CallEvent.class.isInstance(e))
			serializer.text("call");
		else if (SMSEvent.class.isInstance(e))
			serializer.text("sms");

		serializer.endTag("", EVENTTYPE);
		serializer.startTag("", DATETIME);
		serializer.text(String.valueOf(e.dateTime));
		serializer.endTag("", DATETIME);
		serializer.startTag("", LONGITUDE);
		serializer.text(String.valueOf(e.longitude));
		serializer.endTag("", LONGITUDE);
		serializer.startTag("", LATITUDE);
		serializer.text(String.valueOf(e.latitude));
		serializer.endTag("", LATITUDE);
		serializer.startTag("", DATA);
		// Type specific
		if (ReadingEvent.class.isInstance(e))
			addReadingXml(serializer, (ReadingEvent) e);
		else if (IncomingCallEvent.class.isInstance(e))
			addIncomingCallXml(serializer, (IncomingCallEvent) e);
		else if (OutgoingCallEvent.class.isInstance(e))
			addOutgoingCallXml(serializer, (OutgoingCallEvent) e);
		else if (IncomingSMSEvent.class.isInstance(e))
			addSMSXml(serializer, (SMSEvent) e);
		else if (OutgoingSMSEvent.class.isInstance(e))
			addSMSXml(serializer, (SMSEvent) e);
		
		serializer.endTag("", DATA);
		serializer.endTag("", EVENT);
	}

	protected static String buildXmlFromTrip(Trip t) throws IOException {
		XmlSerializer serializer = Xml.newSerializer();
		final StringWriter writer = new StringWriter();

		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", TRIP);
		serializer.startTag("", EVENTS);
		for (Event e : t.getTripEvents()) {
			addEventXml(serializer, e);
		}
		serializer.endTag("", EVENTS);
		serializer.startTag("", STARTDATETIME);
		serializer.text(String.valueOf(t.getStartDate()));
		serializer.endTag("", STARTDATETIME);
		serializer.startTag("", TRIP_NAME);
		//TODO Implement Trip Name
		serializer.text("Gin Saturdays");
		serializer.endTag("", TRIP_NAME);
		serializer.endTag("", TRIP);
		serializer.endDocument();

		return writer.toString();
	}
}
