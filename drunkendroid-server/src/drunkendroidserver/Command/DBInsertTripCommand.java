package drunkendroidserver.Command;


import java.sql.Connection;
import java.util.*;
import drunkendroidserver.Util.DatabaseConnection;

public class DBInsertTripCommand implements ICommand {

	LinkedList<DBInsertReadingCommand> readingCommands = new LinkedList<DBInsertReadingCommand>();
	String IMEINumber;
	long startDateTime,endDateTime;
	public DBInsertTripCommand(String IMEINumber,long startDateTime,long endDateTime) {
		this.IMEINumber = IMEINumber;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	
	}
	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		Connection conn = DatabaseConnection.getInstance().getConn();
		
	}
	public void addCommand(DBInsertReadingCommand readingComm) {
		readingCommands.add(readingComm);
	}
}
