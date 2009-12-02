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

public class MockGetTripDatabaseConnection implements IDatabaseConnection {

	private MockGetTripDatabaseConnection(){
	}

	private static class MockGetTripDatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new MockGetTripDatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return MockGetTripDatabaseConnectionSingleton.INSTANCE;
	}
	
	@Override
	public Connection getConn() {
		
		ResultSet rs = createMock(java.sql.ResultSet.class);
		
		Statement stmt = createMock(java.sql.Statement.class);
		Connection mockConnection = createMock(java.sql.Connection.class);

		try {
			
			//Statement 
			expect(stmt.executeQuery("Select dateTime,longitude,latitude,mood from reading where trip = 1")).andReturn(rs);
		    expect(stmt.getResultSet()).andReturn(rs);

			expect(stmt.executeQuery("Select dateTime, longitude,latitude from Location where trip = 1")).andReturn(rs);
			expect(stmt.getResultSet()).andReturn(rs);

			expect(stmt.executeQuery("Select dateTime, longitude,latitude,caller,reciever,endTime from Call where trip = 1")).andReturn(rs);
			expect(stmt.getResultSet()).andReturn(rs);
			
			expect(stmt.executeQuery("Select dateTime, longitude,latitude,sender,reciever,message from SMS where trip = 1")).andReturn(rs);
			expect(stmt.getResultSet()).andReturn(rs);
			
			expect(stmt.executeQuery("Select name,startDateTime,endDateTime from trip where id = 1")).andReturn(rs);
			expect(stmt.getResultSet()).andReturn(rs);

		    stmt.close();
		    EasyMock.expectLastCall();
		    replay(stmt);
		    
		    // resultSet
			expect(rs.next()).andReturn(true);
			expect(rs.getLong("dateTime")).andReturn(1L);
			expect(rs.getDouble("latitude")).andReturn(1.0D);
			expect(rs.getDouble("longitude")).andReturn(2.0D);
			expect(rs.getInt("mood")).andReturn(1);
			expect(rs.next()).andReturn(false);
			

			expect(rs.next()).andReturn(true);
			expect(rs.getLong("dateTime")).andReturn(1L);
			expect(rs.getDouble("latitude")).andReturn(1D);
			expect(rs.getDouble("longitude")).andReturn(2D);
			expect(rs.next()).andReturn(false);
			
			expect(rs.next()).andReturn(true);
			expect(rs.getLong("dateTime")).andReturn(1L);
			expect(rs.getDouble("latitude")).andReturn(1D);
			expect(rs.getDouble("longitude")).andReturn(2D);
			expect(rs.getString("caller")).andReturn("004551883250");
			expect(rs.getString("reciever")).andReturn("004551883250");
			expect(rs.getLong("endTime")).andReturn(2L);
			expect(rs.next()).andReturn(false);
			
			expect(rs.next()).andReturn(true);
			expect(rs.getLong("dateTime")).andReturn(1L);
			expect(rs.getDouble("latitude")).andReturn(1D);
			expect(rs.getDouble("longitude")).andReturn(2D);
			expect(rs.getString("sender")).andReturn("004551883250");
			expect(rs.getString("reciever")).andReturn("004551883250");
			expect(rs.getString("message")).andReturn("test message");
			expect(rs.next()).andReturn(false);
			
			
			expect(rs.first()).andReturn(true).times(1);
			expect(rs.getLong("startDateTime")).andReturn(1L);
			expect(rs.getLong("endDateTime")).andReturn(2L);
			expect(rs.getString("name")).andReturn("testTrip");
			
			
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
