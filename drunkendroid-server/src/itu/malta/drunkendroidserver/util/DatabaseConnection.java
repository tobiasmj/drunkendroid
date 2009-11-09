package drunkendroidserver.Util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private Connection conn = null;
	private DatabaseConnection(){
		try { 
			conn = DriverManager.getConnection("jdbc:mysql://localhost/drunkendroid-server?user=drunkendroid&password=81sliema");
			} catch (SQLException se) {
				//TODO : handle exception
			}
		
	}
	
	private static class DatabaseConnectionSingleton {
		private static final DatabaseConnection INSTANCE = new DatabaseConnection();
	}

	public static DatabaseConnection getInstance(){
		return DatabaseConnectionSingleton.INSTANCE;
	}
	public Connection getConn(){
		return conn;
	}
}