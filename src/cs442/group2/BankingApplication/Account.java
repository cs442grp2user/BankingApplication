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
	
	//Needs to be changed to protected	
	protected Account(int accountID, int customerID, String accountType, int pin) {
		this.accountID = accountID;
		this.customerID = customerID;
		this.accountType = accountType;
		this.pin = pin;
	}

	public static Account getAccount(int accountID) {
		Account account = new Account(1000, 100, "Checkings", 888);
		// Add DB code.
		
		Connection conn = BankConnect.getConnection();
		if(conn!=null)	
			System.out.println("Connection established successfully");
			
		try {
			PreparedStatement statement = conn.prepareStatement(accID);
			statement.setInt(1, accountID);
			ResultSet rs = statement.executeQuery();
			
			//System.out.println(rs.findColumn("accounttypeid"));
			
			while(rs.next()){
				int accType = rs.getInt("accounttypeid");
				System.out.println("Value from result is "+accType);
				
			}
			
		} catch (SQLException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			Reporting.err.println(e);
		}
 		
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
	
	/**
	 * Method to get all transactions for a particular AccountID of a Customer 
	 * @return
	 */
	public List<Transaction> accountHistory() {

		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		// Add DB Code
		//System.out.println("Connection successful :"+conn);
		
		Connection conn = BankConnect.getConnection();
		String sql = "SELECT * FROM customertransaction WHERE customerid = ? AND (fromaccountid = ? OR toaccountid = ?);";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, this.getCustomerID());
			statement.setInt(2, this.getAccountID());
			statement.setInt(3, this.getAccountID());
			ResultSet rs = statement.executeQuery();
			
			while(rs.next()){
				rs.getInt("customerid");
				rs.getInt("transactionid");
				rs.getFloat("amount");
				
				//We need to change visibility of Transaction constructor to public/protected
				Transaction tr = new Transaction(rs.getInt("transactionid"), rs.getInt("customerid"),
						rs.getInt("fromaccountid"), rs.getInt("toaccountid"), rs.getDouble("amount"), rs.getDate("timestamp"));
				transactions.add(tr);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Reporting.err.println(e);
		}
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
	private static String accID = "SELECT * FROM Account WHERE accountID = ?;";

	/**
	 * @return the customerID
	 */
	public int getCustomerID() {
		return customerID;
	}

	/**
	 * @param customerID the customerID to set
	 */
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return the customerAccounts
	 */
	public static String getCustomerAccounts() {
		return customerAccounts;
	}

	/**
	 * @param customerAccounts the customerAccounts to set
	 */
	public static void setCustomerAccounts(String customerAccounts) {
		Account.customerAccounts = customerAccounts;
	}

	/**
	 * @return the accountBalance
	 */
	public static String getAccountBalance() {
		return accountBalance;
	}

	/**
	 * @param accountBalance the accountBalance to set
	 */
	public static void setAccountBalance(String accountBalance) {
		Account.accountBalance = accountBalance;
	}

	/**
	 * @param accountID the accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	/**
	 * @param pin the pin to set
	 */
	public void setPin(int pin) {
		this.pin = pin;
	}
	
	public static void main(String[] args)
	{
		Account.getAccount(1);	
		
		/*List<Account> accList = Account.getAllAccounts(1);
		for(Account acc:accList){
			System.out.println(acc.getAccountID());
			System.out.println(acc.getAccountType());
			System.out.println(acc.getBalance());
			
		}*/
		
		//How is the Account type as String type here
		Account acc = new Account(21, 3, "Checkings", 8888);
		
		/*List<Transaction> op_list = acc.accountHistory();
		for(Transaction tr:op_list){
			System.out.println("Transaction ID is: "+tr.getTransactionID());
			System.out.println("Transaction Id for Customer: "+tr.getCustomerID());
			System.out.println("Transaction made from accountID: "+tr.getFromAccountID());
			System.out.println("Transaction made to accountID: "+tr.getToAccountID());
			
			System.out.println("------------Next Transaction--------------");
		}*/
		
		
	}
}
