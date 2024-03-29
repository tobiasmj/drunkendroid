package itu.dd.server;


import itu.dd.server.control.Repository;
import itu.dd.server.domain.GridCell;
import itu.dd.server.domain.Moodmap;
import itu.dd.server.tech.DatabaseConnection;
import itu.dd.server.util.*;
import itu.dd.server.util.xstreem.converters.MoodMapConverter;

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
public class MoodmapResource extends ServerResource {
	private long _startTimeStamp, _endTimeStamp;
	private double _ULlatitude, _ULlongitude, _LRlatitude, _LRlongitude;
	private int _gridX = 10 , _gridY = 10;
	private GridCell[][] _moodMapGrid;

	/**
	 * Handles get requests for MoodMaps, based on the provided URL arguments.
	 * @return XML MoodMap representation
	 */
	@Get  
	public Representation represent() {  

		Long startEndVariable, gMM = 0L, cMM = 0L; 

		startEndVariable  = System.currentTimeMillis();

		Representation result = null;
		try {
			// get the values posted to the server
			_startTimeStamp = Long.parseLong(getRequest().getAttributes().get("StartTimeStamp").toString());
			_endTimeStamp = Long.parseLong(getRequest().getAttributes().get("EndTimeStamp").toString());

			_ULlatitude = Double.parseDouble(getRequest().getAttributes().get("ULLatitude").toString());
			_ULlongitude = Double.parseDouble(getRequest().getAttributes().get("ULLongitude").toString());

			_LRlatitude = Double.parseDouble(getRequest().getAttributes().get("LRLatitude").toString());
			_LRlongitude = Double.parseDouble(getRequest().getAttributes().get("LRLongitude").toString());

		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);

			result = XmlResponse.generateErrorRepresentation("Malformed Data in URL", "7");	


		}
		// create the MoodMap object
		Moodmap moodMap = new Moodmap(_startTimeStamp, _endTimeStamp, _ULlatitude, _ULlongitude, _LRlatitude, _LRlongitude, _gridX, _gridY);

		try {
			// set up the repository
			Repository rep = new Repository(DatabaseConnection.getInstance().getConn());
			//calculate the MoodMap
			cMM = System.currentTimeMillis();
			_moodMapGrid = rep.calculateMoodMap(moodMap);
			cMM = System.currentTimeMillis()- cMM;
			// get the MoodMap as an XML representation.
			gMM = System.currentTimeMillis();
			result = getMoodMapXML();
			gMM = System.currentTimeMillis()- gMM;
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
		startEndVariable = System.currentTimeMillis() - startEndVariable;

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
			// Generate a DOM document representing the list of  
			// MoodMapReadings.  
			result = new DomRepresentation(MediaType.TEXT_XML);  
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

