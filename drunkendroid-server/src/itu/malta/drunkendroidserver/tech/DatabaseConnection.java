package itu.malta.drunkendroidserver.tech;

import itu.malta.drunkendroidserver.interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection implements IDatabaseConnection {
	private String connURL = null;
	private DatabaseConnection(){
			connURL = databaseConfiguration.getString("DatabaseConnection.1"); //$NON-NLS-1$
		
	}
	
	private static class DatabaseConnectionSingleton {
		private static final IDatabaseConnection INSTANCE = new DatabaseConnection();
	}

	public static IDatabaseConnection getInstance(){
		return DatabaseConnectionSingleton.INSTANCE;
	}
	/* (non-Javadoc)
	 * @see itu.malta.drunkendroidserver.util.IDatabaseConnection#getConn()
	 */
	public Connection getConn(){
		Connection conn = null;
		try{
			conn  = DriverManager.getConnection(connURL);
		} catch (SQLException se){
			// ignore for new since we can't do much about it?
		}
		return conn;
	}
}