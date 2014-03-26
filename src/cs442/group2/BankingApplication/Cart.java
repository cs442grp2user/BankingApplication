package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Prasad Nair and Akshay
 * 
 * Cart Class keeps a record of all cart related details.
 * Every customer has 1 cart.
 * 
 */

public class Cart {
	private int customerID;
	HashMap<Item, Integer> items; // HashMap<Item, Quantity>

	// Cart Constructor
	public Cart(int customerID) {
		// Populate data into items variable from Cart table
		items = new HashMap<Item, Integer>();
		this.customerID = customerID;

	}

	public int getCustomerID() {
		return customerID;
	}
	
	// Returns all the items in a cart for a particular customer
	public List<Item> getAllItemsInCart() {
		// Get all unique items from cart
		ArrayList<Item> items = new ArrayList<Item>();
		
			// int itemID = item.getItemID(item);
			String SQLCartSelect = "Select itemid, quantity FROM cart WHERE customerid = ?;";
			try{
				Connection conn = BankConnect.getConnection();
				try{
				
					if(conn != null){
						
						PreparedStatement statement = conn.prepareStatement(SQLCartSelect);
						statement.setInt(1, customerID);
						ResultSet rs = null;
						rs =  statement.executeQuery();
					
						while(rs.next()){
					         //Retrieve by column name
					         int itemID = rs.getInt("itemid");
					         int itemQuantity = rs.getInt("quantity");

					         String itemName = Item.getItemName(itemID);
					         double itemCost = Item.getItemCost(itemID);
					         Item item = new Item(itemID, itemName,itemQuantity, itemCost);
					         items.add(item);
						}      
					}
				}
				catch(SQLException e){
					Reporting.err.println(e);
					conn.rollback();
				}
			
				finally{
					// conn.close();
				}   
			
			}
			catch(SQLException e){
				Reporting.err.println(e);
			}

		
			return items;
	}

	// Returns the purchase quantity of an item in the cart
	public int getQuantityOfItem(Item item) {
		Integer itemQuantity = items.get(item);		
		if (itemQuantity == null)
			return 0;
		else
			return itemQuantity.intValue();
	}

	// Customer object required to removeItem from the Cart of the correct customer
	public void addItemToCart(Item item, int quantity) {
		// Save in Cart table too
		items.put(item, quantity);
	
		int itemID = item.getItemID(item);
		Connection conn = BankConnect.getConnection();
		System.out.println(customerID+", "+itemID+", " + quantity);
		String SQLCartInsert = "INSERT INTO cart VALUES (?, ?, ?);";
		try{
			try{
			
				if(conn != null){
					PreparedStatement statement = conn.prepareStatement(SQLCartInsert);
					statement.setInt(1, customerID);
					statement.setInt(2, itemID);
					statement.setInt(3, quantity);
					statement.executeUpdate();
					conn.commit();
				}
			}
			catch(SQLException e){
				Reporting.err.println(e);
				conn.rollback();
			}
		
			finally{
				
				//conn.close();
			}   
		
		}
		catch(Exception e){
			Reporting.err.println(e);
		}
	}

	// Customer object required to removeItem from the Cart of the correct customer
	public void removeItemFromCart(Item item) {
		// Remove from Cart table too
		items.remove(item);
		int itemID = item.getItemID(item);
		
		String SQLCartDelete = "DELETE FROM cart WHERE customerid = ? AND itemid = ?;";
		try{
			Connection conn = BankConnect.getConnection();
			try{
			
				if(conn != null){
				
					PreparedStatement statement = conn.prepareStatement(SQLCartDelete);
					statement.setInt(1, customerID);
					statement.setInt(2, itemID);
					int i = statement.executeUpdate();
					conn.commit();
					System.out.println(i);
				}
			}
			catch(SQLException e){
				Reporting.err.println(e);
				conn.rollback();
			}
		
			finally{
				//conn.close();
			}   
		
		}
		catch(SQLException e){
			Reporting.err.println(e);
		}
		
	}

	// To change the purchase quantity of an item in the cart
	public void changeQuantityOfItemInCart(Item item, int quantity) {
		// Update in Cart table too
		items.put(item, quantity);
		
		int itemID = item.getItemID(item);
		
		String SQLCartUpdate = "UPDATE cart SET quantity = ? WHERE customerid = ? AND itemid = ?;";
		try{
			Connection conn = BankConnect.getConnection();
			try{
			
				if(conn != null){
				
					PreparedStatement statement = conn.prepareStatement(SQLCartUpdate);
					statement.setInt(1, quantity);
					statement.setInt(2, customerID);
					statement.setInt(3, itemID);
					statement.executeUpdate();
					conn.commit();
				}
			}
			catch(SQLException e){
				Reporting.err.println(e);
				conn.rollback();
			}
		
			finally{
				//conn.close();
			}   
		
		}
		catch(SQLException e){
			Reporting.err.println(e);
		}
	}



//	public static void main(String[] args){
//		Cart cart = new Cart(1);
//		
//		List<Item> item = cart.getAllItemsInCart();
//		for(Item item1: item){
//			System.out.println(item1.getItemName());
//		}
//		
//		Item item13 = new Item(4,"Nair",3,3);
//		cart.addItemToCart(item13,6);
//		
//		Item item12 = new Item(2,"Nair",3,3);
//		cart.removeItemFromCart(item12);
//	}
	

}



