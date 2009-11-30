package itu.malta.drunkendroidserver.util.xstreem.converters;

import java.util.ArrayList;
import java.util.LinkedList;

import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.Reading;
import itu.malta.drunkendroidserver.interfaces.IEvent;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Class for converting an Event object to and from XML, for use with the XStream framework 
 */
public class EventConverter implements Converter{

/**
 * method for marshaling an Event object into XML
 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		if(ArrayList.class.isInstance(value)) {
			ArrayList<?> events = (ArrayList<?>) value;
			for (int i = 0; i < events.size(); i++) {
				IEvent event = (IEvent)events.get(i);
				writer.startNode("event");
				writer.startNode("eventType");
				writer.setValue(event.getType());
				writer.endNode();
				writer.startNode("dateTime");
				writer.setValue(Long.toString(event.getTimeStamp()));
				writer.endNode();
				writer.startNode("longitude");
				writer.setValue(Double.toString(event.getLongitude()));
				writer.endNode();
				writer.startNode("latitude");
				writer.setValue(Double.toString(event.getLatitude()));
				writer.endNode();
				if(event.getType().equals("reading")){
					Reading readingEvent = (Reading) event;
					writer.startNode("data");
					writer.startNode("mood");
					writer.setValue(Integer.toString(readingEvent.getMood()));
					writer.endNode();
					writer.endNode();	
				}
				writer.endNode();
			}

		} else if (Reading.class.isInstance(value) || Location.class.isInstance(value)) {
			IEvent event = (IEvent)value;
			writer.startNode("eventType");
			writer.setValue(event.getType());
			writer.endNode();
			writer.startNode("dateTime");
			writer.setValue(Long.toString(event.getTimeStamp()));
			writer.endNode();
			writer.startNode("longitude");
			writer.setValue(Double.toString(event.getLongitude()));
			writer.endNode();
			writer.startNode("latitude");
			writer.setValue(Double.toString(event.getLatitude()));
			writer.endNode();
			if(event.getType().equals("reading")){
				Reading readingEvent = (Reading) event;
				writer.startNode("data");
				writer.startNode("mood");
				writer.setValue(Integer.toString(readingEvent.getMood()));
				writer.endNode();
				writer.endNode();	
			}
		}

	}
	// parse and combine events as xml string


	/**
	 * Method for un-marshaling an Event from XML into an object
	 */
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		int signedHigh = 2147483647;
		Long timeStamp = 0L;
		Double longitude = -1D;
		Double latitude = -1D;
		int mood = -1;
		LinkedList<IEvent> events = new LinkedList<IEvent>();

		String eventType = "";

		while(reader.hasMoreChildren()) {

			if ("events".equals(reader.getNodeName())) {

				while(reader.hasMoreChildren()) {
					reader.moveDown();

					if ("event".equals(reader.getNodeName())) {
						while(reader.hasMoreChildren()) {
							reader.moveDown();
							if("eventType".equals(reader.getNodeName())) {
								// switching of types when implemented
								eventType = reader.getValue();
							} else if("dateTime".equals(reader.getNodeName())) {
								timeStamp = Long.valueOf(reader.getValue());
								if(timeStamp > signedHigh) {
									throw new NumberFormatException("timeStamp out of bounds");
								}

							} else if("longitude".equals(reader.getNodeName())) {
								longitude = Double.valueOf(reader.getValue());
							} else if("latitude".equals(reader.getNodeName())) {
								latitude = Double.valueOf(reader.getValue());
							} else if("data".equals(reader.getNodeName())) {
								reader.moveDown();
								if(eventType.equals("reading")) {
									if("mood".equals(reader.getNodeName())) {
										mood = Integer.valueOf(reader.getValue());
									}
								}
								reader.moveUp();
							}
							reader.moveUp();
						}
					}

					if(eventType.equals("reading")) {
						events.add(new Reading(timeStamp,latitude,longitude,mood));	
					} else if(eventType.equals("location")) {
						events.add(new Location(timeStamp, longitude, latitude));
					}
					reader.moveUp();
				}
			}
		}
		return events;
	}


	/**
	 * Method to determine what class that this converter can convert.
	 */
	@Override
	public boolean canConvert(Class clazz) {
		if(clazz.equals(Reading.class)) {
			return true;
		} else if (clazz.equals(Location.class)) {
			return true;
		}
		return false;
	}
}
