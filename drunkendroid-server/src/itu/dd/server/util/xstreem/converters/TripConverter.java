package itu.dd.server.util.xstreem.converters;

import java.util.LinkedList;

import itu.dd.server.domain.CallEvent;
import itu.dd.server.domain.LocationEvent;
import itu.dd.server.domain.MoodEvent;
import itu.dd.server.domain.SmsEvent;
import itu.dd.server.domain.Trip;
import itu.dd.server.interfaces.IEvent;

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
			if(event.getType().equals("mood")){
				MoodEvent moodEvent = (MoodEvent) event;
				writer.startNode("data");
				writer.startNode("mood");
				writer.setValue(Integer.toString(moodEvent.getMood()));
				writer.endNode();
				writer.endNode();	
			}  else if(event.getType().equals("call")) {
				CallEvent callEvent = (CallEvent) event;
				writer.startNode("data");
				if(!callEvent.getCaller().equals("0")) {
					writer.startNode("caller");
					writer.setValue(callEvent.getCaller());
					writer.endNode();
				}
				if(!callEvent.getreceiver().equals("0")) {
					writer.startNode("receiver");
					writer.setValue(callEvent.getreceiver());
					writer.endNode();
				}
				writer.startNode("endTime");
				writer.setValue(Long.toString(callEvent.getEndTime()));
				writer.endNode();
				writer.endNode();
			} else if(event.getType().equals("SMS")) {
				SmsEvent smsEvent = (SmsEvent) event;
				writer.startNode("data");
				if(!smsEvent.getSender().equals("0")) {	
					writer.startNode("sender");
					writer.setValue(smsEvent.getSender());
					writer.endNode();
				}
				if(!smsEvent.getreceiver().equals("0")) {
					writer.startNode("receiver");
					writer.setValue(smsEvent.getreceiver());
					writer.endNode();
				}
				writer.startNode("message");
				writer.setValue(smsEvent.getMessage());
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

		Long startDateTime = 0L, endDateTime = -1L, timeStamp = 0L;
		Double longitude = 0D, latitude = 0D;
		int mood = 0;
		String name = "";
		String eventType = "";
		String caller = "0";
		String receiver = "0";
		String message = "";
		String sender = "0";
		long endTime = -1;
		LinkedList<IEvent> events  = new LinkedList<IEvent>();

		while(reader.hasMoreChildren()) {

			reader.moveDown();
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
								/*if(timeStamp > signedHigh) {
									throw new NumberFormatException("timeStamp out of bounds");
								}*/

							} else if("longitude".equals(reader.getNodeName())) {
								longitude = Double.valueOf(reader.getValue());
							} else if("latitude".equals(reader.getNodeName())) {
								latitude = Double.valueOf(reader.getValue());
							} else if("data".equals(reader.getNodeName())) {
								while(reader.hasMoreChildren()) {
									reader.moveDown();
									if(eventType.equals("mood")) {
										if("mood".equals(reader.getNodeName())) {
											mood = Integer.valueOf(reader.getValue());
										}
									}else if(eventType.equals("call")) {
										if("caller".equals(reader.getNodeName())) {
											caller = String.valueOf(reader.getValue());
										} else if ("receiver".equals(reader.getNodeName())) {
											receiver = String.valueOf(reader.getValue());
										}else if ("endTime".equals(reader.getNodeName())) {
											endTime = Long.valueOf(reader.getValue());
										}
									}else if(eventType.equals("SMS")) {
										if("sender".equals(reader.getNodeName())) {
											sender = String.valueOf(reader.getValue());
										} else if ("receiver".equals(reader.getNodeName())) {
											receiver = String.valueOf(reader.getValue());
										}else if ("message".equals(reader.getNodeName())) {
											message = String.valueOf(reader.getValue());
										}
									}

									reader.moveUp();
								}
							}
							reader.moveUp();
						}
					}

					if(eventType.equals("mood")) {
						events.add(new MoodEvent(timeStamp,latitude,longitude,mood));	
					} else if(eventType.equals("location")) {
						events.add(new LocationEvent(timeStamp, longitude, latitude));
					} else if (eventType.equals("call")) {
						events.add(new CallEvent(timeStamp,latitude,latitude,caller,receiver,endTime));
					} else if (eventType.equals("SMS")) {
						events.add(new SmsEvent(timeStamp,latitude,longitude,sender,receiver,message));
					}
					reader.moveUp();
				}
				reader.moveUp();

			} else if("startDateTime".equals(reader.getNodeName())) {
				startDateTime = Long.valueOf(reader.getValue());
				reader.moveUp();
			} else if("endDateTime".equals(reader.getNodeName())) {
				endDateTime = Long.valueOf(reader.getValue());
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
