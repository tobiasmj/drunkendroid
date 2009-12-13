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

public class MockInsertSmsDatabaseConnection implements IDatabaseConnection {

	private MockInsertSmsDatabaseConnection(){
	}

	private static class MockInsertSmsDatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new MockInsertSmsDatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return MockInsertSmsDatabaseConnectionSingleton.INSTANCE;
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

			expect(stmt.executeUpdate("Insert into SMS(trip,timeStamp,latitude,longitude,sender,receiver) values (1,1,10.0,10.0,004551883250,004551883250,test message)")).andReturn(1);
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
