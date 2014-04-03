package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cs442.group2.BankingApplication.helpers.AccountChoice;

public class Customer {

	private int customerID;
	private int branchID;
	private String userName;
	private List<Account> accounts;

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

	private Cart cart;
	
	/**
	 * This constructor was just created for test purpose
	 * @param customerID
	 * @param branchID
	 * @param userName
	 */
	public Customer(int customerID, int branchID, String userName){
		
		this.customerID = customerID;
		this.branchID = branchID;
		this.userName = userName;		
	}
	
	public Customer(int customerID, int branchID, String userName,
			List<Account> accounts, Cart cart) {
		this.customerID = customerID;
		this.branchID = branchID;
		this.userName = userName;
		this.accounts = accounts;
		this.cart = cart;
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

	public void addItemToCart(Item item, int quantity) {
		cart.addItem(item, quantity);
	}

	public void removeItemFromCart(Item item) {
		cart.removeItem(item);
	}

	public List<Item> viewCart() {
		return cart.getAllItems();
	}

	public boolean transfer(Account fromAccount, Account toAccount,
			double amount) {
		Transaction transaction = Transaction.makeTranction(this,
				new AccountChoice(fromAccount, amount), toAccount);
		return transaction != null; // returns true if transfer successful
	}

	public boolean order(List<AccountChoice> fromAccountChoices) {
		Order order = Transaction.makeTransaction(this, fromAccountChoices,
				this.cart);
		System.out.println("New Order generated is :"+order.getOrderID()+ " For Customer: "+this.getUserName());
		return order != null; // returns true if order successful
	}
	
	public List<Item> searchItem(String productName, String category){
		ArrayList<Item> searchProdList = new ArrayList<Item>();
		
		return searchProdList;
	}
	
	public List<Order> orderHistory() {
		ArrayList<Order> orders = new ArrayList<Order>();
		HashMap<Integer, Order> orders_map = new HashMap<Integer, Order>();
		
		// Add DB Code
		Connection conn = BankConnect.getConnection();
		String select1 = "SELECT SO.orderid, SO.orderpayment, SO.timestamp,SO.customerid, OP.transactionid, OP.amountpaid "+ 
						 "FROM shoppingorder SO, orderpayment OP "+
						 "WHERE SO.orderid = OP.orderid AND SO.customerid = ?;";
		
		try {
			PreparedStatement statement = conn.prepareStatement(select1);
			statement.setInt(1, this.getCustomerID());
			
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				int orderid = rs.getInt("orderid");
				int custid = rs.getInt("customerid");
				double amtPaidinTransac = rs.getDouble("amountpaid");
				int transactionid = rs.getInt("transactionid");
				
				//Check whether the current order has been already accounted for
				if(orders_map.containsKey(orderid)) {
					OrderPayment op = new OrderPayment(orderid, 1, transactionid, amtPaidinTransac);
					orders_map.get(orderid).getPaymentOptions().add(op);
					
					for(Order ord : orders) {
						if(ord.getOrderID() == orderid) {
							ord.getPaymentOptions().add(op);
							break;
						}
						
					}
				}
				//This is a new Order instance not yet put inside the ArrayList
				else {
					ArrayList<OrderPayment> paymentOptions = new ArrayList<OrderPayment>();
					OrderPayment op = new OrderPayment(orderid, custid, transactionid, amtPaidinTransac);
					paymentOptions.add(op);
					
					Order ord = new Order(orderid, custid, rs.getDate("timestamp"), paymentOptions, rs.getDouble("orderpayment"));
					orders.add(ord);
					orders_map.put(orderid, ord);
				}
				
			}
		} 
		catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		return orders;
	}


	private static String customerLogin = "SELECT customerID, branchID FROM Customer NATURAL JOIN Authentication WHERE userName = ? AND password = md5(?);";

}
