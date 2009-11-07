package itu.malta.drunkendroid.tech;

import java.io.IOException;
import java.io.StringWriter;
import java.net.ContentHandlerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import itu.malta.drunkendroid.domain.Reading;
import itu.malta.drunkendroid.domain.Trip;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xmlpull.v1.XmlSerializer;

import com.sun.xml.internal.ws.util.xml.ContentHandlerToXMLStreamWriter;

/**
 * This class handles connections to the server, with the REST protocol.
 * @author ExxKA
 * This class is inspired by the book: Beginning Android and the Restlet documentation
 */
public class RESTServerHelper {
	private static final String BASE_URI = "http://192.168.0.13:8182/drunkendroid/";
	private static final String TRIP_NAME = "name";
	private static final String TRIP = "trip";
	private static final String TRIPID = "tripId";
	private static final String STARTDATETIME = "startDateTime";
	private static final String ENDDATETIME = "endDateTime";
	private static final String EVENTS = "events";
	private static final String EVENT = "event";
	private static final String EVENTTYPE = "eventType";
	private static final String DATETIME = "dateTime";
	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String DATA = "data";
	private static final String MOOD = "mood";
	private String IMEI = "";
	
	
	public RESTServerHelper(Context context){
		TelephonyManager mgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		//IMEI = mgr.getDeviceId();
		IMEI = "4213371337";
	}
	
	public Long uploadTrip(Trip t) throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", TRIP);
	        serializer.startTag("", EVENTS);
	        for(Reading r : t.getTripReadings()){
	            serializer.startTag("", EVENT);
	            serializer.startTag("", EVENTTYPE);
	            serializer.text("reading");
	            serializer.endTag("", EVENTTYPE);
	            serializer.startTag("", DATETIME);
	            serializer.text(String.valueOf(r.getDate().getTimeInMillis()));
	            serializer.endTag("", DATETIME);
	            serializer.startTag("", LONGITUDE);
	            serializer.text(String.valueOf(r.getLongitude()));
	            serializer.endTag("", LONGITUDE);
	            serializer.startTag("", LATITUDE);
	            serializer.text(String.valueOf(r.getLatitude()));
	            serializer.endTag("", LATITUDE);
	            serializer.startTag("", DATA);
	            serializer.startTag("", MOOD);
	            serializer.text(String.valueOf(r.getMood()));
	            serializer.endTag("", MOOD);
	            serializer.endTag("", DATA);
	            serializer.endTag("", EVENT);
	        }
	        serializer.endTag("", EVENTS);
	        serializer.startTag("", STARTDATETIME);
	        serializer.text(String.valueOf(t.getStartDate().getTimeInMillis()));
	        serializer.endTag("", STARTDATETIME);
	        serializer.startTag("", ENDDATETIME);
	        serializer.text(String.valueOf(t.getStartDate().getTimeInMillis())); // Not correct
	        serializer.endTag("", ENDDATETIME);
	        serializer.startTag("", TRIP_NAME);
	        serializer.text("Gin Saturdays");
	        serializer.endTag("", TRIP_NAME);
	        serializer.endTag("", TRIP);
	        serializer.endDocument();
	        
	        System.out.println(writer);		
			
	        
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 

		
		return new Long(1);
	}
	
	/*
	public Long uploadTripT(Trip t) throws IllegalArgumentException, IOException {
		Long remoteTripId = null;
		//Create the payload
		try{
			//Start a representation of an xml document
			DomRepresentation xmlrep = new DomRepresentation(MediaType.TEXT_XML);
			Document xmlDoc = xmlrep.getDocument();
			//Add the content
			Element trip = xmlDoc.createElement(TRIP);
			Element startDateTime = xmlDoc.createElement(STARTDATETIME);
			startDateTime.appendChild(xmlDoc.createTextNode(String.valueOf(t.getStartDate().getTimeInMillis())));
			trip.appendChild(startDateTime);
			Element name = xmlDoc.createElement(TRIP_NAME);
			//right now, a trip cannot have a name.
			name.appendChild(xmlDoc.createTextNode("NONAME"));
			trip.appendChild(name);
			xmlDoc.appendChild(trip);
			
			//Add the events
			Element events = xmlDoc.createElement(EVENTS);
			trip.appendChild(events);
			for(Reading r : t.getTripReadings()){
				Element event = xmlDoc.createElement(EVENT);
				events.appendChild(event);
				//Add elements to the event
				Element dateTime = xmlDoc.createElement(DATETIME);
				Element longitude = xmlDoc.createElement(LONGITUDE);
				Element latitude = xmlDoc.createElement(LATITUDE);
				Element data = xmlDoc.createElement(DATA);
				//Add data to the elements
				dateTime.appendChild(xmlDoc.createTextNode(String.valueOf(r.getDate().getTimeInMillis())));
				longitude.appendChild(xmlDoc.createTextNode(String.valueOf(r.getLongitude())));
				latitude.appendChild(xmlDoc.createTextNode(String.valueOf(r.getLatitude())));
				//Append the elements
				event.appendChild(dateTime);
				event.appendChild(longitude);
				event.appendChild(latitude);
				event.appendChild(data);
				
				//Add the mood
				Element mood = xmlDoc.createElement(MOOD);
				mood.appendChild(xmlDoc.createTextNode(String.valueOf(r.getMood())));
				data.appendChild(mood);
				
				//Close the xml document
				xmlrep.setDocument(xmlDoc);
				
				//Just for debugging purposes
				Log.i("REST", xmlrep.getText());
				
				//Contact the server
				Client uploadClient = new Client(Protocol.HTTP);
				uploadClient.setConnectTimeout(10);
				Response response = uploadClient.post(BASE_URI + "trip/"+IMEI, xmlrep);
				if(!response.getStatus().isSuccess()){
					//Handle this propperly.
					//Check the wiki for different responsecodes
					throw new IllegalArgumentException("The Trip could not be uploaded due to a communication failure");
				}
				//Handle the response
				try{
					DomRepresentation responseXml = new DomRepresentation(response.getEntity());
					Document responseXmlDoc = responseXml.getDocument();
					NodeList children = responseXmlDoc.getChildNodes();
					for(int i = 0; i < children.getLength(); i++){
						Node n = children.item(i);
						if(n.getLocalName() == TRIPID){
							remoteTripId = Long.parseLong(n.getNodeValue());
						}
					}
				}
				catch(IOException e){
					//The response was wrong
					IOException extra = new IOException(e.getMessage()+": Couln't handle the response propperly");
					extra.setStackTrace(e.getStackTrace());
				}
			}
		}
		catch(IOException e){
			//This is strange. There should be no reason for an IO exception when the DOM is
			// created from memory.
			IOException extra = new IOException(e.getMessage() + ": ");
			extra.setStackTrace(e.getStackTrace());
			throw e;
		}
		
		
		//Handle the response
		return remoteTripId;
	}
	*/
}
