package cs442.group2.BankingApplication.testing;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import cs442.group2.BankingApplication.BankConnect;


public class TestTransaction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Connection conn = BankConnect.getConnection();
			conn.setAutoCommit(false);
			String keys[] = { "itemID" };
			PreparedStatement statement = conn.prepareStatement("INSERT INTO Item(itemName, quantity, itemCost) VALUES(?,?,?)", keys);
			statement.setString(1, "dummy 3");
			statement.setInt(2, 4);
			statement.setDouble(3, 20.50);
			statement.executeUpdate();
			
			ResultSet rs = statement.getGeneratedKeys();
			while(rs.next()){
				System.out.println(rs.getInt(1));
			}
			
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}