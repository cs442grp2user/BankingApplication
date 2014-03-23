package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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
		cart.addItemToCart(item, quantity);
	}

	public void removeItemFromCart(Item item) {
		cart.removeItemFromCart(item);
	}

	public List<Item> viewCart() {
		return cart.getAllItemsInCart();
	}

	public boolean transfer(Account fromAccount, Account toAccount,
			double amount) {
		Transaction transaction = Transaction.makeTranction(this,
				new AccountChoice(fromAccount, amount), toAccount);
		return transaction != null; // returns true if transfer successfull
	}

	public boolean order(List<AccountChoice> fromAccountChoices) {
		Order order = Transaction.makeTransaction(this, fromAccountChoices,
				this.cart);
		return order != null; // returns true if order successfull
	}

	private static String customerLogin = "SELECT customerID, branchID FROM Customer NATURAL JOIN Authentication WHERE userName = ? AND password = md5(?);";

}
