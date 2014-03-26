package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.*;

@SuppressWarnings("unused")
public class Item  {
	private int itemID;
	private String itemName;
	private int itemQuantity;
	private double itemCost;
	
	public static void main(String [ ] args)
	{
	      //setQuantity(1,1);
	      searchItem("Game");
	      //getItemName(1);
	      //getItemCost(1);
	}
	
	public static void setQuantity(int itemID, int quantity) {
		// Add DB Code, to update quantity in Item table
		// This code is only called after an order is succesful
		ArrayList<Item> result = new ArrayList<Item>();
		
			System.out.println("in setquantity");
			String updateTableSQL = "UPDATE item SET quantity = quantity - ?  WHERE itemid = ?";
			String updateTableSQL2 = "SELECT * FROM item  WHERE itemid = ?";
			System.out.println(updateTableSQL);
			ResultSet rs=null;
			try{
				Connection conn = BankConnect.getConnection();
				
				try{
					if (conn!=null){
						PreparedStatement statement = conn.prepareStatement(updateTableSQL);
						statement.setInt(1, quantity);
						statement.setInt(2, itemID);
						PreparedStatement statement2 = conn.prepareStatement(updateTableSQL2);
						statement2.setInt(1, itemID);
						
						try{
							statement.executeUpdate();
							rs =  statement2.executeQuery();
						} catch (SQLException e){
							System.out.println(e);
						}
						
						while(rs.next()){
							int temp = rs.getInt("quantity"); 
							System.out.println(temp);
							
						}
						
						}
					
										
				}catch(SQLException e){
					
				}
				
			}
			finally{
				
			}
		
	
	}
	
	
	public String getItemName(int itemID) { 
				
			String SQLSearchItem = "Select itemName FROM item WHERE itemID=?";
			
			System.out.println(SQLSearchItem);
			try{
				Connection conn = BankConnect.getConnection();
				try{
					if(conn != null){
						PreparedStatement statement = conn.prepareStatement(SQLSearchItem);
						statement.setInt(1, itemID);
						ResultSet rs = null;
						try{
							rs =  statement.executeQuery();
						} catch (SQLException e){
							System.out.println(e);
						}
						
						while(rs.next()){
							String itemName = rs.getString("itemName"); 
							System.out.println(itemName);
							}
						
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

			
		
	return itemName;
	}
	
	public static List<Item> getItemCost(int itemID) { 
		ArrayList<Item> result = new ArrayList<Item>();
		
		
			String SQLSearchItem = "Select itemcost FROM item WHERE itemID=?";
			
			System.out.println(SQLSearchItem);
			try{
				Connection conn = BankConnect.getConnection();
				try{
					if(conn != null){
						PreparedStatement statement = conn.prepareStatement(SQLSearchItem);
						statement.setInt(1, itemID);
						ResultSet rs = null;
						try{
							rs =  statement.executeQuery();
						} catch (SQLException e){
							System.out.println(e);
						}
						
						while(rs.next()){
							float itemcost = rs.getFloat("itemcost"); 
							System.out.println(itemcost);
						}
						
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

			
		
	return result;
	}
	
	public static List<Item> searchItem(String itemName) { 
		ArrayList<Item> result = new ArrayList<Item>();
		// Add DB code.
		
		String SQLSearchItem = "Select * FROM item WHERE itemname LIKE '%"+itemName+"%'";
		
		System.out.println(SQLSearchItem);
		try{
			Connection conn = BankConnect.getConnection();
			try{
				if(conn != null){
					ResultSet rs=null;
					PreparedStatement statement = conn.prepareStatement(SQLSearchItem);
					//statement.setString(1, itemName);
					try{
					 rs =  statement.executeQuery();
					}catch(SQLException e){
						System.out.println(e);
					}
					while(rs.next()){
						//Retrieve by column name
						int itemID = rs.getInt("itemid");
						itemName = rs.getString("itemname");
						int itemQuantity = rs.getInt("quantity");
						double itemCost = rs.getDouble("itemcost");
						System.out.println(itemID+" "+ itemName+" "+itemQuantity+" "+itemCost);
						Item item = new Item( itemID, itemName,itemQuantity, itemCost);
						result.add(item);
		        
					}      
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

		
		return result;
	}
	public Item(int itemID, String itemName, int itemQuantity,  double itemCost) {
		this.itemID = itemID;
		this.itemName = itemName;
		this.itemQuantity = itemQuantity;
		this.itemCost = itemCost;
		
		
	}
}
