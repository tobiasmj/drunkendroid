package itu.malta.drunkendroidserver;

import itu.malta.drunkendroidserver.tech.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * Class used for inserting readings in to the database.
 *
 */
public class Reading{

	long readingTime;
	double latitude, longitude;
	int mood = -1;
	private long tripID = 0;
	
	/* (non-Javadoc)
	 * @see itu.malta.drunkendroidserver.IInsertEvent#getTripID()
	 */
	public long getTripID() {
		return tripID;
	}

	/* (non-Javadoc)
	 * @see itu.malta.drunkendroidserver.IInsertEvent#setTripID(long)
	 */
	public void setTripID(long tripId) {
		this.tripID = tripId;
	}
	/**
	 * Constructor
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 * @param mood the mood valued 0-255
	 */
	public Reading (long readingTime, double latitude, double longitude, int mood ) {
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mood = mood;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 * @param mood the mood valued 0-255
	 */
	public Reading (long tripId, long readingTime, double latitude, double longitude, int mood ) {
		this.tripID = tripId;
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mood = mood;
	}
	
	/* (non-Javadoc)
	 * @see itu.malta.drunkendroidserver.IInsertEvent#execute()
	 */
	public void execute() throws SQLException{
		if (tripID != 0) {
			Connection conn = DatabaseConnection.getInstance().getConn();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("Insert into Reading(trip,dateTime,latitude,longitude,mood) values (" + tripID + "," + readingTime+ "," + latitude + "," + longitude + "," + mood +")");
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

	public long getDateTime() {
		return readingTime;
	}

	public String getEventType() {
		return "reading";
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	public int getMood() {
		return mood;
	}
}
