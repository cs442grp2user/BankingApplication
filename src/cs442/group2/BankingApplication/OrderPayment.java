package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderPayment {
	private int orderID;
	private static int accountID; // Where payment was made to Portal user
	private int transactionID;
	private double amountPaid;

	public static void main(String [ ] args)
	{

	      //getOrderPayments(1,3);
	      makeOrderPayments(1,2,6,10);
	}

	public OrderPayment(int orderID, int accountID, int transactionID,
			double amountPaid) {
		this.orderID = orderID;
		this.accountID = accountID;
		this.transactionID = transactionID;
		this.amountPaid = amountPaid;
	}

	public int getOrderID() {
		return orderID;
	}

	public int getAccountID() {
		return accountID;
	}

	public int getTransactionID() {
		return transactionID;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public static List<OrderPayment> makeOrderPayments(int customerID,int orderID,int transactionID,float amountPaid) {

		ArrayList<OrderPayment> paymentOptions = new ArrayList<OrderPayment>();
		String SQLInsertItem = "INSERT INTO OrderPayment VALUES (?,?,?) ";
		String SearchTableSQL2 = "SELECT * FROM OrderPayment WHERE orderid = ?";
		try{
			Connection conn = BankConnect.getConnection();
			conn.setAutoCommit(true);
			try{
				if(conn != null){
					PreparedStatement statement = conn.prepareStatement(SQLInsertItem);
					statement.setInt(1, orderID);
					statement.setInt(2, transactionID);
					statement.setFloat(3, amountPaid);
					try{
						statement.executeUpdate();
					} catch (SQLException e){
						System.out.println(e);
					}

					PreparedStatement statement2 = conn.prepareStatement(SearchTableSQL2);
					statement2.setInt(1, orderID);
					ResultSet rs=statement2.executeQuery();

					conn.commit();
					while(rs.next()){
						//Retrieve by column name
						orderID = rs.getInt("orderid");
						 transactionID = rs.getInt("transactionid");
						double amountpaid = rs.getDouble("amountpaid");
						System.out.println(orderID+" "+ transactionID+" "+amountpaid);
						OrderPayment op = new OrderPayment( orderID, accountID,transactionID, amountpaid);
						paymentOptions.add(op);
					}      
				}
			}
			catch(SQLException e){
				Reporting.err.println(e);
				conn.rollback();
			}
			finally{
				//conn.commit();
			}   
		}
		catch(SQLException e){
			Reporting.err.println(e);
		}

		return paymentOptions;
	}






	public static List<OrderPayment> getOrderPayments(int orderID,
			int customerID) {
		ArrayList<OrderPayment> paymentOptions = new ArrayList<OrderPayment>();

		// Add DB Code
		String SQLSearchItem = "Select * FROM orderpayment WHERE orderid = ? ";
		try{
			Connection conn = BankConnect.getConnection();
			try{
				if(conn != null){
					PreparedStatement statement = conn.prepareStatement(SQLSearchItem);
					statement.setInt(1, orderID);
					ResultSet rs =  statement.executeQuery();
					while(rs.next()){
						//Retrieve by column name
						orderID = rs.getInt("orderid");
						int transactionID = rs.getInt("transactionid");
						double amountpaid = rs.getDouble("amountpaid");
						System.out.println(orderID+" "+ transactionID+" "+amountpaid);
						OrderPayment op = new OrderPayment( orderID, accountID,transactionID, amountpaid);
						paymentOptions.add(op);
					}      
				}
			}
			catch(SQLException e){
				Reporting.err.println(e);
				conn.rollback();
			}
			finally{
				conn.close();
			}   
		}
		catch(SQLException e){
			Reporting.err.println(e);
		}

		return paymentOptions;

	}
}
