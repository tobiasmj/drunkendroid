package drunkendroidserver.Util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private String connURL = null;
	private DatabaseConnection(){
			connURL = "jdbc:mysql://localhost/drunkendroid-server?user=drunkendroid&password=81sliema";
			//conn = DriverManager.getConnection("jdbc:mysql://localhost/drunkendroid-server?user=drunkendroid&password=81sliema");	
	}
	
	private static class DatabaseConnectionSingleton {
		private static final DatabaseConnection INSTANCE = new DatabaseConnection();
	}

	public static DatabaseConnection getInstance(){
		return DatabaseConnectionSingleton.INSTANCE;
	}
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