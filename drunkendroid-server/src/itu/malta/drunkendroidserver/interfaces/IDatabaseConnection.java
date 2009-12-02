package itu.malta.drunkendroidserver.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Interface for databaseConnections.
 */
public interface IDatabaseConnection {

	public Connection getConn() throws SQLException;

}