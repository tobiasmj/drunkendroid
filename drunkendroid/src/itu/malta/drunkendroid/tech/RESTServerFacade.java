package itu.malta.drunkendroid.tech;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import itu.malta.drunkendroid.control.IRemoteDataFacade;
import itu.malta.drunkendroid.domain.*;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.Xml;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

/**
 * This class handles connections to the server, with the REST protocol.
 * @author ExxKA
 * This class is inspired by the book: Beginning Android and http://www.smnirven.com/?p=15
 */
public class RESTServerFacade implements IRemoteDataFacade {
	private static final String TRIP_NAME = "name";
	private static final String TRIP = "trip";
	private static final String TRIPID = "tripId";
	private static final String STARTDATETIME = "startDateTime";
	private static final String EVENTS = "events";
	private static final String EVENT = "event";
	private static final String EVENTTYPE = "eventType";
	private static final String DATETIME = "dateTime";
	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String DATA = "data";
	private static final String MOOD = "mood";
	private static final String LOGTAG = "RESTServerHelper";
	private static final String MESSAGE = "message";
	private static final String MOODMAP = "moodmap";
	private String IMEI = "";
	IWebserviceConnection conn = null;
	
	public RESTServerFacade(Context context, IWebserviceConnection conn){
		TelephonyManager mgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = mgr.getDeviceId();
		this.conn = conn;
	}
	
	/**
	 * @return currently only returns events with moods. Used to generate a moodmap.
	 */
	public List<ReadingEvent> getReadingEvents(Long starTime, Long endTime, Double latitude, Double longitude, Long distance) {
		List<ReadingEvent> resultingTrip = null;
		
		//Call the server
		HttpResponse response = conn.get(MOODMAP +"/"+
				 String.valueOf(starTime) +"/"+ 
				 String.valueOf(endTime) +"/"+ 
				 String.valueOf(latitude) +"/"+ 
				 String.valueOf(longitude) +"/"+ 
				 String.valueOf(distance) +"/"+ 
				 String.valueOf(distance)); 
		//TODO Handle errors.
		
		try {
			//This might be null and should be handled.
			resultingTrip = consumeXmlFromMoodMap(response);
			if(resultingTrip == null)
				throw new AndroidRuntimeException(LOGTAG + ": Unhandled condition. MoodMap from server is null");
		} catch (IllegalStateException e) {
			//Wrong content has been supplied by the server.
			//Tell the user we cannot show the map.
			Log.i(LOGTAG, "Presenting the user with a dialogbox saying we cannot handle the request");
			//TODO Send an exception to the user.
			
		} catch (IOException e) {
			//An xml DOM object could not be build from the content in the HttpResponse.
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultingTrip;
	}
	
	/**
	 * This method expects a Trip which already has a remoteId.
	 * To obtain a remoteId call the upload function.
	 */
	public void updateTrip(Trip t, Event e) {
		try {
			//Build xml
	    	String xml;
			xml = buildXmlFromStandAloneEvent(e);
			//Now try to send it
	        HttpResponse response = conn.post(TRIP + "/" + EVENT + "/" +IMEI + "/" + 
	        		String.valueOf(t.getRemoteID()), xml);
	        
	        int responseCode = response.getStatusLine().getStatusCode();
	        if(responseCode >= 400 && responseCode < 500){
	        	//This is in the 400: We have done something wrong.
	        	Log.e(LOGTAG, "Tried to update a trip with startdate " + t.getStartDate().getTimeInMillis() + ", the server returned malformed xml");
	        }
	        else if(responseCode >= 500 && responseCode < 600){
	        	//This is in the 500 range
	        	int tries = 0;
	        	
	        	while(responseCode > 400 && tries++ < 3){
		        	try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						//Got an interrupt. No problem. Just proceed.
					}
		        	response = conn.post(TRIP + "/" + EVENT + "/" +IMEI + "/" + 
			        		String.valueOf(t.getRemoteID()), xml);
		      	}
	        } 
		} catch (IOException e1) {
			//The XML was not build properly. Log the problem and do nothing further.
			//We ought to tell the user about these problems.
			Log.e(LOGTAG, "The xml needed to post an update command, could not be generated: " + e1.getMessage());
		}
	}
	
