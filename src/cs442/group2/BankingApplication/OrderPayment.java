package cs442.group2.BankingApplication;

import java.util.ArrayList;
import java.util.List;

public class OrderPayment {
	private int orderID;
	private int accountID; // Where payment was made to Portal user
	private int transactionID;
	private double amountPaid;

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

	public static List<OrderPayment> getOrderPayments(int orderID, int customerID) {
		ArrayList<OrderPayment> paymentOptions = new ArrayList<OrderPayment>();
		
		// Add DB Code
		
		return paymentOptions;

	}
}
