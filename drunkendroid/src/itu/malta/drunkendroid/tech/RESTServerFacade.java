package itu.malta.drunkendroid.tech;

import java.io.IOException;
import java.io.StringWriter;

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
	private String IMEI = "";
	IWebserviceConnection conn = null;
	
	public RESTServerFacade(Context context, IWebserviceConnection conn){
		TelephonyManager mgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = mgr.getDeviceId();
		this.conn = conn;
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
	        HttpResponse response = conn.postTrip(TRIP + "/"+IMEI, xml);
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
						response = conn.postTrip(TRIP + "/"+IMEI, xml);
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
        for(Event r : t.getTripEvents()){
        	if(ReadingEvent.class.isInstance(r)){
        		addReadingXml(serializer,(ReadingEvent) r);	
        	}
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

	private void addReadingXml(XmlSerializer serializer, ReadingEvent r) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", EVENT);
        serializer.startTag("", EVENTTYPE);
        serializer.text("reading");
        serializer.endTag("", EVENTTYPE);
        serializer.startTag("", DATETIME);
        serializer.text(String.valueOf(r.dateTime));
        serializer.endTag("", DATETIME);
        serializer.startTag("", LONGITUDE);
        serializer.text(String.valueOf(r.longitude));
        serializer.endTag("", LONGITUDE);
        serializer.startTag("", LATITUDE);
        serializer.text(String.valueOf(r.latitude));
        serializer.endTag("", LATITUDE);
        serializer.startTag("", DATA);
        serializer.startTag("", MOOD);
        serializer.text(String.valueOf(r.mood));
        serializer.endTag("", MOOD);
        serializer.endTag("", DATA);
        serializer.endTag("", EVENT);	
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
	public Trip getEvents(Long starTime, Long endTime, Long latitude, Long longitude, Long distance) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * This method expects a Trip which already has a remoteId.
	 * To obtain a remoteId call the upLoad function.
	 */
	public void updateTrip(Trip t, Event e) {
		//Not implemented. 
		throw new AndroidRuntimeException("Not implemented");
	}
}