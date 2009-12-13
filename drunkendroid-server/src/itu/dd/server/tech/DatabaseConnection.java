package itu.dd.server.tech;

import itu.dd.server.interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for getting a Database connection.
 */
public class DatabaseConnection implements IDatabaseConnection {
	private String _connURL = null;
	private DatabaseConnection(){
		_connURL = "jdbc:mysql://localhost/drunkendroid-server?user=drunkendroid&password=81sliema";
	}

	private static class DatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new DatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return DatabaseConnectionSingleton.INSTANCE;
	}

	public Connection getConn() throws SQLException{
		Connection conn = null;
		conn  = DriverManager.getConnection(_connURL);
		return conn;
	}
}