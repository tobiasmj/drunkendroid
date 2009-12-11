package itu.dd.server;


import itu.dd.server.control.Repository;
import itu.dd.server.domain.CallEvent;
import itu.dd.server.domain.LocationEvent;
import itu.dd.server.domain.MoodEvent;
import itu.dd.server.domain.SmsEvent;
import itu.dd.server.interfaces.IEvent;
import itu.dd.server.tech.DatabaseConnection;
import itu.dd.server.util.XmlResponse;
import itu.dd.server.util.xstreem.converters.EventConverter;

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
	private long _IMEINumber;

	
	
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
		_IMEINumber = Long.parseLong((String)getRequest().getAttributes().get("IMEI"));
		
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			try{

				// setting up the repository 
				Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
				
				if (!rep.checkTripExist(_tripId, _IMEINumber)) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					result = XmlResponse.generateErrorRepresentation("A Trip with TripId:" + _tripId + " and IMEI number:" + _IMEINumber + " does not exsist", "8");
				}
				// setting up XStream for marshaling and un-marshalling events.
				XStream xStream = new XStream();
				xStream.registerConverter(new EventConverter());
				xStream.alias("events", MoodEvent.class);
				xStream.alias("events", LocationEvent.class);
				xStream.alias("events", CallEvent.class);
				xStream.alias("events", SmsEvent.class);
				
				// parse the xml
				Object uncastedEvents = xStream.fromXML(entity.getStream());

				LinkedList<?> events = (LinkedList<?>)uncastedEvents; 
				IEvent event;
				
				// iterate the events and create the prober class instances. 
				for (int i = 0; i < events.size(); i++ ) {
					event = (IEvent) events.get(i);
					event.setTripId(_tripId);
					if(MoodEvent.class.isInstance(event)) {
						MoodEvent mEvent = (MoodEvent)event;
						rep.insertMood(mEvent);
					} else if (LocationEvent.class.isInstance(event)) {
						LocationEvent lEvent = (LocationEvent)event;
						rep.insertLocation(lEvent);
					} else if (CallEvent.class.isInstance(event)) {
						CallEvent cEvent = (CallEvent)event;
						rep.insertCall(cEvent);
					} else if (SmsEvent.class.isInstance(event)) {
						SmsEvent sEvent = (SmsEvent)event;
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

