package edu.ncsu.csc326.coffeemaker.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.ncsu.csc326.coffeemaker.Recipe;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;

public class RecipeBookDB {
	
	public static boolean addRecipe(Recipe r) {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean recipeAdded = false;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("INSERT INTO recipe_book VALUES(?,?,?,?,?,?)");
			stmt.setString(1, r.getName());
			stmt.setInt(2, r.getPrice());
			stmt.setInt(3, r.getAmtCoffee());
			stmt.setInt(4, r.getAmtMilk());
			stmt.setInt(5, r.getAmtSugar());
			stmt.setInt(6, r.getAmtChocolate());
			int updated = stmt.executeUpdate();
			if (updated == 1) {
				recipeAdded = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return recipeAdded;
	}
	
	public static String deleteRecipe(String name) {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("DELETE FROM recipe_book WHERE name=?");
			stmt.setString(1, name);
			int result = stmt.executeUpdate(); 
			if (result == 0) { //Nothing was deleted
				name = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return name;
	}
	
	public static String editRecipe(String name, Recipe r) {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("UPDATE recipe_book SET price=?, amtCoffee=?, amtMilk=?, amtSugar=?, amtChocolate=? WHERE name=?");
			stmt.setInt(1, r.getPrice());
			stmt.setInt(2, r.getAmtCoffee());
			stmt.setInt(3, r.getAmtMilk());
			stmt.setInt(4, r.getAmtSugar());
			stmt.setInt(5, r.getAmtChocolate());
			stmt.setString(6, name);
			int result = stmt.executeUpdate();
			if (result == 0) { //nothing was updated
				name = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return name;
	}
	
	public static Recipe getRecipe(String name) throws RecipeException {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		Recipe r = null;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("SELECT * FROM recipe_book WHERE name=?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			//Only one result b/c name is primary key
			if (rs.next()) {
				r = new Recipe();
				r.setName(name);
				r.setPrice(rs.getString("price"));
				r.setAmtCoffee(rs.getString("amtCoffee"));
				r.setAmtMilk(rs.getString("amtMilk"));
				r.setAmtSugar(rs.getString("amtSugar"));
				r.setAmtChocolate(rs.getString("amtChocolate"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return r;
	}
	
	public static Recipe[] getRecipes() throws RecipeException {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("SELECT * FROM recipe_book");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Recipe r = new Recipe();
				r.setName(rs.getString("name"));
				r.setPrice(rs.getString("price"));
				r.setAmtCoffee(rs.getString("amtCoffee"));
				r.setAmtMilk(rs.getString("amtMilk"));
				r.setAmtSugar(rs.getString("amtSugar"));
				r.setAmtChocolate(rs.getString("amtChocolate"));
				recipes.add(r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		Recipe [] recipeArray = new Recipe[recipes.size()];
		for (int i = 0; i < recipeArray.length; i++) {
			recipeArray[i] = recipes.get(i);
		}
		return recipeArray;
	}
	
	public static boolean recipeExists(String name) {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean recipeExists = false;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("SELECT * FROM recipe_book WHERE name=?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			//Only one result b/c name is primary key
			if (rs.next()) {
				if (rs.getString("name").equals(name)) {
					recipeExists = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return recipeExists;
	}
	
	public static int recipeCount() {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM recipe_book");
			ResultSet rs = stmt.executeQuery();
			//Only one result b/c name is primary key
			rs.next();
			count = rs.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return count;
	}

}
