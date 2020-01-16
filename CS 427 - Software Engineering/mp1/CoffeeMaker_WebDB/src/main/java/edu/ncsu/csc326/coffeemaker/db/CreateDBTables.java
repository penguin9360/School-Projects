package edu.ncsu.csc326.coffeemaker.db;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class CreateDBTables {

	public static void createTables() {
		createRecipeBookTable();
		createInventoryTable();
		createCoffeePurchasedTable();
	}
	
	private static void createRecipeBookTable() {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS recipe_book (" +
					"name varchar(25) NOT NULL, " +
					"price int NOT NULL, " +
					"amtCoffee int NOT NULL, " +
					"amtMilk int NOT NULL, " +
					"amtSugar int NOT NULL, " +
					"amtChocolate int NOT NULL) " );
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
	}
	
	private static void createInventoryTable() {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS inventory (" +
					"coffee int NOT NULL, " +
					"milk int NOT NULL, " +
					"sugar int NOT NULL, " +
					"chocolate int NOT NULL)");
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
	}
	
	private static void createCoffeePurchasedTable() {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS coffee_purchased (" +
					//"purchaseDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
					"name varchar(25) NOT NULL, " +
					"paid int NOT NULL, " +
					"amountReturned int NOT NULL)");
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
	}

}
