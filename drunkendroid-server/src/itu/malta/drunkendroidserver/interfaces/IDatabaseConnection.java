package itu.malta.drunkendroidserver.interfaces;

import java.sql.Connection;

public interface IDatabaseConnection {

	public abstract Connection getConn();

}