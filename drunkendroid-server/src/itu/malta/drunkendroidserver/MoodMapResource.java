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
	private long _startTimeStamp, _endTimeStamp;
	private double _ULlatitude, _ULlongitude, _LRlatitude, _LRlongitude;
	private int _gridX = 20 , _gridY = 20;
	private GridCell[][] _moodMapGrid;
	
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
		_startTimeStamp = Long.parseLong(getRequest().getAttributes().get("StartTimeStamp").toString());
		_endTimeStamp = Long.parseLong(getRequest().getAttributes().get("EndTimeStamp").toString());
		
		_ULlatitude = Double.parseDouble(getRequest().getAttributes().get("ULLatitude").toString());
		_ULlongitude = Double.parseDouble(getRequest().getAttributes().get("ULLongitude").toString());
		System.out.println("UL. lat: " + _ULlatitude + " long: " + _ULlongitude);
		_LRlatitude = Double.parseDouble(getRequest().getAttributes().get("LRLatitude").toString());
		_LRlongitude = Double.parseDouble(getRequest().getAttributes().get("LRLongitude").toString());
		System.out.println("LR. lat: " + _LRlatitude + " long: " + _LRlongitude);		
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			result = XmlResponse.generateErrorRepresentation("Malformed Data in URL", "7");			
		}
		// create the MoodMap object
		MoodMap moodMap = new MoodMap(_startTimeStamp, _endTimeStamp, _ULlatitude, _ULlongitude, _LRlatitude, _LRlongitude, _gridX, _gridY);
		
		try {
			// set up the repository
			Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
			//calculate the MoodMap
			System.out.println(System.currentTimeMillis()/1000 +" Start calculate moodmap");
			_moodMapGrid = rep.calculateMoodMap(moodMap);
			System.out.println(System.currentTimeMillis()/1000 +" End calculate moodmap");
			// get the MoodMap as an XML representation.
			System.out.println(System.currentTimeMillis()/1000 + " get moodmap xml");
			result = getMoodMapXML();
			System.out.println(System.currentTimeMillis()/1000 +  " end get moodmap xml");
		} catch (SQLException se) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			result = XmlResponse.generateErrorRepresentation(" Error getting data from database", "5");
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
			

			if(_moodMapGrid != null) {
			XStream xStream = new XStream();
			xStream.registerConverter(new MoodMapConverter());
			xStream.alias("p", GridCell.class);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder builder = null;
    		Document document = null;
			
    		builder = factory.newDocumentBuilder();
			String xmlOutput = "<points>";
	            result = new DomRepresentation(MediaType.TEXT_XML);  
	            // Generate a DOM document representing the list of  
	            // MoodMapReadings.  
	            for(int i = 0; i < _gridY; i++){
	            	for(int j = 0; j < _gridX; j++){
	            		if (_moodMapGrid[i][j] != null){
	            			xmlOutput = xmlOutput + xStream.toXML(_moodMapGrid[i][j]);
	             		}
	            	}
	            }
	        xmlOutput = xmlOutput + "</points>";
            document = builder.parse(new InputSource(new StringReader(xmlOutput)));
			result.setDocument(document);
			}	  			 
			return result;
	}
}

