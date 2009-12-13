package itu.dd.server.mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import itu.dd.server.interfaces.IDatabaseConnection;

/**
 * Used for UnitTests, creates a Mock database object instead of a prober database connection.
 */

public class MockUpdateTripDatabaseConnection implements IDatabaseConnection {

	private MockUpdateTripDatabaseConnection(){
	}

	private static class MockUpdateTripDatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new MockUpdateTripDatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return MockUpdateTripDatabaseConnectionSingleton.INSTANCE;
	}

	@Override
	public Connection getConn() throws SQLException {

		Statement stmt = createMock(java.sql.Statement.class);
		Connection mockConnection = createMock(java.sql.Connection.class);

		expect(stmt.executeUpdate("Update trip set name = \'testTrip\' , startTimeStamp = 1, endTimeStamp = 2 where id = 1")).andReturn((int) 0);
		stmt.close();
		EasyMock.expectLastCall();
		replay(stmt);

		expect(mockConnection.createStatement()).andReturn(stmt);
		replay(mockConnection);


		return mockConnection;
	}
}
