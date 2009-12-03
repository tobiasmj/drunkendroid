package itu.malta.drunkendroidserver.control;

import itu.malta.drunkendroidserver.domain.Call;
import itu.malta.drunkendroidserver.domain.GridCell;
import itu.malta.drunkendroidserver.domain.Location;
import itu.malta.drunkendroidserver.domain.MoodMap;
import itu.malta.drunkendroidserver.domain.Mood;
import itu.malta.drunkendroidserver.domain.Sms;
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
	private Connection _conn = null;
	private Statement _stmt = null;


	/**
	 * Constructor 
	 * @param connection the Connection to be used with the repository.
	 */
	public Repository(java.sql.Connection connection) {
		this._conn = connection;
	}
	/**
	 * Method for inserting a call
	 * @param call, the call object to be inserted
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public void insertCall(Call call) throws SQLException {
		// make sure that the location has a tripid associated.
		if (call.getTripId() != 0) {
			ResultSet rs = null;
			try {
				_stmt = _conn.createStatement();
				_stmt.executeUpdate("Insert into Call(trip,timeStamp,latitude,longitude,caller,reciever,endTimeStamp) values (" + call.getTripId() + "," + call.getTimeStamp() + "," + call.getLatitude() + "," + call.getLongitude() + "," + call.getCaller() + "," + call.getReciever() + "," + call.getEndTime() + ")");
			} finally {
				// cleanup
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException ex) {
						// ignore 
					}
				}

				if (_stmt != null) {
					try {
						_stmt.close();
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
	 * Method for inserting an sms
	 * @param call, the call object to be inserted
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public void insertSms(Sms sms) throws SQLException {
		// make sure that the location has a tripid associated.
		if (sms.getTripId() != 0) {
			ResultSet rs = null;
			try {
				_stmt = _conn.createStatement();
				_stmt.executeUpdate("Insert into SMS(trip,timeStamp,latitude,longitude,sender,reciever) values (" + sms.getTripId() + "," + sms.getTimeStamp() + "," + sms.getLatitude() + "," + sms.getLongitude() + "," + sms.getSender() + "," + sms.getReciever() + "," + sms.getMessage() + ")");
			} finally {
				// cleanup
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException ex) {
						// ignore 
					}
				}

				if (_stmt != null) {
					try {
						_stmt.close();
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
	 * Method for inserting a location
	 * @param location, the location object to be inserted
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public void insertLocation(Location location) throws SQLException {
		// make sure that the location has a tripid associated.
		if (location.getTripId() != 0) {
			ResultSet rs = null;
			try {
				_stmt = _conn.createStatement();
				_stmt.executeUpdate("Insert into Location(trip,timeStamp,latitude,longitude) values (" + location.getTripId() + "," + location.getTimeStamp() + "," + location.getLatitude() + "," + location.getLongitude() + ")");
			} finally {
				// cleanup
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException ex) {
						// ignore 
					}
				}

				if (_stmt != null) {
					try {
						_stmt.close();
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
	 * Method for inserting a moodReading
	 * @param mood, the Mood object to be inserted
	 * @throws SQLException, thrown when an SQL error occurs.
	 */
	public void insertMood(Mood mood) throws SQLException {
		// make sure that the mood has a tripid associated.
		if (mood.getTripId() != 0) {
			ResultSet rs = null;
			try {
				_stmt = _conn.createStatement();
				_stmt.executeUpdate("Insert into Mood(trip,timeStamp,latitude,longitude,mood) values (" + mood.getTripId() + "," + mood.getTimeStamp() + "," + mood.getLatitude() + "," + mood.getLongitude() + "," + mood.getMood() +")");
			} finally {
				// cleanup
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException ex) {
						// ignore
					}
				}

				if (_stmt != null) {
					try {
						_stmt.close();
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
			_stmt = _conn.createStatement();
			_stmt.executeUpdate("Insert into Trip (IMEINumber,startTimeStamp,endTimeStamp,name) values (" + trip.getImeiNumber() + "," + trip.getStartTime() + "," + trip.getEndTime() + "," + "\"" + trip.getName() + "\"" + ")", Statement.RETURN_GENERATED_KEYS);
			rs = _stmt.getGeneratedKeys();

			if (rs.next()) {
				tripID = rs.getInt(1);

				if (tripID != -1) {
					// if the trip holds any events, insert the into the database.
					while(trip.moreEvents()) {

						IEvent event = trip.getNextEvent();
						event.setTripId(tripID);
						if(event.getClass().equals(Mood.class)) {
							insertMood((Mood)event);
						} else if (event.getClass().equals(Location.class)) {
							insertLocation((Location)event);
						}
					}
				} else {
					throw new SQLException("Error inserting the trip into the database");
				}
			} else {
				throw new SQLException("Error inserting the trip into the database");	
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

			if (_stmt != null) {
				try {
					_stmt.close();
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
			_stmt = _conn.createStatement();
			// update the trip.
			_stmt.executeUpdate("Update trip set name = \'" + trip.getName() + "\' , startTimeStamp = " + trip.getStartTime() + ", endTimeStamp = "+ trip.getEndTime() + " where id = " + trip.getTripId());
		} finally {

			if (_stmt != null) {
				try {
					_stmt.close();
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
			_stmt = _conn.createStatement();
			LinkedList<IEvent> events = new LinkedList<IEvent>(); 

			// get all mood events for a given tripId
			_stmt.executeQuery("Select timeStamp,longitude,latitude,mood from Mood where trip = " + trip.getTripId());
			rs = _stmt.getResultSet();

			while (rs.next()) {
				events.add(new Mood(rs.getLong("timeStamp"), rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getInt("mood")));
			}

			// get all location events for a given tripId
			_stmt.executeQuery("Select timeStamp, longitude,latitude from Location where trip = " + trip.getTripId());
			rs= _stmt.getResultSet();

			while (rs.next()) {
				events.add(new Location(rs.getLong("timeStamp"), rs.getDouble("longitude"), rs.getDouble("latitude")));
			}
			
			// get all call events for a given tripId
			_stmt.executeQuery("Select timeStamp, longitude,latitude,caller,reciever,endTimeStamp from Call where trip = " + trip.getTripId());
			rs= _stmt.getResultSet();

			while (rs.next()) {
				events.add(new Call(rs.getLong("timeStamp"), rs.getDouble("longitude"), rs.getDouble("latitude"), rs.getString("caller"), rs.getString("reciever"), rs.getLong("endTimeStamp")));
			}
			
			// get all sms events for a given tripId
			_stmt.executeQuery("Select timeStamp, longitude,latitude,sender,reciever,message from SMS where trip = " + trip.getTripId());
			rs= _stmt.getResultSet();

			while (rs.next()) {
				events.add(new Sms(rs.getLong("timeStamp"), rs.getDouble("longitude"), rs.getDouble("latitude"), rs.getString("sender"), rs.getString("reciever"), rs.getString("message")));
			}

			// create trip object 
			_stmt.executeQuery("Select name,startTimeStamp,endTimeStamp from trip where id = " + trip.getTripId());

			rs = _stmt.getResultSet();
			Trip newTrip = null;
			if (rs.first()) {
				newTrip = new Trip(rs.getLong("startTimeStamp"), rs.getLong("endTimeStamp"), rs.getString("name"));
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

			if (_stmt != null) {
				try {
					_stmt.close();
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
		int gridX = mm.getGridX();
		int gridY = mm.getGridY();
		double moodReadingLong, moodReadingLat;
		double width = mm.getLongMax() - mm.getLongMin();
		double height = mm.getLatMax() - mm.getLatMin();
		double gridWidth = width/gridX;
		double gridHeight = height/gridY;

		GridCell[][] moodMapGrid = new GridCell[gridX][gridY];
		
		// tryng to fix moodmap skrew
		double snapLongMin = Math.round(mm.getLongMin() / gridWidth) * gridWidth;
		double snapLatMin = Math.round(mm.getLatMin()/gridHeight) * gridHeight;
		double snapLongMax = Math.round(mm.getLongMax() / gridWidth) * gridWidth;
		double snapLatMax = Math.round(mm.getLatMax()/gridHeight) * gridHeight; 
		System.out.println("snapLatMin : " + snapLatMin);
		System.out.println("snapLongMin : " + snapLongMin);
		System.out.println("snapLatMax : " + snapLatMax);
		System.out.println("snapLongMax : " + snapLongMax);
		
	
		
		
		
		ResultSet rs = null;
		try { 
			_stmt = _conn.createStatement();
			String query =  "select mood, longitude, latitude from Mood where timeStamp between " + mm.getStartTimeStamp() + " and " + mm.getEndTimeStamp() +
			" and longitude between " + snapLongMin + " and " +snapLongMax + " and latitude between " + snapLatMin + " and " + snapLatMax;
			
			_stmt.executeQuery(query);
			rs = _stmt.getResultSet();
			int xCoord, yCoord;
			// iterate all the mood readings and calculate their place in the grid
			while(rs.next()) {
				moodReadingLong = rs.getDouble("longitude");
				moodReadingLat = rs.getDouble("latitude");
				xCoord = (int)((moodReadingLong - snapLongMin)/gridWidth);

				if(xCoord > 0) {
					xCoord = xCoord -1;
				}
				yCoord = (int)((moodReadingLat - snapLatMin)/gridHeight);
				if(yCoord > 0) {
					yCoord = yCoord -1;
				}
				// insert the mood reading in the appropriate GridCell, if no GridCell exists in that array index we create a new one.
				if(moodMapGrid[xCoord][yCoord] == null) {
					moodMapGrid[xCoord][yCoord] = new GridCell((xCoord + 0.5)*gridWidth+snapLongMin,(yCoord + 0.5)*gridHeight+snapLatMin);
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

			if (_stmt != null) {
				try {
					_stmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
		return moodMapGrid;
	}
}
