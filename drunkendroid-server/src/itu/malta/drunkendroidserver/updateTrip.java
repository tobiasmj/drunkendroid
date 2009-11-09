package itu.malta.drunkendroidserver;

import itu.malta.drunkendroidserver.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



public class updateTrip {

	String IMEINumber, name;
	long startDateTime,endDateTime;
	int tripID = -1; 
	
	public updateTrip(int tripId,String IMEINumber,long startDateTime,long endDateTime, String name) {
		this.IMEINumber = IMEINumber;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.tripID = tripId;
		this.name = name;
	}
	
	public void execute() throws SQLException {
		Connection conn = DatabaseConnection.getInstance().getConn();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("Update trip set name = " + name + ", startDateTime = " + startDateTime + ", endDateTime "+ endDateTime + " where id = " + tripID);
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
}
