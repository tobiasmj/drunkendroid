package itu.dd.client.tech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import itu.dd.client.domain.*;
import itu.dd.client.tech.exception.CommunicationException;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class handles connections to the server, with the REST protocol.
 * @author ExxKA
 * This class is inspired by the book: Beginning Android and http://www.smnirven.com/?p=15
 */
public class RestAdapter implements IRemoteDataFacade {
	private static final String _TRIP = "trip";
	private static final String _TRIPID = "tripId";
	private static final String EVENT = "event";
	private static final String LOGTAG = "RESTServerHelper";
	private static final String MESSAGE = "message";
	private static final String MOODMAP = "moodmap";
	private String _IMEI = "";
	IWebserviceConnection conn = null;
	
	public RestAdapter(Context context, IWebserviceConnection conn){
		TelephonyManager mgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		_IMEI = mgr.getDeviceId();
		this.conn = conn;
	}

	/**
	 * If errors occur an empty map is returned.
	 * @return currently only returns events with moods. Used to generate a moodmap.
	 */
	public ArrayList<MoodEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude) throws CommunicationException{
		
		ArrayList<MoodEvent> resultingTrip = null;
		//Call the server
		HttpResponse response = conn.get(MOODMAP +"/"+
				 String.valueOf(starTime) +"/"+ 
				 String.valueOf(endTime) +"/"+ 
				 String.valueOf(ulLatitude) +"/"+ 
				 String.valueOf(ulLongitude) +"/"+ 
				 String.valueOf(lrLatitude) +"/"+ 
				 String.valueOf(lrLongitude)); 
		try {
			//This might be null and should be handled.
			 resultingTrip = consumeXmlFromMoodMap(response);
			if(resultingTrip == null){
				Log.i(LOGTAG, "The response could not be consumed correctly");
				throw new CommunicationException(LOGTAG, "The server did not send a map back.");
				
			}
		} catch (IllegalStateException e) {
			//Wrong content has been supplied by the server.
			//Tell the user we cannot show the map.
			Log.i(LOGTAG, "IllegalStateException, " + e.getMessage() );
			throw new CommunicationException(LOGTAG, "The server has send a map which I cannot understand. Please update the application");
		} catch (IOException e) {
			//An xml DOM object could not be build from the content in the HttpResponse.
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(LOGTAG, "IOException, " + e.getMessage());
			throw new CommunicationException(LOGTAG, "The server did not send a correct map.");
		}
		
		return resultingTrip;
	}
	
	/**
	 * Blocking call
	 * This method expects a Trip which already has a remoteId.
	 * To obtain a remoteId call the upload function.
	 * @throws CommunicationException to indicate failure which could not be handled.
	 */
	synchronized public void updateTrip(Trip t, ArrayList<Event> events) throws CommunicationException {
		if(t.getRemoteId() == null){
			String cause = "Tried to update a trip which has no remoteId";
			throw new CommunicationException(LOGTAG, cause);
		}
		
		try {
			//Build xml
	    	String xml;
			xml = XmlBuilder.buildXmlFromEvents(events);
			//Now try to send it
	        HttpResponse response = conn.post(_TRIP + "/" + EVENT + "/" +_IMEI + "/" + 
	        		String.valueOf(t.getRemoteId()), xml);
	        
	        int responseCode = response.getStatusLine().getStatusCode();
	        if(responseCode >= 400 && responseCode < 500){
	        	//This is in the 400: We have done something wrong.
	        	Log.e(LOGTAG, "Tried to update a trip with startdate " + t.getStartDate() + ", the server returned malformed xml");
	        	throw new CommunicationException(LOGTAG, "Got a reponse code in the 400 series");
	        }
	        else if(responseCode >= 500 && responseCode < 600){
	        	//This is in the 500 range
	        	//Something is wrong on the server
	        	//Try to connect a few times more.
	        	int tries = 0;
	        	while(responseCode > 400 && tries++ < 3){
	        		Log.e(LOGTAG, "Post call, got responseCode " + responseCode + ". This is try " + tries);
		        	try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						//Got an interrupt. No problem. Just proceed.
					}
		        	response = conn.post(_TRIP + "/" + EVENT + "/" +_IMEI + "/" + 
			        		String.valueOf(t.getRemoteId()), xml);
		        	responseCode = response.getStatusLine().getStatusCode();
		      	}
	        } 
		} catch (IOException e1) {
			//The XML was not build properly.
			String cause = "The xml needed to post an update command, could not be generated: " + e1.getMessage();
			Log.e(LOGTAG, cause);
			throw new CommunicationException(LOGTAG, cause , e1);
		}
	}
	
	/**
	 * Blocking call
	 * @param t the trip to be uploaded to the server, with containing events.
	 * @return The foreign ID of the trip.Can be null, if there was a problem which could not be solved.
	 */
	synchronized public void uploadTrip(Trip t) throws CommunicationException
	{
		try {
	        //Build xml
	    	String xml = XmlBuilder.buildXmlFromTrip(t);
	        //Now try to send it
	        HttpResponse response = conn.post(_TRIP + "/"+_IMEI, xml);
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
						response = conn.post(_TRIP + "/"+_IMEI, xml);
						resultId = consumeTripUploadResponse(response);
        				tries += 1;
        			} 
        		}
        		t.setRemoteId(resultId);
        	}
	        //Everything is fine
        	else{
        		t.setRemoteId(resultId);
        	}
	    }
	    catch(Exception e){
	    	//Something was wrong when we tried to build up xml.
	    	Log.e(LOGTAG, e.getMessage());
	    	throw new CommunicationException(LOGTAG, "Unknown exception caught", e);
	    }
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
        		NodeList nodes = xmlDoc.getElementsByTagName(_TRIPID);
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
	private ArrayList<MoodEvent> consumeXmlFromMoodMap(HttpResponse response) throws IllegalStateException, IOException {
		final String POINT = "p";
		final String MOOD = "value";
		final String LONGITUDE = "long";
		final String LATITUDE = "lat";
		try{
        	//Getting ready to proceed
	        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        	DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        	Document xmlDoc = docBuilder.parse(response.getEntity().getContent());
    		//Work out the status codes.
        
        	if(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300){
	        	//Everything works build the events.
        		ArrayList<MoodEvent> events = new ArrayList<MoodEvent>();
        		Long currentTime = Calendar.getInstance().getTimeInMillis();
        		
        		NodeList nodes = xmlDoc.getElementsByTagName(POINT);
        		//Traversing over MoodMapReading elements in the map. It'll contain 
        		for(int i = 0; nodes.getLength() > i; i++){
        			Node n = nodes.item(i);
        			Integer mood = null;
        			Double latitude = null;
        			Double longitude = null;
    				NamedNodeMap attributes = n.getAttributes();
    				
    				for(int j = 0 ; j < attributes.getLength() ; j++) {
    			        Attr attribute = (Attr)attributes.item(j);
    			        
    			        if(attribute.getName().contentEquals(MOOD))
        					mood = Integer.parseInt(attribute.getValue());
    			        else if(attribute.getName().contentEquals(LONGITUDE))
        					longitude = Double.parseDouble(attribute.getValue());
        				else if(attribute.getName().contentEquals(LATITUDE))
        					latitude = Double.parseDouble(attribute.getValue());
    			    }
        			//Check that all values have been discovered
        			if(mood == null || latitude == null || longitude == null){
        				Log.e(LOGTAG, "Expected at least a mood, longitude and latitude. But not all values where found");
        				throw new IllegalStateException(LOGTAG + ": Expected values(mood, lat, long) weren't found in the xml");
        			}
        			//adding the result.
        			//insert the current time, since no time is supplied by the map from the server.
        			events.add(new MoodEvent(currentTime, latitude, longitude, mood));
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