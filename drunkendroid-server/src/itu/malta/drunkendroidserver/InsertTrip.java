package itu.malta.drunkendroidserver;

import itu.malta.drunkendroidserver.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;



public class InsertTrip {

	LinkedList<InsertReading> readingCommands = new LinkedList<InsertReading>();
	String IMEINumber;
	long startDateTime;
	long endDateTime = -1;
	int tripID = -1; 
	String name;

	/**
	 * Constructor
	 * @param IMEINumber the phones imeinumber.
	 * @param startDateTime startdate of the trip.
	 * @param endDateTime end date of the trip.
	 * @param name the name of the trip.
	 */
	public InsertTrip(String IMEINumber,long startDateTime,long endDateTime, String name) {
		this.IMEINumber = IMEINumber;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.name = name;
	}
	public InsertTrip(String IMEINumber, long startDateTime, String name) {
		this.IMEINumber = IMEINumber;
		this.startDateTime = startDateTime;
		this.name = name;
	}
	
	/**
	 * Inserts the created Trip and any inserted readings in it.
	 * @return a long representing the newly created tripId.
	 * @throws SQLException thrown if and error occurs while communicating with the database.
	 */
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
	/**
	 * inserts a reading in the trip insert, so that it will be added when the execute method is invoked.
	 * @param readingComm the reading to inserted into the database.
	 */
	public void addCommand(InsertReading readingComm) {
		readingCommands.add(readingComm);
	}
}
