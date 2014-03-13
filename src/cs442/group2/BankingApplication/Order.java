package cs442.group2.BankingApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	public Order(int orderID, int customerID, Date dateTime,
			List<OrderPayment> paymentOptions, double totalAmount) {
		this.orderID = orderID;
		this.customerID = customerID;
		this.dateTime = dateTime;
		this.paymentOptions = paymentOptions;
		this.totalAmount = totalAmount;
	}
	
	public static List<Order> getAllOrders(int customerID){
		ArrayList<Order> orders = new ArrayList<Order>();
		// Add DB code
		return orders;
	}
	
}
