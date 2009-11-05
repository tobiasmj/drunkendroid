package drunkendroidserver.Util;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
	private String connURL = null;
	private DatabaseConnection(){
		// create and load default properties
		Properties defaultProps = new Properties();
		try {
			//FileInputStream in = new FileInputStream("Properties.xml");
			defaultProps.loadFromXML(DatabaseConnection.class.getResourceAsStream("Properties.xml"));
			//in.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		connURL = defaultProps.getProperty("DatabaseConncetionURL");
		if(connURL == null) {
			// throw some exception! can't read connection url from properties file...
			connURL = "jdbc:mysql://localhost/drunkendroid-server?user=drunkendroid&password=81sliema";
		}
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