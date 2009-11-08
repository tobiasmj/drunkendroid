package itu.malta.drunkendroid.tech;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import itu.malta.drunkendroid.domain.Reading;
import itu.malta.drunkendroid.domain.Trip;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
public class RESTServerHelper {
	private static final String BASE_URI = "http://192.168.0.13:8182/drunkendroid/";
	private static final String targetDomain = "192.168.0.13";
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
	private String IMEI = "";
	
	
	public RESTServerHelper(Context context){
		TelephonyManager mgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = mgr.getDeviceId();
	}
	
	public Long uploadTrip(Trip t)
	{
		try {
	        //Build xml
	    	String xml = buildXmlFromTrip(t);
	        //Now try to send it
	        HttpResponse response = postContent(BASE_URI + TRIP + "/"+IMEI, xml);
	        Long resultId = consumeTripUploadResponse(response);
	        return resultId;
	    }
		catch (IllegalStateException e){
			//We have recieved an error from the server.
			//Handle it
			
			return null;
		}
	    catch(IOException e){
	    	Log.e(LOGTAG, e.getMessage());
	    	return null;
	    }
	}
	
	
	private String buildXmlFromTrip(Trip t) throws IOException{
		XmlSerializer serializer = Xml.newSerializer();
	    final StringWriter writer = new StringWriter();
	    
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
        serializer.startTag("", TRIP_NAME);
        serializer.text("Gin Saturdays");
        serializer.endTag("", TRIP_NAME);
        serializer.endTag("", TRIP);
        serializer.endDocument();
       
        return writer.toString();
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
	        	throw new IllegalStateException(String.valueOf(response.getStatusLine().getStatusCode()));
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

	private HttpResponse postContent(String URI, String xmlContent){
		DefaultHttpClient httpClient = new DefaultHttpClient();
        
        HttpHost targetHost = new HttpHost(targetDomain, 8182, "http");
        // Using POST here
        HttpPost httpPost = new HttpPost(URI);
        // Make sure the server knows what kind of a response we will accept
        httpPost.addHeader("Accept", "text/xml");
        // Also be sure to tell the server what kind of content we are sending
        httpPost.addHeader("Content-Type", "text/xml");
                
        try 
        {
            StringEntity entity = new StringEntity(xmlContent, "UTF-8");
            entity.setContentType("text/xml");
            httpPost.setEntity(entity);
            
            return httpClient.execute(targetHost, httpPost);
         }
        catch (Exception ex)
        {
                ex.printStackTrace();
                return null;
        }
	}
}