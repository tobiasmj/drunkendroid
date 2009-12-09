package itu.dd.server;

import itu.dd.server.control.Repository;
import itu.dd.server.domain.Trip;
import itu.dd.server.tech.DatabaseConnection;
import itu.dd.server.util.XmlResponse;
import itu.dd.server.util.xstreem.converters.TripConverter;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.resource.ResourceException;
import org.restlet.representation.Representation;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * Class that handles requests for adding, updating and retrieving Trips.
 *
 */
public class TripResource extends ServerResource {
	private String _imeiNumber;
	private long _returnId;
	private int _tripId;
	
	/**
	 * Method invoked for adding trips 
	 * @param entity
	 * @return either the generated trip id in an XML representation for a successful addition of one or more events
	 *  	   or a XML representation containing the error message. 
	 * @throws ResourceException
	 */
	@Post
	public Representation storeRepresentation(Representation entity) throws ResourceException {
		DomRepresentation result = null;
		_imeiNumber = (String) getRequest().getAttributes().get("IMEI");
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			
			//Assuming only one trip per post, get the startTime and endTime
			try{

				Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
				XStream xStream = new XStream();
				xStream.registerConverter(new TripConverter());
				xStream.alias("trip", Trip.class);
				Trip trip = (Trip) xStream.fromXML(entity.getStream());
				trip.setImeiNumber(_imeiNumber);
				_returnId = rep.insertTrip(trip);
				// set the status and build an response 
				setStatus(Status.SUCCESS_CREATED);
		        try {  
		        	result = new DomRepresentation(MediaType.TEXT_XML);  
		            Document d = result.getDocument();  
		            // add the tripid element to the representation.
		            Element eltTripId = d.createElement("tripId");  
		            eltTripId.appendChild(d.createTextNode(Long.toString(_returnId)));
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
			} catch (IOException e) {
        		setStatus(Status.SERVER_ERROR_INTERNAL);
        		result = XmlResponse.generateErrorRepresentation("Error creating XML response.", "6");				
        	}  
			
		} else {
		// not text/XML format
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Payload not text/XML.", "1");
		}
		return result;
	}
	
	
	/**
	 * Method invoked for updating a trip 
	 * @param entity
	 * @return either null for a successful edit or a xml document containing the error message. 
	 * @throws ResourceException
	 */
	@Put
	public Representation store(Representation entity) throws ResourceException {
		Representation result = null;
		
		_imeiNumber = (String) getRequest().getAttributes().get("IMEI");

		_tripId = Integer.parseInt((String)getRequest().getAttributes().get("TripId"));
		
		// Testing if the HTTP content-type is XML.
		if (entity.getMediaType().equals(MediaType.TEXT_XML,true)) {
			
			
			//Assuming only one trip per post, get the startTime and endTime
			try{

				Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
				XStream xStream = new XStream();
				xStream.registerConverter(new TripConverter());
				xStream.alias("trip", Trip.class);
				Trip trip = (Trip) xStream.fromXML(entity.getStream());
				trip.setTripId(_tripId);
				trip.setImeiNumber(_imeiNumber);
				rep.updateTrip(trip);
				
				setStatus(Status.SUCCESS_OK);
				
			} catch (DOMException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed XML.", "2");
			} catch (NumberFormatException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				result = XmlResponse.generateErrorRepresentation("Malformed Data in XML.", "3");
			} catch (SQLException e) {
				setStatus(Status.SERVER_ERROR_INTERNAL);
				result = XmlResponse.generateErrorRepresentation("Error inserting data in database.", "4");				
			} catch (IOException e) {
	        	setStatus(Status.SERVER_ERROR_INTERNAL);
	        	result = XmlResponse.generateErrorRepresentation("Error creating XML response.", "6");				
	        } 
			
		} else {
		// not text/XML format
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Payload not text/XML.", "1");

		}
		return result;
	
	}
	
	/**
	 * Method invoked for getting a specific trip. 
	 * @return either a trip in XML representation or a XML document containing the error message. 
	 */
	@Get  
    public Representation represent() {
		
		_imeiNumber = (String) getRequest().getAttributes().get("IMEI");
		_tripId = Integer.parseInt((String)getRequest().getAttributes().get("TripId"));

    	DomRepresentation result = null;
    	try {

    		Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
    		Trip trip =	rep.getTrip(new Trip(_tripId));
    		XStream xStream = new XStream();
    		xStream.registerConverter(new TripConverter());
    		xStream.alias("trip", Trip.class);
            //result = new DomRepresentation(MediaType.TEXT_XML, xStream.toXML(trip));

    		
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder builder = null;
    		Document document = null;
			
    		builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(xStream.toXML(trip))));

	        result = new DomRepresentation(MediaType.TEXT_XML);
	    	result.setDocument(document);
			
    	} catch (SQLException se) {
        	setStatus(Status.SERVER_ERROR_INTERNAL);
        	result = XmlResponse.generateErrorRepresentation("Error getting data from database.", "5");
    	} catch (IOException ioe) {
        	setStatus(Status.SERVER_ERROR_INTERNAL);
        	result = XmlResponse.generateErrorRepresentation("Error creating XML response.", "6");
    	}catch (ParserConfigurationException e) {
           	setStatus(Status.SERVER_ERROR_INTERNAL);
        	result = XmlResponse.generateErrorRepresentation("Error creating XML response.", "6");
		}catch (SAXException e) {
	       	setStatus(Status.SERVER_ERROR_INTERNAL);
        	result = XmlResponse.generateErrorRepresentation("Error creating XML response.", "6");
		}
    	
    	setStatus(Status.SUCCESS_OK);
    	return result;
    }  
}

