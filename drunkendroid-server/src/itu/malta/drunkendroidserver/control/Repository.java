package itu.malta.drunkendroidserver.control;

import itu.malta.drunkendroidserver.domain.GridCell;
import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.MoodMap;
import itu.malta.drunkendroidserver.domain.Reading;
import itu.malta.drunkendroidserver.domain.Trip;
import itu.malta.drunkendroidserver.interfaces.IEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.util.LinkedList;
/**
 * Class responsible for handling persistence of objects to the database
 */
public class Repository {
	private Connection conn = null;
	private Statement stmt = null;


	/**
	 * Constructor 
	 * @param connection the Connection to be used with the repository.
	 */
	public Repository(java.sql.Connection connection) {
		this.conn = connection;
	}

	/**
	 * Method for inserting a location
	 * @param location, the location object to be inserted
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public void insertLocation(Location location) throws SQLException {
		// make sure that the location has a tripid associated.
		if (location.getTripId() != 0) {
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("Insert into Location(trip,dateTime,latitude,longitude) values (" + location.getTripId() + "," + location.getTimeStamp() + "," + location.getLatitude() + "," + location.getLongitude() + ")");
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
	/**
	 * Method for inserting a Reading
	 * @param reading, the reading object to be inserted
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public void insertReading(Reading reading) throws SQLException {
		// make sure that the reading has a tripid associated.
		if (reading.getTripId() != 0) {
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("Insert into Reading(trip,dateTime,latitude,longitude,mood) values (" + reading.getTripId() + "," + reading.getTimeStamp() + "," + reading.getLatitude() + "," + reading.getLongitude() + "," + reading.getMood() +")");
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

	/**
	 * Method for inserting a trip, including it's events.
	 * @param trip, the trip object to be inserted
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public long insertTrip(Trip trip) throws SQLException {
		long tripID = -1;
		ResultSet rs = null;
		try {
			//insert the trip in the database
			stmt = conn.createStatement();
			stmt.executeUpdate("Insert into Trip (IMEINumber,startDateTime,endDateTime,name) values (" + trip.getImeiNumber() + "," + trip.getStartTime() + "," + trip.getEndTime() + "," + "\"" + trip.getName() + "\"" + ")", Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				tripID = rs.getInt(1);

				if (tripID != -1) {
					// if the trip holds any events, insert the into the datbase.
					while(trip.moreEvents()) {

						IEvent event = trip.getNextEvent();
						event.setTripId(tripID);
						if(event.getClass().equals(Reading.class)) {
							insertReading((Reading)event);
						} else if (event.getClass().equals(Location.class)) {
							insertLocation((Location)event);
						}
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

	/**
	 * Method for updating a trip.
	 * @param trip, the trip object to be updated based on the trip objects tripId.
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public void updateTrip(Trip trip) throws SQLException {

		try {
			stmt = conn.createStatement();
			// update the trip.
			stmt.executeUpdate("Update trip set name = \'" + trip.getName() + "\' , startDateTime = " + trip.getStartTime() + ", endDateTime = "+ trip.getEndTime() + " where id = " + trip.getTripId());
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
	 * Method for getting a trip, including it's related events.
	 * @return a DomRepresentation with the trip and it's events in XML format
	 * @throws SQLException thrown when an error communicating with the SQL server occurs.
	 * @throws IOException thrown when constructing the XML document fails.
	 */	
	public Trip getTrip(Trip trip) throws SQLException, IOException {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			LinkedList<IEvent> events = new LinkedList<IEvent>(); 

			// get all reading events for a given tripId
			stmt.executeQuery("Select dateTime,longitude,latitude,mood from reading where trip = " + trip.getTripId());
			rs = stmt.getResultSet();

			while (rs.next()) {
				events.add(new Reading(rs.getLong("dateTime"), rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getInt("mood")));
			}

			// get all location events for a given tripId
			stmt.executeQuery("Select dateTime, longitude,latitude from Location where trip = " + trip.getTripId());
			rs= stmt.getResultSet();

			while (rs.next()) {
				events.add(new Location(rs.getLong("dateTime"), rs.getDouble("longitude"), rs.getDouble("latitude")));
			}

			// create trip object 
			stmt.executeQuery("Select name,startDateTime,endDateTime from trip where id = " + trip.getTripId());

			rs = stmt.getResultSet();
			Trip newTrip = null;
			if (rs.first()) {
				newTrip = new Trip(rs.getLong("startDateTime"), rs.getLong("endDateTime"), rs.getString("name"));
				newTrip.setTripId(trip.getTripId());
			}

			if (newTrip != null) {
				newTrip.addEvents(events);
				return newTrip;
			}
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
		return null;
	}
	/**
	 * Method for calculating a gridcell array from a given moodmap object 
	 * @param mm the MoodMap object.
	 * @return GridCell array containing the mood readings.
	 * @throws SQLException thrown when an SQL error occurs 
	 */
	public GridCell[][] calculateMoodMap(MoodMap mm) throws SQLException {
		int gridX = 60 , gridY = 60;
		double readingLong, readingLat;
		double width = mm.getLongMax() - mm.getLongMin();
		double height = mm.getLatMax() - mm.getLatMin();
		double gridWidth = width/gridX;
		double gridHeight = height/gridY;

		GridCell[][] moodMapGrid = new GridCell[gridX][gridY];
		ResultSet rs = null;
		try { 
			stmt = conn.createStatement();
			// get all mood readings within the given parameters.
			String query =  "select mood, longitude, latitude from Reading where dateTime between " + mm.getStartReadingTime() + " and " + mm.getEndReadingTime() +
			" and longitude between " + mm.getLongMin() + " and " + mm.getLongMax() + " and latitude between " + mm.getLatMin() + " and " + mm.getLatMax();
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			int xCoord, yCoord;
			// iterate all the mood readings and calculate their place in the grid
			while(rs.next()) {
				readingLong = rs.getDouble("longitude");
				readingLat = rs.getDouble("latitude");
				xCoord = (int)((readingLong - mm.getLongMin())/gridWidth);
				if(xCoord > 0) {
					xCoord = xCoord -1;
				}
				yCoord = (int)((readingLat - mm.getLatMin())/gridHeight);
				if(yCoord > 0) {
					yCoord = yCoord -1;
				}
				// insert the mood reading in the appropriate GridCell, if no GridCell exists in that array index we create a new one.
				if(moodMapGrid[xCoord][yCoord] == null) {
					moodMapGrid[xCoord][yCoord] = new GridCell((xCoord + 0.5)*gridWidth+mm.getULlongitude(),(yCoord + 0.5)*gridHeight+mm.getULlatitude());
					moodMapGrid[xCoord][yCoord].addValue(rs.getInt("mood"));
				} else {
					moodMapGrid[xCoord][yCoord].addValue(rs.getInt("mood"));
				}

			}

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