	/**
	 * Blocking call
	 * @param t the trip to be uploaded to the server, with containing events.
	 * @return The foreign ID of the trip.Can be null, if there was a problem which could not be solved.
	 */
	public void uploadTrip(Trip t)
	{
		try {
	        //Build xml
	    	String xml = buildXmlFromTrip(t);
	        //Now try to send it
	        HttpResponse response = conn.post(TRIP + "/"+IMEI, xml);
	        //Status codes in the 400 range are logged in the consume method.
	        //These occur if the XML is malformed.
	        Long resultId = consumeTripUploadResponse(response);
	        if(response.getStatusLine().getStatusCode() <= 500){
        		//The server has experienced and error. Try over, for a maximum of 3 times.
        		int tries = 0;
        		while(tries < 3 && resultId == null){
        			//give the server some time to recover.
        			try {
        				Thread.sleep(1000);
						//wait for a second before trying over.
					} catch(InterruptedException e){
						//Just go on.
					}
        			finally{
						response = conn.post(TRIP + "/"+IMEI, xml);
						resultId = consumeTripUploadResponse(response);
        				tries += 1;
        			} 
        		}
        		t.setRemoteID(resultId);
        	}
	        //Everything is fine
        	else{
        		t.setRemoteID(resultId);
        	}
	    }
	    catch(IOException e){
	    	//Something was wrong when we tried to build up xml.
	    	Log.e(LOGTAG, e.getMessage());
	    }
	}
	
	private String buildXmlFromTrip(Trip t) throws IOException{
		XmlSerializer serializer = Xml.newSerializer();
	    final StringWriter writer = new StringWriter();
	    
		serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", TRIP);
        serializer.startTag("", EVENTS);
        for(Event e : t.getTripEvents()){
        		addEventXml(serializer, e);	
        }
        serializer.endTag("", EVENTS);
        serializer.startTag("", STARTDATETIME);
        serializer.text(String.valueOf(t.getStartDate().getTimeInMillis()));
        serializer.endTag("", STARTDATETIME);
        serializer.startTag("", TRIP_NAME);
        serializer.text("Gin Saturdays");
        serializer.endTag("", TRIP_NAME);
        serializer.endTag("", TRIP);
        serializer.endDocument();
       
        return writer.toString();
	}
	
	private String buildXmlFromStandAloneEvent(Event e) throws IOException{
		XmlSerializer serializer = Xml.newSerializer();
	    final StringWriter writer = new StringWriter();
	    
		serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", EVENTS);
        addEventXml(serializer, e);	
        serializer.endTag("", EVENTS);
        serializer.endDocument();
       
        return writer.toString();
	}

