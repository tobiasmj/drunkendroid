package itu.malta.drunkendroidserver.mock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import itu.malta.drunkendroidserver.interfaces.IDatabaseConnection;

/**
 * Used for UnitTests, creates a Mock database object instead of a prober database connection.
 */

public class MockInsertCallDatabaseConnection implements IDatabaseConnection {

	private MockInsertCallDatabaseConnection(){
	}

	private static class MockInsertCallDatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new MockInsertCallDatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return MockInsertCallDatabaseConnectionSingleton.INSTANCE;
	}
	
	@Override
	public Connection getConn() {
		
		ResultSet rs = createMock(java.sql.ResultSet.class);
		
		Statement stmt = createMock(java.sql.Statement.class);
		Connection mockConnection = createMock(java.sql.Connection.class);

		try {
			expect(rs.next()).andReturn(true).times(1);
			rs.close();
			EasyMock.expectLastCall();
		    replay(rs);
		    
		    expect(stmt.executeUpdate("Insert into Call(trip,timeStamp,latitude,longitude,caller,reciever,endTimeStamp) values (1,1,10.0,10.0,004551883250,004551883250,2)")).andReturn(1);
		    //expect(stmt.getGeneratedKeys()).andReturn(rs);
		    stmt.close();
		    EasyMock.expectLastCall();
		    //expect(stmt.close()).andStubReturn(void);
		    replay(stmt);
		    
			expect(mockConnection.createStatement()).andReturn(stmt);
			replay(mockConnection);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mockConnection;
	}
}
