package itu.dd.server.util.xstreem.converters;

import java.util.ArrayList;
import java.util.LinkedList;

import itu.dd.server.domain.CallEvent;
import itu.dd.server.domain.LocationEvent;
import itu.dd.server.domain.MoodEvent;
import itu.dd.server.domain.SmsEvent;
import itu.dd.server.interfaces.IEvent;

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
				if(event.getType().equals("mood")){
					MoodEvent moodEvent = (MoodEvent) event;
					writer.startNode("data");
					writer.startNode("mood");
					writer.setValue(Integer.toString(moodEvent.getMood()));
					writer.endNode();
					writer.endNode();	
				} else if(event.getType().equals("call")) {
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
						writer.startNode("caller");
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

		} else if (MoodEvent.class.isInstance(value) || LocationEvent.class.isInstance(value) || CallEvent.class.isInstance(value) || SmsEvent.class.isInstance(value) ) {
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
		}

	}


	/**
	 * Method for un-marshaling an Event from XML into an object
	 */

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Long timeStamp = 0L;
		Double longitude = -1D;
		Double latitude = -1D;
		int mood = -1;
		String caller = "0";
		String receiver = "0";
		long endTime = -1;
		String message = "";
		String sender ="0";

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
								eventType = reader.getValue();
							} else if("dateTime".equals(reader.getNodeName())) {
								timeStamp = Long.valueOf(reader.getValue());

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
						events.add(new CallEvent(timeStamp,latitude,longitude,caller,receiver,endTime));
					} else if (eventType.equals("SMS")) {
						events.add(new SmsEvent(timeStamp,latitude,longitude,sender,receiver,message));
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
		if(clazz.equals(MoodEvent.class)) {
			return true;
		} else if (clazz.equals(LocationEvent.class)) {
			return true;
		} else if (clazz.equals(CallEvent.class)) {
			return true;
		} else if (clazz.equals(SmsEvent.class)) {
			return true;
		}
		return false;
	}
}
