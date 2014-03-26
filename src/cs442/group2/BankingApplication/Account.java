package cs442.group2.BankingApplication;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
		Account account = null;
		// Add DB code.
		
		Connection conn = BankConnect.getConnection();
		if(conn!=null)	
			System.out.println("Connection established successfully");
		
		String accID = "SELECT * FROM Account NATURAL JOIN AccountType WHERE accountID = ?;";
			
		try {
			PreparedStatement statement = conn.prepareStatement(accID);
			statement.setInt(1, accountID);
			ResultSet rs = statement.executeQuery();
			
			//System.out.println(rs.findColumn("accounttypeid"));
			
			while(rs.next()){
				int accType = rs.getInt("accounttypeid");
				System.out.println("Value from result is "+accType);
				
				if(accType == 3) {
					//Its a Credit account
					CreditCard credit = null;
					String selectcredit = "SELECT * FROM Credit NATURAL JOIN Account NATURAL JOIN AccountType WHERE accountID = ?;";
					PreparedStatement stmtcredit = conn.prepareStatement(selectcredit);
					stmtcredit.setInt(1, accountID);
					ResultSet rscr = stmtcredit.executeQuery();
					while(rscr.next()) {
						credit = new CreditCard(rscr.getInt("accountid"), rscr.getInt("customerid"), rscr.getString("accounttypename"), 
									rscr.getInt("pin"), rscr.getString("cardnumber"), rscr.getShort("cvv2"), rscr.getFloat("approvedcredit"));
					}
					return credit;
				}
				else {
					//Its either a Checking, Savings or Rewards account
					
					Account acc = new Account(rs.getInt("accountid"), rs.getInt("customerid"), 
									rs.getString("accounttypename"), rs.getInt("pin"));
					
					return acc;
				}
				
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
	
	/**
	 * Method returns balance for both Parent account class and child Credit class
	 * @return
	 */
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
		
		String sql = "SELECT * FROM customertransaction WHERE fromaccountid = ? OR toaccountid = ?;";		//accommodates for this account involved in different customers transaction 
		try {
			Connection conn = BankConnect.getConnection();
			
			PreparedStatement statement = conn.prepareStatement(sql);
			//statement.setInt(1, this.getCustomerID());
			statement.setInt(1, this.getAccountID());
			statement.setInt(2, this.getAccountID());
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
	
	/**
	 * This method will return a list of all Orders associated with the current account
	 * @return
	 */
	public List<Order> orderHistory() {
		ArrayList<Order> orders = new ArrayList<Order>();
		HashMap<Integer, Order> orders_m = new HashMap<Integer, Order>();
		// Add DB Code
		
		String sqlselect = "SELECT SO.orderid, SO.customerid, SO.timestamp, SO.orderpayment, OP.transactionid, OP.amountpaid from shoppingorder SO, orderpayment OP, customertransaction CT "+
							"WHERE SO.orderid = OP.orderid AND OP.transactionid = CT.transactionid "+
							"AND (CT.fromaccountid = ? OR CT.toaccountid = ?);";
		
		try {
			Connection conn = BankConnect.getConnection();
			PreparedStatement statement = conn.prepareStatement(sqlselect);
			statement.setInt(1, this.getAccountID());
			statement.setInt(2, this.getAccountID());
			ResultSet rs = statement.executeQuery();
			
			while(rs.next()){
				int orderid = rs.getInt("orderid");
				int transactionid = rs.getInt("transactionid");
				double amtPaidinTransac = rs.getDouble("amountpaid");
				
				//Check whether the current order has been already accounted for
				if(orders_m.containsKey(orderid)) {
					OrderPayment op = new OrderPayment(orderid, 1, transactionid, amtPaidinTransac);
					
					orders_m.get(orderid).getPaymentOptions().add(op);
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
					OrderPayment op = new OrderPayment(orderid, 1, transactionid, amtPaidinTransac);
					paymentOptions.add(op);
					
					Order ord = new Order(orderid, rs.getInt("customerid"), rs.getDate("timestamp"), paymentOptions, rs.getDouble("orderpayment"));
					
					//Add this new Order to the HashMap and the ArrayList
					orders_m.put(orderid, ord);
					orders.add(ord);
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return orders;
	}
	
	/*public List<Order> orderHistory() {
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
	}*/

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
	
	private static String customerAccounts = "SELECT accountID, accountTypeName, pin FROM Customer NATURAL JOIN Account NATURAL JOIN AccountType WHERE customerID = ?; ";
	private static String accountBalance = "SELECT balance FROM Account WHERE accountID = ?;";
	
	public static void main(String[] args)
	{
		Account account = Account.getAccount(34);	
		System.out.println("Account ID is: "+account.getAccountID());
		System.out.println("Account type is :"+account.getAccountType());
		System.out.println("Account belongs to customer :"+account.getCustomerID());
		System.out.println("Account PIN is: "+account.getPin());
		
		/*List<Account> accList = Account.getAllAccounts(1);
		for(Account acc:accList){
			System.out.println(acc.getAccountID());
			System.out.println(acc.getAccountType());
			System.out.println(acc.getBalance());
			
		}*/
		
		Account acc = new Account(14, 2, "Rewards", 8888);
		//Account acc = new Account(21, 3, "Rewards",0);
		
		
		/*List<Transaction> op_list = acc.accountHistory();
		for(Transaction tr:op_list){
			System.out.println("Transaction ID is: "+tr.getTransactionID());
			System.out.println("Transaction Id for Customer: "+tr.getCustomerID());
			System.out.println("Transaction made from accountID: "+tr.getFromAccountID());
			System.out.println("Transaction made to accountID: "+tr.getToAccountID());
			
			System.out.println("------------Next Transaction--------------");
		}
		*/
		
		List<Order> resultList = acc.orderHistory();
		for(Order order : resultList) {
			
			System.out.println("Order involved with Account:"+acc.getAccountID() + " is:"+order.getOrderID() );
		}
				
	}
}
