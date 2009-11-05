package drunkendroidserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


import drunkendroidserver.Util.DatabaseConnection;

public class InsertTrip {

	LinkedList<InsertReading> readingCommands = new LinkedList<InsertReading>();
	String IMEINumber;
	long startDateTime,endDateTime;
	int tripID = -1; 
	String name;
	
	public InsertTrip(String IMEINumber,long startDateTime,long endDateTime, String name) {
		this.IMEINumber = IMEINumber;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.name = name;
	}
	
	public long execute() throws SQLException {
		Connection conn = DatabaseConnection.getInstance().getConn();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("Insert into Trip (IMEINumber,startDateTime,endDateTime,name) values (" + IMEINumber + "," + startDateTime+ "," + endDateTime + "," + "\"" + name + "\"" + ")", Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();

		    if (rs.next()) {
		        tripID = rs.getInt(1);
		        
				if (tripID != -1) {
					for(InsertReading com : readingCommands) {
						com.setTripID(tripID);
						com.execute();
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
		//Miserable failed attempt at using stored Procedures, which sucks balls when using return values.....
		/*Connection conn = DatabaseConnection.getInstance().getConn();
		String insertTripProcedure = "{ Call insertTrip(?,?,?,?) }";
		try {
			CallableStatement cs = conn.prepareCall(insertTripProcedure);
			cs.setString(2, IMEINumber);
			cs.setLong(3, startDateTime);
			cs.setLong(4, endDateTime);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.execute();
			tripID = cs.getInt(1);
			if (tripID != 0) {
				for(DBInsertReadingCommand com : readingCommands) {
					com.setTripID(tripID);
					com.Execute();
				}
			} else {
				// Throw exception about failed trip insert
			}
		} catch (SQLException e) {
			// HandleSQL Error 
			// TODO HANDLE ERRORS 
		}
		*/
		
	}
	public void addCommand(InsertReading readingComm) {
		readingCommands.add(readingComm);
	}
}
