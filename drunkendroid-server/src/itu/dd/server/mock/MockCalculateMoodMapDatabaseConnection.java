package itu.dd.server.mock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import itu.dd.server.interfaces.IDatabaseConnection;

/**
 * Used for UnitTests, creates a Mock database object instead of a prober database connection.
 */

public class MockCalculateMoodMapDatabaseConnection implements IDatabaseConnection {

	private MockCalculateMoodMapDatabaseConnection(){
	}

	private static class MockCalculateMoodMapDatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new MockCalculateMoodMapDatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return MockCalculateMoodMapDatabaseConnectionSingleton.INSTANCE;
	}
	
	@Override
	public Connection getConn() {
		
		ResultSet rs = createMock(java.sql.ResultSet.class);
		
		Statement stmt = createMock(java.sql.Statement.class);
		Connection mockConnection = createMock(java.sql.Connection.class);

		try {
			
			//Statement 
			expect(stmt.executeQuery("select mood, longitude, latitude from Mood where timeStamp between 1 and 10 and longitude between 5.0 and 10.0 and latitude between 5.0 and 10.0")).andReturn(rs);
			expect(stmt.getResultSet()).andReturn(rs);

		    stmt.close();
		    EasyMock.expectLastCall();
		    replay(stmt);
		    
		    // resultSet
			expect(rs.next()).andReturn(true);
			expect(rs.getLong("dateTime")).andReturn(5L);
			expect(rs.getDouble("latitude")).andReturn(10.0D);
			expect(rs.getDouble("longitude")).andReturn(10.0D);
			expect(rs.getInt("mood")).andReturn(1);
			
			expect(rs.next()).andReturn(true);
			expect(rs.getLong("dateTime")).andReturn(5L);
			expect(rs.getDouble("latitude")).andReturn(5.0D);
			expect(rs.getDouble("longitude")).andReturn(5.0D);
			expect(rs.getInt("mood")).andReturn(1);
			expect(rs.next()).andReturn(false);
			
			
			rs.close();
			EasyMock.expectLastCall();
			
		    replay(rs);
			expect(mockConnection.createStatement()).andReturn(stmt);
			replay(mockConnection);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mockConnection;
	}
}
