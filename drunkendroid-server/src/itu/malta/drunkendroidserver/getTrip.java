package itu.malta.drunkendroidserver;

import itu.malta.drunkendroidserver.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 
 * Class for accessing a specific trip
 *
 */
public class getTrip {

	long tripId;
	public getTrip (long tripId) {
		this.tripId = tripId;
	}
	
	/**
	 * 
	 * @return a DomRepresentation with the trip and it's events in xmlformat
	 * @throws SQLException thrown when an error communicating with the sql server occurs.
	 * @throws IOException thown when contructing the xml document fails.
	 */
	public DomRepresentation execute() throws SQLException, IOException{
			Connection conn = DatabaseConnection.getInstance().getConn();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				
				// create the result xmlDoc
				DomRepresentation result = null;  
	        	result = new DomRepresentation(MediaType.TEXT_XML);  

	        	
	        	// get all events for a given tripID
				stmt.executeQuery("Select dateTime,longitude,latitude,mood from reading where trip = " + tripId);
				
				rs = stmt.getResultSet();
				Document d = result.getDocument();  
				
				Element eltEvents = d.createElement("events");
				
				// create XML for the events
				while (rs.next()) {
				
					Element eltEvent = d.createElement("event");
					eltEvents.appendChild(eltEvent);
					
					Element eltType = d.createElement("eventType");
		        	eltType.appendChild(d.createTextNode("reading"));  
		        	eltEvent.appendChild(eltType);
		        	
		        	Element eltDateTime = d.createElement("dateTime");
		        	eltDateTime.appendChild(d.createTextNode(Long.toString(rs.getLong("dateTime"))));  
		        	eltEvent.appendChild(eltDateTime);
		        	
		        	Element eltLongitude = d.createElement("longitude");
		        	eltLongitude.appendChild(d.createTextNode(Double.toString(rs.getLong("longitude"))));  
		        	eltEvent.appendChild(eltLongitude);
		        	
		        	Element eltData = d.createElement("data");
		        	eltEvent.appendChild(eltData);

		        	Element eltMood = d.createElement("mood");
		        	eltMood.appendChild(d.createTextNode(Integer.toString(rs.getInt("mood"))));  
		        	eltEvent.appendChild(eltMood);
					
				}
				
				
				// create XML for the trip 
				stmt.executeQuery("Select name,startDateTime,endDateTime from trip where id = " + tripId);
				
				rs = stmt.getResultSet();

				if (rs.first()) {
					// build tripxml response
					Element eltTrip = d.createElement("trip");  
		        	d.appendChild(eltTrip);
		            
		        	Element eltName = d.createElement("name");
		        	eltName.appendChild(d.createTextNode(rs.getString("name")));  
		        	eltTrip.appendChild(eltName);  

		        	Element eltStart = d.createElement("startDateTime");
		        	eltStart.appendChild(d.createTextNode(rs.getString("startDateTime")));  
		        	eltTrip.appendChild(eltStart);  

		        	Element eltEnd = d.createElement("endDateTime");
		        	eltEnd.appendChild(d.createTextNode(rs.getString("endDateTime")));  
		        	eltTrip.appendChild(eltEnd);  
					
					// append the events to the trip
		        	eltTrip.appendChild(eltEvents);
					
			
				}
				
				//clean up resultset.
				if(rs != null) {
					rs.close();
				}
				rs = null;
				
				return result;
			} finally {
				// cleanup database connection and resultset.
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException ex) {
						// ignore
					}
				}

				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException ex) {
						// ignore
					}
				}
			}
	}
}
