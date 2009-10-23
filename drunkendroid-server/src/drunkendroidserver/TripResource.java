package drunkendroidserver;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.resource.ResourceException;
import org.restlet.representation.Representation;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.NodeSet;
import org.w3c.dom.DOMException;
import drunkendroidserver.Command.*;

// TODO: Handle exceptions in a structured manner, send HTTP error codes back to the client

public class TripResource extends ServerResource {
	private String imeiNumber;
	private Long startTime,endTime;
	private Transaction trans = new Transaction();
	@Post
	public void storeRepresentation(Representation entity) throws ResourceException {
		
		imeiNumber = (String) getRequest().getAttributes().get("IMEI");
		
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			
			//Build the DOMTree
			DomRepresentation domDocument = new DomRepresentation(entity);
			
			//Assuming only one trip per post, get the startTime and endTime
			try{
				startTime = Long.parseLong(domDocument.getNode("//trip/startDateTime").getTextContent());
			} catch (DOMException e) {
				// need to return an error due to malformed xml send by the client.
			} catch (NumberFormatException e) {
				// need to return an error due to malformed xml data send by the client.
			}
			try{
				endTime = Long.parseLong(domDocument.getNode("//trip/endDateTime").getTextContent());
			} catch (DOMException e) {
				// need to return an error due to malformed xml send by the client.
			} catch (NumberFormatException e) {
				// need to return an error due to malformed xml data send by the client.
			}
			
			// insert the DBinsertTripCommand
			DBInsertTripCommand tripComm;
			tripComm = new DBInsertTripCommand(imeiNumber,startTime,endTime);
			trans.addCommand(tripComm);
			
			// get the readings, there might be multiple
			NodeSet readings = domDocument.getNodes("//trip/readings/reading");
			for (int i = 0;i < readings.size(); i++) {
				long readingTime;
				double longtitude,latitude;
				int mood;
				
				
				//Get the readings variables
				try {
					readingTime = Long.parseLong(domDocument.getNode("//trip/readings/reading[" + i+1 + "]/dateTime").getTextContent()); 
					latitude = Double.parseDouble(domDocument.getNode("//trip/readings/reading[" + i+1 + "]/latitude").getTextContent());
					longtitude = Double.parseDouble(domDocument.getNode("//trip/readings/reading[" + i+1 + "]/longtitude").getTextContent());
					mood = Integer.parseInt(domDocument.getNode("//trip/readings/reading[" + i+1 + "]/mood").getTextContent());
					// insert the DBInsertReadingCommand
					tripComm.addCommand(new DBInsertReadingCommand(readingTime,latitude,longtitude,mood));
				} catch (DOMException e) {
					// need to return an error due to malformed xml send by the client.
				}
				
			}
			// Commit the Transaction Object to the database
			trans.Commit();
			
		} else {
			// not text/XML format
		}
			
		
	}
    @Get  
    public String represent() {  
    	imeiNumber = (String) getRequest().getAttributes().get("IMAI");
    	return imeiNumber;
    }
}

