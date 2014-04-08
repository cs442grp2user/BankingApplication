package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cs442.group2.BankingApplication.helpers.AccountChoice;

public class Customer {

	private int customerID;
	private int branchID;
	private String userName;
	private List<Account> accounts;
	private Cart cart;

	@Override
	public String toString() {
		String str = "[" + userName + "(" + customerID + ")] Accounts: (\n";
		str += Arrays.toString(accounts.toArray(new Account[0]));
		str += ")";
		return str;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	private Customer(int customerID, int branchID, String userName,
			List<Account> accounts, Cart cart) {
		this.customerID = customerID;
		this.branchID = branchID;
		this.userName = userName;
		this.accounts = accounts;
		this.cart = cart;
	}

	public static synchronized Customer authenticate(String userName,
			String password) {
		Customer customer = null;

		Connection conn = BankConnect.getConnection();
		try {
			PreparedStatement statement = conn.prepareStatement(customerLogin);
			statement.setString(1, userName);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				ArrayList<Account> accounts = new ArrayList<Account>();

				int customerID = rs.getInt(1);
				int branchID = rs.getInt(2);

				for (Account account : Account.getAllAccounts(customerID)) {
					accounts.add(account);
				}

				Cart cart = new Cart(customerID);
				customer = new Customer(customerID, branchID, userName,
						accounts, cart);
			}

		} catch (Exception e) {
			Reporting.err.println(e);
		}

		return customer;
	}

	public boolean addItemToCart(Item item, int quantity) {
		try {
			cart.addItem(item, quantity);
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	public void removeItemFromCart(Item item) {
		cart.removeItem(item);
	}

	public List<Item> viewCart() {
		return cart.getAllItems();
	}

	public boolean transfer(Account fromAccount, Account toAccount,
			double amount) {
		Transaction transaction = null;
		try {
			try {
				transaction = Transaction.makeTranction(this,
						new AccountChoice(fromAccount, amount), toAccount);
			} catch (Exception e) {
				BankConnect.getConnection().rollback();
				Reporting.err.println(e);
			}

			if (transaction != null) {

				BankConnect.getConnection().commit();

			}
		} catch (Exception e) {
			Reporting.err.println(e);
		}

		return transaction != null; // returns true if transfer successful
	}

	public boolean order(List<AccountChoice> fromAccountChoices) {
		Order order = Transaction.makeTransaction(this, fromAccountChoices,
				this.cart);
		Reporting.out.println("New Order generated is :" + order.getOrderID()
				+ " For Customer: " + this.getUserName());
		return order != null; // returns true if order successful
	}

	public List<Item> searchItem(String productName, String category) {
		ArrayList<Item> searchProdList = new ArrayList<Item>();

		return searchProdList;
	}

	public List<Order> orderHistory() {
		ArrayList<Order> orders = new ArrayList<Order>();
		HashMap<Integer, ArrayList<OrderPayment>> payments = new HashMap<Integer, ArrayList<OrderPayment>>();
		HashMap<Integer, Date> orderIDtoShoppingTimeStamp = new HashMap<Integer, Date>();
		HashMap<Integer, Double> orderIDtoTotalPayment = new HashMap<Integer, Double>();

		
		String sqlOrderHistory = "SELECT * FROM shoppingorder NATURAL JOIN orderpayment NATURAL JOIN customertransaction  WHERE customerid = ?;";
		
		try {
			Connection conn = BankConnect.getConnection();
			PreparedStatement statement = conn.prepareStatement(sqlOrderHistory);
			statement.setInt(1, customerID);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				
				int orderID = rs.getInt("orderid");
				int fromAccountID = rs.getInt("fromaccountid");
				
				int transactionID = rs.getInt("transactionid");
				
				Date dateTime = rs.getDate("shoppingtimestamp");
				orderIDtoShoppingTimeStamp.put(orderID, dateTime);
				
				double totalAmount = rs.getDouble("orderpayment");
				orderIDtoTotalPayment.put(orderID, totalAmount);
				
				double amountPaid = rs.getDouble("amountpaid");
				
				OrderPayment payment = new OrderPayment(orderID, fromAccountID, transactionID, amountPaid);
				if(!payments.containsKey(orderID)){
					ArrayList<OrderPayment> orderPayments = new ArrayList<OrderPayment>();
					orderPayments.add(payment);
					payments.put(orderID, orderPayments);
				} else {
					ArrayList<OrderPayment> orderPayments = payments.get(orderID);
					orderPayments.add(payment);
					payments.put(orderID, orderPayments);
				}
			}
			
			Set<Integer> orderIDs = payments.keySet();
			for(int orderID : orderIDs){
				ArrayList<OrderPayment> orderPayments = payments.get(orderID);
				Order order = new Order(orderID, this.getCustomerID(), orderIDtoShoppingTimeStamp.get(orderID), orderPayments, orderIDtoTotalPayment.get(orderID));
				orders.add(order);
			}
			
		} catch (Exception e) {
			Reporting.err.println(e);
		}
		
		return orders;
	}

	public int getCustomerID() {
		return customerID;
	}

	public int getBranchID() {
		return branchID;
	}

	public String getUserName() {
		return userName;
	}

	public Cart getCart() {
		return cart;
	}

	private static String customerLogin = "SELECT customerID, branchID FROM Customer NATURAL JOIN Authentication WHERE userName = ? AND password = md5(?);";

}
