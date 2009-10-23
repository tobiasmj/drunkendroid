package drunkendroidserver.Command;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import drunkendroidserver.Util.DatabaseConnection;

public class DBInsertReadingCommand implements ICommand {

	long readingTime;
	double latitude, longtitude;
	int mood = -1;
	private int tripID = 0;
	
	public int getTripID() {
		return tripID;
	}

	public void setTripID(int tripID) {
		this.tripID = tripID;
	}

	public DBInsertReadingCommand (long readingTime, double latitude, double longtitude, int mood ) {
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.mood = mood;
	}
	
	@Override
	public void Execute() {
		
		Connection conn = DatabaseConnection.getInstance().getConn();
		String insertReadingProcedure = "{ Call insertTrip(" + tripID + "," + readingTime + "," + latitude + "," + longtitude + "," + "mood"+ ") }";
		try {
			CallableStatement cs = conn.prepareCall(insertReadingProcedure);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.execute();
			tripID = cs.getInt(1);
		} catch (SQLException e) {
			// HandleSQL Error 
			// TODO HANDLE ERRORS 
		}

	}

}
