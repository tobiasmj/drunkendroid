package drunkendroidserver.Command;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import drunkendroidserver.Util.DatabaseConnection;

public class DBInsertTripCommand implements ICommand {

	LinkedList<DBInsertReadingCommand> readingCommands = new LinkedList<DBInsertReadingCommand>();
	String IMEINumber;
	long startDateTime,endDateTime;
	int tripID = 0; 
	public DBInsertTripCommand(String IMEINumber,long startDateTime,long endDateTime) {
		this.IMEINumber = IMEINumber;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		
	}
	@Override
	public void Execute() {
		Connection conn = DatabaseConnection.getInstance().getConn();
		String insertTripProcedure = "{ Call insertTrip(" + IMEINumber + "," + startDateTime + "," + endDateTime + ") }";
		try {
			CallableStatement cs = conn.prepareCall(insertTripProcedure);
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
	}
	public void addCommand(DBInsertReadingCommand readingComm) {
		readingCommands.add(readingComm);
	}
}
