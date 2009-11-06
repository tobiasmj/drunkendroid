package drunkendroidserver;

import java.io.IOException;
import java.sql.SQLException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.resource.ResourceException;
import org.restlet.representation.Representation;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.NodeSet;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import drunkendroidserver.Util.XmlResponse;

public class TripResource extends ServerResource {
	private String imeiNumber;
	private Long startTime,endTime;
	private long returnId;
	private String name;
	private int tripId;
	
	
	@Post
	public Representation storeRepresentation(Representation entity) throws ResourceException {
		DomRepresentation result = null;
		imeiNumber = (String) getRequest().getAttributes().get("IMEI");
		
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			
			//Build the DOMTree
			DomRepresentation domDocument = new DomRepresentation(entity);
			
			//Assuming only one trip per post, get the startTime and endTime
			try{
				startTime = Long.parseLong(domDocument.getNode("//trip/startDateTime").getTextContent());
				endTime = Long.parseLong(domDocument.getNode("//trip/endDateTime").getTextContent());
				name = domDocument.getNode("//trip/name").getTextContent();
				// insert the DBinsertTripCommand
				InsertTrip tripComm;
				tripComm = new InsertTrip(imeiNumber,startTime,endTime,name);
				
				
				long eventTime;
				double longitude,latitude;
				String eventType;
				// get the events, there might be multiple
				NodeSet events = domDocument.getNodes("//trip/events/event");
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
						tripComm.addCommand(new InsertReading(eventTime,latitude,longitude,mood));
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
				// Commit the Transaction Object to the database
				returnId = tripComm.execute();
				
				// set the status and build an response 
				setStatus(Status.SUCCESS_CREATED);
		        try {  
		        	result = new DomRepresentation(MediaType.TEXT_XML);  
		  
		            Document d = result.getDocument();  
		  
		            Element eltTripId = d.createElement("tripId");  
		            eltTripId.appendChild(d.createTextNode(Long.toString(returnId)));
		            d.appendChild(eltTripId);
		            
		        } catch (IOException e) {
		        	setStatus(Status.SERVER_ERROR_INTERNAL);
		        	result = XmlResponse.generateErrorRepresentation("Error creating XML response.", "6");				
		        }  
			
			} catch (DOMException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed XML.", "2");
			} catch (NumberFormatException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed Data in XML.", "3");
			} catch (SQLException e) {
				setStatus(Status.SERVER_ERROR_INTERNAL);
				result = XmlResponse.generateErrorRepresentation("Error inserting data in database.", "4");				
			}
			
		} else {
		// not text/XML format
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Payload not text/XML.", "1");

		}
		return result;
		
	}
	
	@Put
	public Representation store(Representation entity) throws ResourceException {
		Representation result = null;
		
		imeiNumber = (String) getRequest().getAttributes().get("IMEI");
		
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			
			//Build the DOMTree
			DomRepresentation domDocument = new DomRepresentation(entity);
			
			//Assuming only one trip per post, get the startTime and endTime
			try{
				startTime = Long.parseLong(domDocument.getNode("//trip/startDateTime").getTextContent());
				endTime = Long.parseLong(domDocument.getNode("//trip/endDateTime").getTextContent());
				name = domDocument.getNode("//trip/name").getTextContent();
				tripId = Integer.parseInt(domDocument.getNode("//trip/tripId").getTextContent()); 
				// Create the updateObejct
				updateTrip tripComm;
				tripComm = new updateTrip(tripId,imeiNumber,startTime,endTime,name);
		
		
				tripComm.execute();
				
			} catch (DOMException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed XML.", "2");
			} catch (NumberFormatException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed Data in XML.", "3");
			} catch (SQLException e) {
				setStatus(Status.SERVER_ERROR_INTERNAL);
				result = XmlResponse.generateErrorRepresentation("Error inserting data in database.", "4");				
			}
			
		} else {
		// not text/XML format
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Payload not text/XML.", "1");

		}
		
		return result;
	
	}
	
	
	@Get  
    public Representation represent() {  
    	imeiNumber = (String) getRequest().getAttributes().get("IMEI");
    	tripId = Integer.getInteger(getRequest().getAttributes().get("TripId").toString());

    	DomRepresentation result = null;
    	try {
    		result = new getTrip(tripId).execute(); 
    	} catch (SQLException se) {
        	setStatus(Status.SERVER_ERROR_INTERNAL);
        	result = XmlResponse.generateErrorRepresentation("Error getting data from database.", "5");
    	} catch (IOException ioe) {
        	setStatus(Status.SERVER_ERROR_INTERNAL);
        	result = XmlResponse.generateErrorRepresentation("Error creating XML response.", "6");
    	}
    	
    	setStatus(Status.SUCCESS_OK);
    	return result;
    }  
}

