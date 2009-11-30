package itu.malta.drunkendroidserver.util.xstreem.converters;

import java.util.LinkedList;

import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.Reading;
import itu.malta.drunkendroidserver.domain.Trip;
import itu.malta.drunkendroidserver.interfaces.IEvent;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class TripConverter implements Converter{

	/**
	 * method for marshaling an Trip object into XML
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Trip trip = (Trip) value;
		// start events tag
		writer.startNode("events");
		// parse and combine events as xml string
		
		while (trip.moreEvents()) {
			IEvent event = trip.getNextEvent();
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

		// end the events node
		writer.endNode();

		// write trip info to xml
		writer.startNode("startDateTime");
		writer.setValue(Long.toString(trip.getStartTime()));
		writer.endNode();

		long endTime = trip.getEndTime();
		if (endTime != -1) {
			writer.startNode("endDateTime");
			writer.setValue(Long.toString(endTime));
			writer.endNode();		
		}

		writer.startNode("name");
		writer.setValue(trip.getName());
		writer.endNode();

	}
	/**
	 * Method for un-marshaling an Trip from XML into an object
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		int signedHigh = 2147483647;
		Long startDateTime = 0L, endDateTime = -1L, timeStamp = 0L;
		Double longitude = 0D, latitude = 0D;
		int mood = 0;
		String name = "";
		String eventType = "";
		LinkedList<IEvent> events  = new LinkedList<IEvent>();
		/*XStream xStream = new XStream();
		xStream.registerConverter(new EventConverter());
		xStream.alias("event", Reading.class);
		*/

		while(reader.hasMoreChildren()) {

			reader.moveDown();
			if ("events".equals(reader.getNodeName())) {

				while(reader.hasMoreChildren()) {
					reader.moveDown();

					//xStream.fromXML((PathTrackingReader)reader);

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
				//events.add(new Reading(timeStamp,latitude,longitude,mood));
				reader.moveUp();
				//reader.moveUp();
			} else if("startDateTime".equals(reader.getNodeName())) {
				startDateTime = Long.valueOf(reader.getValue());
				if(startDateTime > signedHigh) {
					throw new NumberFormatException("startDateTime out of bounds");
				}
				reader.moveUp();
			} else if("endDateTime".equals(reader.getNodeName())) {
				endDateTime = Long.valueOf(reader.getValue());
				if(endDateTime > signedHigh) {
					throw new NumberFormatException("endDateTime out of bounds");
				}
				reader.moveUp();
			} else if("name".equals(reader.getNodeName())) {
				name = reader.getValue();
				reader.moveUp();
			}
			
		}
		Trip trip = new Trip(startDateTime,endDateTime,name);
		trip.addEvents(events);
		
		return trip;
	}

	/**
	 * Method to determine what class that this converter can convert.
	 */
	@Override
	public boolean canConvert(Class clazz) {
		if(clazz.equals(Trip.class)) {
			return true;
		}
		return false;
	}
}
