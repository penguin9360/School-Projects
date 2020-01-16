package edu.ncsu.csc326.coffeemaker.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.ResultSet;

import edu.ncsu.csc326.coffeemaker.Inventory;
import edu.ncsu.csc326.coffeemaker.Recipe;

public class InventoryDB {

	
	public static boolean addInventory(int[] arr) {
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean InventoryAdded = false;
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("UPDATE inventory SET coffee=?, milk=?, sugar=?, chocolate=?");
			stmt.setInt(1, arr[0]);
			stmt.setInt(2, arr[1]);
			stmt.setInt(3, arr[2]);
			stmt.setInt(4, arr[3]);
			int updated = stmt.executeUpdate();
			if (updated == 1) {
				InventoryAdded = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return InventoryAdded;
	}
	
	public static void useInventory(Recipe r) {
        DBConnection db = new DBConnection();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = db.getConnection();
			
            stmt = conn.prepareStatement("UPDATE inventory SET coffee=?, milk=?, sugar=?, chocolate=?");
            stmt.setInt(1, checkInventory()[0] - r.getAmtCoffee());
            stmt.setInt(2, checkInventory()[1] - r.getAmtMilk());
            stmt.setInt(3, checkInventory()[2] - r.getAmtSugar());
            stmt.setInt(4, checkInventory()[3] - r.getAmtChocolate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn, stmt);
        }
    }
	
	public static int[] checkInventory(){
		DBConnection db = new DBConnection();
		Connection conn = null;
		PreparedStatement stmt = null;
		int[] wtf = new int[4];
		try {
			conn = db.getConnection();
			stmt = conn.prepareStatement("SELECT * FROM inventory");
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				wtf[0] = Integer.parseInt(rs.getString("coffee"));
				wtf[1] = Integer.parseInt(rs.getString("milk"));
				wtf[2] = Integer.parseInt(rs.getString("sugar"));
				wtf[3] = Integer.parseInt(rs.getString("chocolate"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn, stmt);
		}
		return wtf;
	}
	
	public static boolean setup(int[] arr) {
        DBConnection db = new DBConnection();
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean InventoryAdded = false;
        try {
            conn = db.getConnection();
            stmt = conn.prepareStatement("UPDATE Inventory SET coffee=?, milk=?, sugar=?, chocolate=?");
            if (arr[0] != 0) {
                stmt.setInt(1, arr[0]);
            }
            if (arr[1] != 0) {
                stmt.setInt(2, arr[1]);
            }
            if (arr[2] != 0) {
                stmt.setInt(3, arr[2]);
            }
            if (arr[3] != 0) {
                stmt.setInt(4, arr[3]);
            }
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                InventoryAdded = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn, stmt);
        }
        return InventoryAdded;
    } 
	
}

