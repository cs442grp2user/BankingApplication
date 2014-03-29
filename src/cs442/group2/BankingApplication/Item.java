package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

@SuppressWarnings("unused")
public class Item {
	private int itemID;
	private String itemName;
	private int itemQuantity;
	private double itemCost;
	
	@Override
	public String toString() {
		return String.format("Item(itemID=%d)[%s]", itemID, itemName);
	}

	public static void setQuantity(int itemID, int quantity) throws Exception {
		// Add DB Code, to update quantity in Item table
		// This code is only called after an order is succesful

		// Do NOT commit in this code, because Order may produce an error

		String updateTableSQL = "UPDATE item SET quantity = quantity - ?  WHERE itemid = ?";
		try {
			Connection conn = BankConnect.getConnection();
			PreparedStatement statement = conn.prepareStatement(updateTableSQL);
			statement.setInt(1, quantity);
			statement.setInt(2, itemID);
			statement.executeUpdate();

			// DO NOT COMMIT

		} catch (Exception e) {
			Reporting.err.println(e);
			throw e;
		}

	}

	public String getItemName() {
		return itemName;
	}

	public static List<Item> getAllItems() {
		// SQL Hack, HA HA ! :D
		return searchItem("");
	}

	public static List<Item> searchItem(String itemName) {
		ArrayList<Item> result = new ArrayList<Item>();
		// Add DB code.

		String SQLSearchItem = "SELECT * FROM Item WHERE itemName LIKE '%"
				+ itemName + "%'";

		
		try {
			Connection conn = BankConnect.getConnection();
			try {
				if (conn != null) {
					ResultSet rs = null;
					PreparedStatement statement = conn
							.prepareStatement(SQLSearchItem);
					// statement.setString(1, itemName);
					try {
						rs = statement.executeQuery();
					} catch (SQLException e) {
						System.out.println(e);
					}
					while (rs.next()) {
						// Retrieve by column name
						int itemID = rs.getInt("itemID");
						itemName = rs.getString("itemName");
						int itemQuantity = rs.getInt("quantity");
						double itemCost = rs.getDouble("itemCost");

						Item item = new Item(itemID, itemName, itemQuantity,
								itemCost);
						result.add(item);

					}
				}
			} catch (SQLException e) {
				Reporting.err.println(e);
				conn.rollback();
			}
		} catch (SQLException e) {
			Reporting.err.println(e);
		}

		return result;
	}

	public int getItemID() {
		return itemID;
	}

	public int getItemQuantity() {
		return itemQuantity;
	}

	public double getItemCost() {
		return itemCost;
	}

	public Item(int itemID, String itemName, int itemQuantity, double itemCost) {
		this.itemID = itemID;
		this.itemName = itemName;
		this.itemQuantity = itemQuantity;
		this.itemCost = itemCost;

	}

	public Item(int itemID) {
		this.itemID = itemID;
		try {
			Connection conn = BankConnect.getConnection();
			if (conn != null) {
				PreparedStatement statement = conn
						.prepareStatement(shoppingItem);
				statement.setInt(1, itemID);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					itemName = rs.getString("itemName");
					itemQuantity = rs.getInt("itemQuantity");
					itemCost = rs.getDouble("itemCost");
				} else {
					throw new RuntimeException("No Item with itemID = "
							+ itemID);
				}
			} else {
				throw new RuntimeException("Connection not established");
			}
		} catch (Exception e) {
			Reporting.err.println(e);
			itemName = "ERROR";
			itemQuantity = -1;
			itemCost = 0;
		}
	}

	private static String shoppingItem = "SELECT * FROM Item WHERE itemID = ? ;";
}
