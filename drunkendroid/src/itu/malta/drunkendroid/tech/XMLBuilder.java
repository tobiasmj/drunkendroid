package itu.malta.drunkendroid.tech;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;

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
	
	
	
	protected static String buildXmlFromStandAloneEvent(Event e) throws IOException{
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
	
	protected static void addEventXml(XmlSerializer serializer, Event e) 
			throws IllegalArgumentException, IllegalStateException, IOException{
		serializer.startTag("", EVENT);
		serializer.startTag("", EVENTTYPE);
		//Type specific
		if(ReadingEvent.class.isInstance(e)){
			serializer.text("reading");
		}
		else{
			serializer.text("event");
		}
		
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
		//Type specific
		if(ReadingEvent.class.isInstance(e)){
			 addReadingXml(serializer, (ReadingEvent)e);
		}
		
		serializer.endTag("", DATA);
		serializer.endTag("", EVENT);
	}
	
	protected static String buildXmlFromTrip(Trip t) throws IOException{
		XmlSerializer serializer = Xml.newSerializer();
	    final StringWriter writer = new StringWriter();
	    
		serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", TRIP);
        serializer.startTag("", EVENTS);
        for(Event e : t.getTripEvents()){
        		addEventXml(serializer, e);	
        }
        serializer.endTag("", EVENTS);
        serializer.startTag("", STARTDATETIME);
        serializer.text(String.valueOf(t.getStartDate()));
        serializer.endTag("", STARTDATETIME);
        serializer.startTag("", TRIP_NAME);
        serializer.text("Gin Saturdays");
        serializer.endTag("", TRIP_NAME);
        serializer.endTag("", TRIP);
        serializer.endDocument();
       
        return writer.toString();
	}
}
