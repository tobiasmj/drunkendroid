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

public class MockInsertMoodDatabaseConnection implements IDatabaseConnection {

	private MockInsertMoodDatabaseConnection(){
	}

	private static class MockInsertMoodDatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new MockInsertMoodDatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return MockInsertMoodDatabaseConnectionSingleton.INSTANCE;
	}
	
	@Override
	public Connection getConn() {
		
		ResultSet rs = createMock(java.sql.ResultSet.class);
		
		Statement stmt = createMock(java.sql.Statement.class);
		Connection mockConnection = createMock(java.sql.Connection.class);

		try {
			expect(rs.getInt(1)).andReturn(42).times(1);
			expect(rs.next()).andReturn(true).times(1);
			rs.close();
			EasyMock.expectLastCall();
		    replay(rs);
		    
		    expect(stmt.executeUpdate("Insert into Mood(trip,timeStamp,latitude,longitude,mood) values (1,1,10.0,10.0,100)")).andReturn(1);
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
