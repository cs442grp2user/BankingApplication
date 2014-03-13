package cs442.group2.BankingApplication;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Account implements Comparable<Account> {
	private int accountID;
	private int customerID;
	private String accountType;
	private int pin;

	@Override
	public String toString() {
		String str = String.format("{(%d) : %s : %f\n", accountID, accountType,
				getBalance());
		return str;
	}

	private Account(int accountID, int customerID, String accountType, int pin) {
		this.accountID = accountID;
		this.customerID = customerID;
		this.accountType = accountType;
		this.pin = pin;
	}

	public static Account getAccount(int accountID) {
		Account account = new Account(1000, 100, "Checkings", 888);
		// Add DB code.
		return account; // its OK to return null as account may not exist
	}

	public static List<Account> getAllAccounts(int customerID) {
		ArrayList<Account> accounts = new ArrayList<Account>();

		Connection conn = BankConnect.getConnection();
		try {
			conn.setAutoCommit(false);
			PreparedStatement statement = conn
					.prepareStatement(customerAccounts);
			statement.setInt(1, customerID);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				Account account = new Account(rs.getInt("accountID"),
						customerID, rs.getString("accountTypeName"),
						rs.getInt("pin"));
				accounts.add(account);
			}
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			Reporting.err.println(e);
		} finally {

		}

		return accounts;
	}

	public double getBalance() {
		BigDecimal balance = null;
		try {
			Connection conn = BankConnect.getConnection();
			PreparedStatement statement = conn.prepareStatement(accountBalance);
			statement.setInt(1, accountID);

			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				balance = rs.getBigDecimal("balance");
			}
		} catch (Exception e) {
			Reporting.err.println(e);
		}
		if (balance == null)
			return -1;
		return balance.doubleValue();
	}

	public List<Transaction> accountHistory() {

		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		// Add DB Code
		return transactions;
	}

	public List<Order> orderHistory() {
		ArrayList<Order> orders = new ArrayList<Order>();
		// Add DB Code
		return orders;
	}

	public int getAccountID() {
		return accountID;
	}

	@Override
	public int compareTo(Account o) {
		// Checks if two accounts are identical, do not infer any conclusions
		// when this program returns 1 or -1

		// Useful in Transaction class
		if (this.accountID < o.accountID)
			return -1;
		if (this.accountID > o.accountID)
			return 1;
		if (this.accountID == o.accountID && this.customerID == o.customerID)
			return 0;
		return 1;
	}

	public int getPin() {
		return pin;
	}

	private static String customerAccounts = "SELECT accountID, accountTypeName, pin FROM Customer NATURAL JOIN Account NATURAL JOIN AccountType WHERE customerID = ?; ";
	private static String accountBalance = "SELECT balance FROM Account WHERE accountID = ?;";

}
