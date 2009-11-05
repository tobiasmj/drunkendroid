package drunkendroidserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import drunkendroidserver.Util.DatabaseConnection;

public class InsertReading {

	long readingTime;
	double latitude, longitude;
	int mood = -1;
	private long tripID = 0;
	
	public long getTripID() {
		return tripID;
	}

	public void setTripID(long tripId) {
		this.tripID = tripId;
	}

	public InsertReading (long readingTime, double latitude, double longitude, int mood ) {
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mood = mood;
	}
	public InsertReading (long tripId, long readingTime, double latitude, double longitude, int mood ) {
		this.tripID = tripId;
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mood = mood;
	}
	
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
}
