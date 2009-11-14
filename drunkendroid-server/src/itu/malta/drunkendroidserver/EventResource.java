package itu.malta.drunkendroidserver;


import itu.malta.drunkendroidserver.util.XmlResponse;

import java.sql.SQLException;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.resource.ResourceException;
import org.restlet.representation.Representation;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.NodeSet;
import org.w3c.dom.DOMException;

/**
 * 
 * Class that handles requests for posting Events.
 *
 */
public class EventResource extends ServerResource {
	private String imeiNumber;
	private long tripId;

	
	
	/**
	 * 
	 * @param entity
	 * @return either null for a successful addition of one or more events or a xml document containing the error message. 
	 * @throws ResourceException
	 */
	@Post
	public Representation storeRepresentation(Representation entity) throws ResourceException {
		DomRepresentation result = null;
		//not being used atm.
		imeiNumber = (String) getRequest().getAttributes().get("IMEI");
		//get the tripID
		tripId = (Long) getRequest().getAttributes().get("TripId");
		
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			
			//Build the DOMTree
			DomRepresentation domDocument = new DomRepresentation(entity);
			
			try{
				long eventTime;
				double longitude,latitude;
				String eventType;
				// get the events, there might be multiple
				NodeSet events = domDocument.getNodes("//events/event");
				for (int i = 0;i < events.size(); i++) {
					int mood;
					
					//Get the readings variables
					eventTime = Long.parseLong(domDocument.getNode("//trip/events/event[" + i+1 + "]/dateTime").getTextContent()); 
					latitude = Double.parseDouble(domDocument.getNode("//trip/events/event[" + i+1 + "]/latitude").getTextContent());
					longitude = Double.parseDouble(domDocument.getNode("//trip/events/event[" + i+1 + "]/longitude").getTextContent());
					eventType = domDocument.getNode("//trip/events/event/eventType").getTextContent(); 
					
					if(eventType == "reading") {
						mood = Integer.parseInt(domDocument.getNode("//trip/events/event[" + i+1 + "]/data/mood").getTextContent()); 
						// insert the DBInsertReadingCommand
						Reading reading  = new Reading(eventTime,latitude,longitude,mood);
						reading.setTripID(tripId);
						reading.execute();
					}
					if(eventType == "sms") {
						//To be implemented
						//String smsText = domDocument.getNode("//trip/events/event[" + i+1 + "]/data/sms").getTextContent(); 
					}
					if(eventType == "call") {
						// To be implemented
					}
					if(eventType == "photo") {
						// To be implemented
					}
					if(eventType == "video") {
						// To be implemented
					}
					
					
				}				
				// set the status and build an response 
				setStatus(Status.SUCCESS_CREATED);
		        
			} catch (DOMException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed XML", "2");
			} catch (NumberFormatException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed Data in XML", "3");
			} catch (SQLException e) {
				setStatus(Status.SERVER_ERROR_INTERNAL);
				result = XmlResponse.generateErrorRepresentation("Error inserting data in database", "4");				
			}
			
		} else {
		// not text/XML format
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Payload not text/XML", "1");

		}
		return result;
		
	}
}

