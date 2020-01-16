package edu.ncsu.csc326.coffeemaker.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnection {
	
	public DBConnection(){
	}
	
	public Connection getConnection() throws SQLException {
		try {
			return ((DataSource) (((Context) new InitialContext().lookup("java:comp/env")))
					.lookup("jdbc/coffeemaker")).getConnection();
		} catch (NamingException e) {
			throw new SQLException(("Context Lookup Naming Exception: " + e.getMessage()));
		}
	}

	/**
	 * Close the prepared statement and the connection in a proper way
	 * 
	 * @param conn
	 * @param ps
	 */
	public static void closeConnection(Connection conn, PreparedStatement ps) {
		try {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			System.err.println("Error closing connections");
			e.printStackTrace();
		}
	}

}
