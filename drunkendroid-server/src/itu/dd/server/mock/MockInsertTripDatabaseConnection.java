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

public class MockInsertTripDatabaseConnection implements IDatabaseConnection {

	private MockInsertTripDatabaseConnection(){
	}

	private static class MockInsertTripDatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new MockInsertTripDatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return MockInsertTripDatabaseConnectionSingleton.INSTANCE;
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

			expect(stmt.executeUpdate("Insert into Trip (IMEINumber,startTimeStamp,endTimeStamp,name) values (111111,123456,123456,\"Test trip\")", 1)).andReturn(1);
			expect(stmt.getGeneratedKeys()).andReturn(rs);
			stmt.close();
			EasyMock.expectLastCall();
			replay(stmt);

			expect(mockConnection.createStatement()).andReturn(stmt);
			replay(mockConnection);

		} catch (SQLException e) {
			// just print stack trace since this object is only used for unit testing
			e.printStackTrace();
		}

		return mockConnection;
	}
}
