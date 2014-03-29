package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "unused" })
public class OrderPayment {

	private int orderID;
	private int accountID; // Where payment was made to Portal user
	private int transactionID;
	private double amountPaid;
	
	@Override
	public String toString() {
		return String.format("OrderPayment(for orderID=%d) paid %4.2f from (accountID=%d) using (transactionID=%d)", orderID, amountPaid, accountID, transactionID);
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

	public static List<OrderPayment> getOrderPayments(int orderID,
			int customerID) {
		ArrayList<OrderPayment> paymentOptions = new ArrayList<OrderPayment>();
		try {
			Connection conn = BankConnect.getConnection();
			PreparedStatement statement = conn
					.prepareStatement(sqlSelectOrderPayment);
			statement.setInt(1, customerID);
			statement.setInt(2, orderID);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {

				int accountID = rs.getInt("fromAccountID");
				int transactionID = rs.getInt("transactionID");
				double amountPaid = rs.getDouble("amountPaid");

				OrderPayment payment = new OrderPayment(orderID, accountID,
						transactionID, amountPaid);
				paymentOptions.add(payment);
			}
		} catch (Exception e) {
			Reporting.err.println(e);
		}

		return paymentOptions;

	}

	private static String sqlSelectOrderPayment = "SELECT * FROM ShoppingOrder NATURAL JOIN OrderPayment NATURAL JOIN CustomerTransaction WHERE customerID = ? AND orderID = ?;";
}
