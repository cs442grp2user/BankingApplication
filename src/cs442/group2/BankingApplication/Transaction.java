package cs442.group2.BankingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cs442.group2.BankingApplication.exceptions.InsufficientBalanceException;
import cs442.group2.BankingApplication.exceptions.InvalidCustomerException;
import cs442.group2.BankingApplication.exceptions.RewardsCustomerException;
import cs442.group2.BankingApplication.helpers.AccountChoice;

@SuppressWarnings("unused")
public class Transaction {
	
	private static String sqlEmptyUserCart = "DELETE FROM Cart WHERE customerID = ?;";
	private int transactionID;
	private int customerID;
	private int fromAccountID;
	private int toAccountID;
	private double amount;
	private Date timestamp;

	@Override
	public String toString() {
		String s = "";
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		s = format.format(timestamp);
		return String.format("Transaction(id=%d) From(id=%d) To(id=%d) With Amount(%4.2f) [%s]", transactionID, fromAccountID, toAccountID, amount, s);
	}
	private static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}

	public static Order makeTransaction(Customer customer,
			List<AccountChoice> fromAccountChoices, Cart cart) {
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		// fromAccountChoices contains list of AccountChoice objects where each
		// object will give you account and the amount you want to deduct from
		// that account

		// These are the problems that got introduced by our design

		// 1. Make sure if there are two accounts that are same, then its
		// replaced by 1 account with the sum of the amounts. Otherwise, what
		// would happen is say there are two choices, deduct 100 from account a1
		// & deduct 100 from account a2. And if account balance is 150 then
		// checkTransaction will be true for the choices(obviously) but the
		// insert will fail

		// Note: you can check if two accounts are same by just doing, account1
		// == account2
		// where account1 & account 2 are objects of type Account. This is
		// possible because comparesTo function is overridden in Account class.

		

		// Create orderpayments and add to this
		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>(1);

		

		Order order = null;

		boolean error = false;

		if (customer.getCustomerID() == 1)
			error &= true;

		List<Item> items = cart.getAllItems();

		double totalFromCart = cart.getItemTotalCost();
		
		// Remove duplicate accounts
		HashMap<Integer, AccountChoice> choices = new HashMap<Integer, AccountChoice>();
		for (AccountChoice choice : fromAccountChoices) {

			if (choices.get(choice.getAccount().getAccountID()) == null) {
				choices.put(choice.getAccount().getAccountID(), choice);

			} else {
				AccountChoice existingchoice = choices.get(choice.getAccount()
						.getAccountID());
				existingchoice.setDeductAmount(existingchoice.getDeductAmount()
						+ choice.getDeductAmount());
				choices.put(choice.getAccount().getAccountID(), existingchoice);
			}

		}
		// Duplicates removed
		
		double totalFromAccounts = 0.0d;
		for (AccountChoice choice : choices.values()) {
			totalFromAccounts += choice.getDeductAmount();
		}

		if (totalFromAccounts != totalFromCart)
			error &= true;

		boolean transactionOK;
		for (AccountChoice choice : choices.values()) {
			try {
				transactionOK = Transaction.checkTransaction(customer, choice);
				if (transactionOK == false) {
					error &= true;
					break;
				}
			} catch (Exception e) {
				Reporting.err.println(e);
				error &= true;
			}
		}

		if (!error) {
			// Proceed with the order

			try {
				Connection conn = BankConnect.getConnection();

				// STEP 1: Make a transaction in customertransaction where toAccount
				// is Portal account
				Account portalAcc = Account.getAccount(1); // In table Account, 1 is always for Portal user's account
				ArrayList<Transaction> transactionlist = new ArrayList<Transaction>();
				int autogenerateOrderID = 0;

				for (AccountChoice choice : choices.values()) {
					// For each unique account chosen by customer for his payment
					// make a transaction in customertransaction table
					Transaction transaction = Transaction.makeTranction(customer,
							choice, portalAcc);

					transactionlist.add(transaction);
				}

				// STEP 2: Create an Order object in shoppingorder table
				String SQLShoppingorderInsert = "INSERT INTO shoppingorder (customerid,orderpayment) VALUES (?,?);";
				String autokey[] = { "orderid" };
				try {
					PreparedStatement statement = conn.prepareStatement(
							SQLShoppingorderInsert, autokey);
					statement.setInt(1, customer.getCustomerID());
					statement.setDouble(2, totalFromAccounts);
					
					statement.executeUpdate();

					ResultSet rs = statement.getGeneratedKeys();

					if (rs.next()) {
						autogenerateOrderID = rs.getInt(1);
						
					}
					

					// STEP 3: Make rows in orderpayment table
					String SQLOrderpaymentInsert = "INSERT INTO orderpayment (orderid,transactionid,amountpaid) VALUES (?,?,?);";
					for (Transaction transaction : transactionlist) {
						PreparedStatement stmt = conn
								.prepareStatement(SQLOrderpaymentInsert);
						stmt.setInt(1, autogenerateOrderID);
						stmt.setInt(2, transaction.getTransactionID());
						stmt.setDouble(3, transaction.getAmount());
						stmt.executeUpdate();

						OrderPayment orderpaymt = new OrderPayment(
								autogenerateOrderID,
								transaction.getFromAccountID(),
								transaction.getTransactionID(),
								transaction.getAmount());
						orderPayments.add(orderpaymt);

					}

					// STEP 4: Insert record in orderdetails table
					String SQLOrderdetailsInsert = "INSERT INTO orderdetails (orderid,itemid,quantity) VALUES (?,?,?);";
					for (Item item : items) {
						PreparedStatement stmt = conn
								.prepareStatement(SQLOrderdetailsInsert);
						stmt.setInt(1, autogenerateOrderID);
						stmt.setInt(2, item.getItemID());
						stmt.setInt(3, cart.getQuantityOfItem(item));
						stmt.executeUpdate();
					}
					
					// STEP 5: Delete all items in Cart table for the customer
					statement = conn.prepareStatement(sqlEmptyUserCart);
					statement.setInt(1, customer.getCustomerID());
					statement.executeUpdate();
					
					// STEP 6: Update quantity of Items purchased by customer.
					
					for (Item item : cart.getAllItems()) {
						Item.setQuantity(item.getItemID(), cart.getQuantityOfItem(item));
					}
					
					conn.commit(); // Save everything in every table involved above and makeTransaction for transfering amount
					
					order = new Order(autogenerateOrderID, customer.getCustomerID(),
							new Date(), orderPayments, totalFromAccounts);

				} catch (SQLException e) {
					Reporting.err.println(e);
					conn.rollback();				
					
				}

			} catch (Exception e) {
				Reporting.err.println(e);
			}
			
			
		}

		return order; // You can return null from this function if order fails
	}

	public static Transaction makeTranction(Customer customer,
			AccountChoice fromAccountChoice, Account toAccount) throws Exception {
		Transaction transaction = null;
		// This will contain only 1
		// ArrayList<Transaction> transactions = new ArrayList<Transaction>();

		// STEP 1: Check whether the transaction is possible i.e. whether the
		// 'From'
		// account of the customer has enough balance to make a successful
		// transfer
		boolean isValid = checkTransaction(customer, fromAccountChoice);

		// STEP 2: Deduct money from Sender's account, add it to recepient's
		// account and then record the
		// transaction in the CustomerTransaction table in the database.
		if (isValid = true) {

			Connection conn = BankConnect.getConnection();

			int customerID = customer.getCustomerID();

			Account account = fromAccountChoice.getAccount();
			int fromAccountID = account.getAccountID();
			int toRecepientAccountID = toAccount.getAccountID();
			double deductionAmount = fromAccountChoice.getDeductAmount();

			String SQLAccountUpdateFromAccount = "UPDATE account SET balance= balance - ?  WHERE accountid = ? and customerid = ?;";
			String SQLAccountUpdateToAccount = "UPDATE account SET balance= balance + ?  WHERE accountid = ? and customerid = ?;";
			String SQLTransactionInsert = "INSERT INTO customertransaction (customerid,fromaccountid,toaccountid,amount) VALUES (?,?,?,?);";
			String SQLTransactionSelect = "SELECT transactionid, customerid,fromaccountid, toaccountid, amount,timestamp FROM customertransaction WHERE transactionid = ? ;";

			try {
				try {

					if (conn != null) {

						// Deducts money from the sender's account in Account
						// table
						PreparedStatement statement = conn
								.prepareStatement(SQLAccountUpdateFromAccount);

						statement.setDouble(1, deductionAmount);
						statement.setInt(2, fromAccountID);
						statement.setInt(3, customerID);
						statement.executeUpdate();
						
						// TODO: Rewards for customer with fromAccount if its a credit account

						// Adds money to the recepient's account in Account
						// table
						statement = conn
								.prepareStatement(SQLAccountUpdateToAccount);

						statement.setDouble(1, deductionAmount);
						statement.setInt(2, toRecepientAccountID);
						statement.setInt(3, customerID);
						statement.executeUpdate();

						// Creates a transaction in the CustomerTransaction
						// table

						String key[] = { "transactionid" };
						statement = conn.prepareStatement(SQLTransactionInsert,
								key);

						statement.setInt(1, customerID);
						statement.setInt(2, fromAccountID);
						statement.setInt(3, toRecepientAccountID);
						statement.setDouble(4, deductionAmount);
						statement.executeUpdate();
						

						ResultSet rs = statement.getGeneratedKeys();
						int generatedTransactionID = 0;
						// To get the database auto-generated transactionid of
						// the transaction just inserted
						if (rs.next()) {
							generatedTransactionID = rs.getInt(1);
							
						}
						

						// To create the transaction object that would be
						// returned to the calling class.

						statement = conn.prepareStatement(SQLTransactionSelect);

						statement.setInt(1, generatedTransactionID);
						rs = statement.executeQuery();

						if (rs.next()) {
							int transactionid = rs.getInt("transactionid");
							int customerid = rs.getInt("customerid");
							int fromaccountid = rs.getInt("fromaccountid");
							int toaccountid = rs.getInt("toaccountid");
							Double amount = rs.getDouble("amount");
							Date timestamp = rs.getDate("timestamp");

							transaction = new Transaction(transactionid,
									customerid, fromaccountid, toaccountid,
									amount, timestamp);
						}
					}
				} catch (SQLException e) {
					throw e;
				}

				
			} catch (Exception e) {
				throw e;
			}

			// RETURNS the transaction object to the caller
			return transaction;

		} else {
			return null; // IF transaction isValid = False
		}

	}

	public static boolean checkTransaction(Customer customer,
			AccountChoice accountChoice) throws InsufficientBalanceException {

		// We have introduced these problems which we need to handle

		// 1. Make sure if payment account is a rewards account, it should be of
		// the customer only.
		// i.e. a person cannot add some other customer's reward account as a
		// payment option
		if (customer.getCustomerID() == 1
				|| accountChoice.getAccount().getAccountID() == 1) // Account OR
																	// Customer
																	// belong to
																	// the
																	// Portal
			throw new InvalidCustomerException(customer);

		Account select_acc = accountChoice.getAccount();
		if (select_acc.getAccountType().equals("Rewards")
				&& select_acc.getCustomerID() != customer.getCustomerID()) {
			// Rewards account does not belong to current logged in customer
			throw new RewardsCustomerException(select_acc, customer);
		} else {

			double availbalance = accountChoice.getAccount().getBalance();
			if (availbalance < accountChoice.getDeductAmount()) {
				
				throw new InsufficientBalanceException(
						accountChoice.getAccount(),
						accountChoice.getDeductAmount());
			} else
				return true;

		}

	}

	Transaction(int transactionID, int customerID, int fromAccountID,
			int toAccountID, double amount, Date timestamp) {
		this.transactionID = transactionID;
		this.customerID = customerID;
		this.fromAccountID = fromAccountID;
		this.toAccountID = toAccountID;
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public int getTransactionID() {
		return transactionID;
	}

	
	public int getCustomerID() {
		return customerID;
	}

	
	public int getFromAccountID() {
		return fromAccountID;
	}

	
	public int getToAccountID() {
		return toAccountID;
	}

	public double getAmount() {
		return amount;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	
	public static void main(String[] args) {
		Customer cust = Customer.authenticate("mgadge2", "password");

		Account acc = new Account(21, 3, "Rewards", 0);
		AccountChoice acc_choice = new AccountChoice(acc, 250);

		if (Transaction.checkTransaction(cust, acc_choice)){
		
		}

		// Testing for place an order

		Item item1 = new Item(6, "A Clash of Kings(A Song of Ice and Fire #2)",
				1000, 89.45);
		Item item2 = new Item(5,
				"A Game of Thrones(A Song of Ice and Fire #1)", 1000, 89.45);
		Item item3 = new Item(2, "The Two Towers(The Lord of the Rings #2)",
				1000, 22.43);

		ArrayList<Account> listAcc = new ArrayList<Account>();
		Account acc1 = new Account(2, 2, "Checkings", 8888);
		Account acc2 = new Account(8, 2, "Savings", 8888);
		Account acc3 = new Account(14, 2, "Credit", 0);
		listAcc.add(acc1);
		listAcc.add(acc2);
		listAcc.add(acc3);

		Cart cart = new Cart(2);
		Customer cust2 = Customer.authenticate("akanch4", "password");
		cust2.addItemToCart(item1, 2);
		cust2.addItemToCart(item2, 1);
		cust2.addItemToCart(item3, 2);

		// Choose option for payment
		ArrayList<AccountChoice> accChoices = new ArrayList<AccountChoice>();
		AccountChoice choice1 = new AccountChoice(acc1, 200.00);
		AccountChoice choice2 = new AccountChoice(acc3, 113.21);
		accChoices.add(choice1);
		accChoices.add(choice2);

		boolean orderstatus = cust2.order(accChoices);
		

	}

}
