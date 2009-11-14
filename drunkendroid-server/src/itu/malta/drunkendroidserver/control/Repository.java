package itu.malta.drunkendroidserver.control;

import itu.malta.drunkendroidserver.GridCell;
import itu.malta.drunkendroidserver.Reading;
import itu.malta.drunkendroidserver.MoodMap;
import itu.malta.drunkendroidserver.Trip;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import com.mysql.jdbc.Connection;
import java.sql.Connection;

public class Repository {
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	public Repository(java.sql.Connection connection) {
		this.conn = connection;
	}
	public void insertReading(Reading reading) throws SQLException {
		if (reading.getTripID() != 0) {

			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("Insert into Reading(trip,dateTime,latitude,longitude,mood) values (" + reading.getTripID() + "," + reading.getDateTime() + "," + reading.getLatitude() + "," + reading.getLongitude() + "," + reading.getMood() +")");
				rs = stmt.getResultSet();
				if(rs != null) {
					rs.close();
				}
				rs = stmt.getResultSet();
				if(rs != null) {
					rs.close();
				}
				rs = null;
			} finally {
				// cleanup
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
		} else {
			throw new SQLException("TripId not initialized before committing to database");
		}

	}
	public long insertTrip(Trip trip) throws SQLException {
		long tripID = -1;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("Insert into Trip (IMEINumber,startDateTime,endDateTime,name) values (" + trip.getImeiNumber() + "," + trip.getStartTime() + "," + trip.getEndTime() + "," + "\"" + trip.getName() + "\"" + ")", Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();

		    if (rs.next()) {
		        tripID = rs.getInt(1);
		        
				if (tripID != -1) {
					while(trip.moreEvents()) {
						
						Reading reading = trip.getNextEvent();
						reading.setTripID(tripID);
						insertReading(reading);
					}
				} else {
					// Throw exception about failed trip insert
				}
		    } else {

		        // throw an exception from here
		    }

		    rs.close();
		    rs = null;
		    return tripID;
		} finally {
			// cleanup resultSet and connection to database.
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
	public void updateTrip(Trip trip) throws SQLException {
		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("Update trip set name = " + trip.getName() + ", startDateTime = " + trip.getStartTime() + ", endDateTime "+ trip.getEndTime() + " where id = " + trip.getTripId());
		} finally {

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException ex) {
		            // ignore
		        }
		    }
		}
	}
	

	/**
	 * 
	 * @return a DomRepresentation with the trip and it's events in xmlformat
	 * @throws SQLException thrown when an error communicating with the sql server occurs.
	 * @throws IOException thown when contructing the xml document fails.
	 */	
	public DomRepresentation getTrip(Trip trip) throws SQLException, IOException {
		try {
			stmt = conn.createStatement();
			
			// create the result xmlDoc
			DomRepresentation result = null;  
        	result = new DomRepresentation(MediaType.TEXT_XML);  

        	
        	// get all events for a given tripID
			stmt.executeQuery("Select dateTime,longitude,latitude,mood from reading where trip = " + trip.getTripId());
			
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
			stmt.executeQuery("Select name,startDateTime,endDateTime from trip where id = " + trip.getTripId());
			
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
	
	public GridCell[][] calculateMoodMap(MoodMap mm) throws SQLException {
		int gridX = 60 , gridY = 60;
		double readingLong, readingLat;
		double width = mm.getLongMax() - mm.getLongMin();
		double height = mm.getLatMax() - mm.getLatMin();
		double gridWidth = width/gridX;
		double gridHeight = height/gridY;

		GridCell[][] moodMapGrid = new GridCell[gridX][gridY];
		try { 
			stmt = conn.createStatement();
			String query =  "select mood, longitude, latitude from Reading where dateTime between " + mm.getStartReadingTime() + " and " + mm.getEndReadingTime() +
			" and longitude between " + mm.getLongMin() + " and " + mm.getLongMax() + " and latitude between " + mm.getLatMin() + " and " + mm.getLatMax();
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			int xCoord, yCoord;
			while(rs.next()) {
				readingLong = rs.getDouble("longtitude");
				readingLat = rs.getDouble("Latitude");
				xCoord = (int)((readingLong - mm.getLongMin())/gridWidth) -1;
				yCoord = (int)((readingLat - mm.getLatMin())/gridHeight) -1;
				if(moodMapGrid[xCoord][yCoord] == null) {
					moodMapGrid[xCoord][yCoord] = new GridCell((xCoord + 0.5)*gridWidth+mm.getULlongitude(),(yCoord + 0.5)*gridHeight+mm.getULlatitude());
					moodMapGrid[xCoord][yCoord].addValue(rs.getInt("mood"));
				} else {
					moodMapGrid[xCoord][yCoord].addValue(rs.getInt("mood"));
				}
				
			}
			
			
			rs = null;
		} finally {
			// cleanup.
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
		return moodMapGrid;
	}
}
