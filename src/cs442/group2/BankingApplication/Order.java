package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Prasad Nair and Akshay
 * 
 * Order Class keeps a record of orders of a customer.
 * A customer can have multiple payment methods for a single order.
 * 
 */

public class Order {
	private int orderID;
	private int customerID;
	private Date dateTime;
	private List<OrderPayment> paymentOptions;
	private double totalAmount;
	
	public int getOrderID() {
		return orderID;
	}

	public int getCustomerID() {
		return customerID;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public List<OrderPayment> getPaymentOptions() {
		return paymentOptions;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	// Constructor for Order class
	public Order(int orderID, int customerID, Date dateTime,
		List<OrderPayment> paymentOptions, double totalAmount) {
		this.orderID = orderID;
		this.customerID = customerID;
		this.dateTime = dateTime;
		this.paymentOptions = paymentOptions;
		this.totalAmount = totalAmount;
	}
	
	// Returns all the orders of a customer
	public static List<Order> getAllOrders(int customerID){
		ArrayList<Order> orders = new ArrayList<Order>();
		//ArrayList<double> orders = new ArrayList<Order>();
		String SQLShoppingOrderSelect = "Select orderid, timestamp  FROM shoppingorder WHERE customerid = ?;";
		String SQLOrderPaymentSelect = "Select sum(amountpaid) As orderamount from orderpayment group by orderid having orderid = ?;";
		try{
			Connection conn = BankConnect.getConnection();
			try{
			
				if(conn != null){
				
					PreparedStatement statement = conn.prepareStatement(SQLShoppingOrderSelect);
					statement.setInt(1, customerID);
					ResultSet rs =  statement.executeQuery();
					while(rs.next()){
				         //Retrieve by column name
				         int orderID = rs.getInt("orderid");
				         Date orderTime = rs.getDate("timestamp");
				         List<OrderPayment> orderPayment = OrderPayment.getOrderPayments(orderID, customerID);
				         
				         // Get amount for that order
				         statement = conn.prepareStatement(SQLOrderPaymentSelect);
				         statement.setInt(1, orderID);
				         ResultSet rsForOrderPayment =  statement.executeQuery();
				         double orderamount = rsForOrderPayment.getDouble("orderamount");
	
				         Order order = new Order(orderID,customerID,orderTime, orderPayment, orderamount);
				         orders.add(order);
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


		
		return orders;
	}
	
}
