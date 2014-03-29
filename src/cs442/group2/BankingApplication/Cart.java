package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Cart {
	private int customerID;
	HashMap<Item, Integer> items;
	
	private static String sqlSelectCart = "SELECT * FROM Cart NATURAL JOIN Item WHERE customerID = ?";
	
	@Override
	public String toString() {
		String output = "";
		List<Item> allItems = this.getAllItems();
		for(Item item: allItems){
			output += "(itemID=" + item.getItemID() + ") Total = " + (item.getItemCost() * this.getQuantityOfItem(item)
					) + "\n";
		}
		return output;
	}
	public List<Item> getAllItems() {
		// Get all unique items from cart
		ArrayList<Item> result = new ArrayList<Item>();

		HashSet<Item> setOfItems = new HashSet<Item>();
		Iterator<Item> iterator = items.keySet().iterator();
		while(iterator.hasNext()){
			
			setOfItems.add(iterator.next());
		}
		result.addAll(setOfItems);
		return result;
	}

	
	
	public double getItemTotalCost(){
		List<Item> items = this.getAllItems();
		double totalFromCart = 0.0d;
		for (Item item : items) {
			totalFromCart += (item.getItemCost() * this.getQuantityOfItem(item));
		}
		return totalFromCart;
	}
	private void updateCart() {
		items.clear();
		try {
			Connection conn = BankConnect.getConnection();
			PreparedStatement statement = conn.prepareStatement(sqlSelectCart);
			statement.setInt(1, customerID);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int itemID = rs.getInt("itemid");
				String itemName = rs.getString("itemname");
				int itemQuantity = rs.getInt("quantity");
				double itemCost = rs.getDouble("itemcost");

				Item item = new Item(itemID, itemName, itemQuantity, itemCost);
				int cartQuantity = rs.getInt("cartquantity");
				items.put(item, cartQuantity);

			}

		} catch (Exception e) {
			Reporting.err.println(e);
		}
	}

	public Cart(int customerID) {
		// Populate data into items variable from Cart table
		items = new HashMap<Item, Integer>();
		this.customerID = customerID;
		updateCart();
		
	}

	public int getCustomerID() {
		return customerID;
	}

	
	public int getQuantityOfItem(Item item) {
		Integer itemQuantity = items.get(item);
		if (itemQuantity == null)
			return 0;
		else
			return itemQuantity.intValue();
	}

	public void addItem(Item item, int quantity) throws Exception {

		Connection conn = BankConnect.getConnection();

		String SQLCartInsert = "INSERT INTO Cart(customerid, itemid, cartquantity) VALUES (?, ?, ?);";
		try {
			try {

				if (conn != null) {
					PreparedStatement statement = conn
							.prepareStatement(SQLCartInsert);
					statement.setInt(1, customerID);
					statement.setInt(2, item.getItemID());
					statement.setInt(3, quantity);
					statement.executeUpdate();
					conn.commit();
				}
			} catch (SQLException e) {
				Reporting.err.println(e);
				conn.rollback();
			}

		} catch (Exception e) {
			Reporting.err.println(e);
			throw e;
		}
		updateCart();
	}

	public void removeItem(Item item) {
		int itemID = item.getItemID();

		String SQLCartDelete = "DELETE FROM Cart WHERE customerID = ? AND itemID = ?;";
		try {
			Connection conn = BankConnect.getConnection();
			try {

				if (conn != null) {

					PreparedStatement statement = conn
							.prepareStatement(SQLCartDelete);
					statement.setInt(1, customerID);
					statement.setInt(2, itemID);
					int i = statement.executeUpdate();
					conn.commit();
					System.out.println(i);
				}
			} catch (SQLException e) {
				Reporting.err.println(e);
				conn.rollback();
			}

		} catch (SQLException e) {
			Reporting.err.println(e);
		}

		updateCart();

	}

	public void changeQuantityOfItemInCart(Item item, int quantity) {

		int itemID = item.getItemID();

		String SQLCartUpdate = "UPDATE Cart SET cartQuantity = ? WHERE customerID = ? AND itemID = ?;";
		try {
			Connection conn = BankConnect.getConnection();
			try {

				if (conn != null) {

					PreparedStatement statement = conn
							.prepareStatement(SQLCartUpdate);
					statement.setInt(1, quantity);
					statement.setInt(2, customerID);
					statement.setInt(3, itemID);
					statement.executeUpdate();
					conn.commit();
				}
			} catch (SQLException e) {
				Reporting.err.println(e);
				conn.rollback();
			}

		} catch (SQLException e) {
			Reporting.err.println(e);
		}

		updateCart();
	}

}