	private void addEventXml(XmlSerializer serializer, Event e) 
			throws IllegalArgumentException, IllegalStateException, IOException{
		serializer.startTag("", EVENT);
        serializer.startTag("", EVENTTYPE);
        serializer.text("event");
        serializer.endTag("", EVENTTYPE);
        serializer.startTag("", DATETIME);
        serializer.text(String.valueOf(e.dateTime));
        serializer.endTag("", DATETIME);
        serializer.startTag("", LONGITUDE);
        serializer.text(String.valueOf(e.longitude));
        serializer.endTag("", LONGITUDE);
        serializer.startTag("", LATITUDE);
        serializer.text(String.valueOf(e.latitude));
        serializer.endTag("", LATITUDE);
        serializer.startTag("", DATA);
        if(ReadingEvent.class.isInstance(e)){
        	 this.addReadingXml(serializer, (ReadingEvent)e);
        }
        serializer.endTag("", DATA);
        serializer.endTag("", EVENT);
	}
	private void addReadingXml(XmlSerializer serializer, ReadingEvent r) 
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", MOOD);
        serializer.text(String.valueOf(r.mood));
        serializer.endTag("", MOOD);	
	}
	
	private Long consumeTripUploadResponse(HttpResponse response) throws IOException, IllegalStateException{
		try{
        	//Getting ready to proceed
	        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        	DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        	Document xmlDoc = docBuilder.parse(response.getEntity().getContent());
    		//Work out the status codes.
        
        	if(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300){
	        	//Everything works
        		NodeList nodes = xmlDoc.getElementsByTagName(TRIPID);
        		Node tripId = nodes.item(0);
        		Node tripIdContent = tripId.getFirstChild();
        		if(tripIdContent.getNodeType() == Node.TEXT_NODE ){
        			String value = tripIdContent.getNodeValue();
        			return Long.parseLong(value);   	
        		}
        		else{
        			Log.e(LOGTAG,"Couldn't find the content for the tripId");
        			return null;
        		}
        	}
	        else{
	        	//There was a problem.
	        	//If it's a status 400 this is all there will be done.
	        	//a code 500 will result in additional tries in the upload method.
	        	NodeList nodes = xmlDoc.getElementsByTagName(MESSAGE);
        		Node messageContent = nodes.item(0).getFirstChild();
        		if(messageContent.getNodeType() == Node.TEXT_NODE){
        			Log.e(LOGTAG, messageContent.getNodeValue());
        		}
        		
	        	return null;
	        }
    	}
    	catch(SAXException e){
    		//The server has send some content which is not xml
    		// This often happens if an error is caught by the RESTServer framework
    		// which is not handled by our server implementation.
    		//Log problem
    		Log.e(LOGTAG, e.getMessage());
    		return null;
    	}
    	catch(ParserConfigurationException e){
    		//Log problem
    		Log.e(LOGTAG, e.getMessage());
    		return null;
    	}
	}
	
	/**
	 * 
	 * @param response from a get moodmap call to the server
	 * @return a Trip build up from the provided xml
	 * @throws IOException If the provided response has no content.
	 * @throws IllegalStateException  If the provided response is in an illegal state(it might have been read before)
	 */
	private List<ReadingEvent> consumeXmlFromMoodMap(HttpResponse response) throws IllegalStateException, IOException {
		final String MOOD_MAP_READING = "MoodMapReading";
		final String MOOD_MAP_MOOD = "MoodMapValue";
		final String MOOD_MAP_LONG = "MoodMapLongitude";
		final String MOOD_MAP_LAT = "MoodMapLatitude";
		try{
        	//Getting ready to proceed
	        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        	DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        	Document xmlDoc = docBuilder.parse(response.getEntity().getContent());
    		//Work out the status codes.
        
        	if(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300){
	        	//Everything works build the trip.
        		List<ReadingEvent> events = new ArrayList<ReadingEvent>();
        		Long currentTime = Calendar.getInstance().getTimeInMillis();
        		
        		NodeList nodes = xmlDoc.getElementsByTagName(MOOD_MAP_READING);
        		//Traversing over MoodMapReading elements in the map. It'll contain 
        		for(int i = 0; nodes.getLength() > i; i++){
        			Node n = nodes.item(i);
        			NodeList eventValues = n.getChildNodes();
        			
        			if(eventValues.getLength() < 3){
        				throw new IllegalStateException(LOGTAG + ": Expected atleast 3 elements in a MoodReading");
        			}
        			
        			//Building events
        			Integer mood = null;
        			Double latitude = null;
        			Double longitude = null;
        			//Traversing over the specific values
        			for(int j = 0; eventValues.getLength() > j; j++){
        				Node nEventValue = eventValues.item(j);
        				if(nEventValue.getNodeName() == MOOD_MAP_MOOD){
        					mood = Integer.parseInt( nEventValue.getNodeValue() );
        				}
        				if(nEventValue.getNodeName() == MOOD_MAP_LONG){
        					longitude = Double.parseDouble( nEventValue.getNodeValue() );
        				}
        				if(nEventValue.getNodeName() == MOOD_MAP_LAT){
        					latitude = Double.parseDouble( nEventValue.getNodeValue() );
        				}
        			}
        			//Check that all values have been discovered
        			if(mood == null || latitude == null || longitude == null){
        				Log.e(LOGTAG, "Expected atleast a mood, longitude and latitude. But not all values where found");
        				throw new IllegalStateException(LOGTAG + ": Expected values(mood, lat, long) weren't found in the xml");
        			}
        			//adding the result.
        			//insert the current time, since no time is supplied by the map from the server.
        			events.add(new ReadingEvent(currentTime, latitude, longitude, mood));
        		}
        		
        		return events;
        	}
	        else{
	        	//There was a problem.
	        	//If it's a status 400 this is all there will be done.
	        	//a code 500 will result in additional tries in the upload method.
	        	NodeList nodes = xmlDoc.getElementsByTagName(MESSAGE);
        		Node messageContent = nodes.item(0).getFirstChild();
        		if(messageContent.getNodeType() == Node.TEXT_NODE){
        			Log.e(LOGTAG, messageContent.getNodeValue());
        		}
	        	return null;
	        }
    	}
    	catch(SAXException e){
    		//The server has send some content which is not xml
    		// This often happens if an error is caught by the RESTServer framework
    		// which is not handled by our server implementation.
    		//Log problem
    		Log.e(LOGTAG, e.getMessage());
    		return null;
    	}
    	catch(ParserConfigurationException e){
    		//Log problem
    		Log.e(LOGTAG, e.getMessage());
    		return null;
    	}	
	}
	
}