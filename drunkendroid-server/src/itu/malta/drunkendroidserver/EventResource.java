package itu.malta.drunkendroidserver;


import itu.malta.drunkendroidserver.control.Repository;
import itu.malta.drunkendroidserver.domain.Call;
import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.Mood;
import itu.malta.drunkendroidserver.domain.Sms;
import itu.malta.drunkendroidserver.interfaces.IEvent;
import itu.malta.drunkendroidserver.tech.DatabaseConnection;
import itu.malta.drunkendroidserver.util.XmlResponse;
import itu.malta.drunkendroidserver.util.xstreem.converters.EventConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.resource.ResourceException;
import org.restlet.representation.Representation;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * Class that handles requests for adding Events.
 *
 */
public class EventResource extends ServerResource {
	//private String imeiNumber;
	private long _tripId;

	
	
	/**
	 * Method invoked for adding events 
	 * @param entity
	 * @return either null for a successful addition of one or more events or a xml document containing the error message. 
	 * @throws ResourceException
	 */
	@Post
	public Representation storeRepresentation(Representation entity) throws ResourceException {
		DomRepresentation result = null;
		//not being used atm.
		//imeiNumber = (String) getRequest().getAttributes().get("IMEI");
		//get the tripID
		_tripId = Integer.parseInt((String)getRequest().getAttributes().get("TripId"));

		
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			try{

				// setting up the repository 
				Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
				// setting up XStream for marshaling and un-marshalling events.
				XStream xStream = new XStream();
				xStream.registerConverter(new EventConverter());
				xStream.alias("events", Mood.class);
				xStream.alias("events", Location.class);
				xStream.alias("events", Call.class);
				xStream.alias("events", Sms.class);
				
				// parse the xml
				Object uncastedEvents = xStream.fromXML(entity.getStream());

				LinkedList<?> events = (LinkedList<?>)uncastedEvents; 
				IEvent event;
				
				// iterate the events and create the prober class instances. 
				for (int i = 0; i < events.size(); i++ ) {
					event = (IEvent) events.get(i);
					event.setTripId(_tripId);
					if(Mood.class.isInstance(event)) {
						Mood mEvent = (Mood)event;
						rep.insertMood(mEvent);
					} else if (Location.class.isInstance(event)) {
						Location lEvent = (Location)event;
						rep.insertLocation(lEvent);
					} else if (Call.class.isInstance(event)) {
						Call cEvent = (Call)event;
						rep.insertCall(cEvent);
					} else if (Sms.class.isInstance(event)) {
						Sms sEvent = (Sms)event;
						rep.insertSms(sEvent);
					}
				}
				
				// set the status and build an response 
				setStatus(Status.SUCCESS_CREATED);
			} catch (SQLException e) {
				setStatus(Status.SERVER_ERROR_INTERNAL);
				result = XmlResponse.generateErrorRepresentation("Error inserting data in database", "4");	
			} catch (IOException e) {
				setStatus(Status.SERVER_ERROR_INTERNAL);

			}
		} else {
			// not text/XML format
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Payload not text/XML", "1");

		}
		return result;
		
	}
}

