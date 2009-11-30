package itu.malta.drunkendroidserver;


import itu.malta.drunkendroidserver.control.Repository;
import itu.malta.drunkendroidserver.domain.GridCell;
import itu.malta.drunkendroidserver.domain.MoodMap;
import itu.malta.drunkendroidserver.tech.DatabaseConnection;
import itu.malta.drunkendroidserver.util.*;
import itu.malta.drunkendroidserver.util.xstreem.converters.MoodMapConverter;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

/**
 * Class that handles requests for retrieving MoodMaps.
 */
public class MoodMapResource extends ServerResource {
	long startTimeStamp, endTimeStamp;
	double latitude, longitude, ULlatitude, ULlongitude, LRlatitude, LRlongitude;
	int height, width;
	int gridX = 60 , gridY = 60;
	GridCell[][] moodMapGrid;
	
	/**
	 * Handles get requests for MoodMaps, based on the provided URL arguments.
	 * @return XML MoodMap representation
	 */
	@Get  
    public Representation represent() {  
		
		//Consider not allowing MoodMaps at sizes bigger then...
		
		Representation result = null;
		try {
		// get the values posted to the server
		startTimeStamp = Long.parseLong(getRequest().getAttributes().get("StartTimeStamp").toString());
		endTimeStamp = Long.parseLong(getRequest().getAttributes().get("EndTimeStamp").toString());
		
		latitude = Double.parseDouble(getRequest().getAttributes().get("Latitude").toString());
		longitude = Double.parseDouble(getRequest().getAttributes().get("Longitude").toString());
		
		height = Integer.parseInt(getRequest().getAttributes().get("Height").toString());
		width = Integer.parseInt(getRequest().getAttributes().get("Width").toString());
		
		//Calculate corners of the moodmap
		ULlatitude = latitude + HelperFunctions.change_in_latitude(-width/1000);
		ULlongitude = longitude + HelperFunctions.change_in_longitude(ULlatitude, -height/1000);
		
		LRlatitude = latitude + HelperFunctions.change_in_latitude(width/1000);
		LRlongitude = longitude + HelperFunctions.change_in_longitude(LRlatitude, height/1000);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Malformed Data in URL", "7");			
		}
		// create the MoodMap object
		MoodMap moodMap = new MoodMap(startTimeStamp, endTimeStamp, ULlatitude, ULlongitude, LRlatitude, LRlongitude);
		
		try {
			// set up the repository
			Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
			//calculate the MoodMap
			moodMapGrid = rep.calculateMoodMap(moodMap);
			// get the MoodMap as an XML representation.
			result = getMoodMapXML();
		} catch (SQLException se) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			result = XmlResponse.generateErrorRepresentation("Error getting data from database", "5");
		} catch (IOException ioe) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			result = XmlResponse.generateErrorRepresentation("Error creating XML response", "6");			
		} catch (SAXException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			result = XmlResponse.generateErrorRepresentation("Error creating XML response", "6");			
		} catch (ParserConfigurationException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			result = XmlResponse.generateErrorRepresentation("Error creating XML response", "6");			
		}
		
		return result;
	}
	
	/**
	 * Generates the moodmap xml representation.	
	 * @return DomRepresentation of the moodmap.
	 * @throws IOException thrown if the creation of the moodmap fails. 
	 * @throws SAXException if the creation of the moodmap fails.
	 * @throws ParserConfigurationException if the creation of the moodmap fails.
	 */
	private Representation getMoodMapXML() throws IOException, SAXException, ParserConfigurationException{
			DomRepresentation result = null;
			

			if(moodMapGrid != null) {
			XStream xStream = new XStream();
			xStream.registerConverter(new MoodMapConverter());
			xStream.alias("MoodMapReading", GridCell.class);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder builder = null;
    		Document document = null;
			
    		builder = factory.newDocumentBuilder();
			
	            result = new DomRepresentation(MediaType.TEXT_XML);  
	            // Generate a DOM document representing the list of  
	            // MoodMapReadings.  
	            String xmlMoodMapReadings = "<MoodMap>";
	            for(int i = 0; i < gridY; i++){
	            	for(int j = 0; j < gridX; j++){
	            		if (moodMapGrid[i][j] != null){
	            			xmlMoodMapReadings = xmlMoodMapReadings + xStream.toXML(moodMapGrid[i][j]);
	             		}
	            	}
	            }
    			xmlMoodMapReadings = xmlMoodMapReadings + "</MoodMap>";
	        document = builder.parse(new InputSource(new StringReader(xmlMoodMapReadings)));
			result.setDocument(document);
			}	  			 
			return result;
	}
}

