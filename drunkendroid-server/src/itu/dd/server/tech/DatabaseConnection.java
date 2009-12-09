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
			// trying to externalise the connection string. currently dossen't work
			//databaseConfiguration.getString("DatabaseConnection.1"); //$NON-NLS-1$
		
	}
	
	private static class DatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new DatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return DatabaseConnectionSingleton.INSTANCE;
	}

	public Connection getConn(){
		Connection conn = null;
		try{
			conn  = DriverManager.getConnection(_connURL);
		} catch (SQLException se){
			// ignore for new since we can't do much about it?
		}
		return conn;
	}
}