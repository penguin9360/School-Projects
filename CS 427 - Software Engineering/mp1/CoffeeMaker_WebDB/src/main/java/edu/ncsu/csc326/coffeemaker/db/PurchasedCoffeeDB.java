package edu.ncsu.csc326.coffeemaker.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.ncsu.csc326.coffeemaker.Recipe;

public class PurchasedCoffeeDB {
	
	public static boolean addCoffeePurchased(Recipe r, int amtPaid, int change) {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean recipeAdded = false;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("INSERT INTO coffee_purchased VALUES(?,?,?)");
			stmt.setString(1, r.getName());
			stmt.setInt(2, amtPaid);
			stmt.setInt(3, change);
			stmt.executeUpdate();
			recipeAdded = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return recipeAdded;
	}

}
