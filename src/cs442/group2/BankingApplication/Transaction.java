package cs442.group2.BankingApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cs442.group2.BankingApplication.exceptions.InsufficientBalanceException;
import cs442.group2.BankingApplication.helpers.AccountChoice;


@SuppressWarnings("unused")
public class Transaction {
	private int transactionID;
	private int customerID;
	private int fromAccountID;
	private int toAccountID;
	private double amount;
	private Date timestamp;

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

		// Note: you can check if two accounts are same by just doing, account1 == account2
		// where account1 & account 2 are objects of type Account. This is possible because comparesTo function is overridden in Account class.

		// Add DB Code.

		// Create orderpayments and add to this
		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>(1);

		// Re read from the database the timestamp for the order made and use
		// the date from that timestamp and finally create this order object

		// Add DB Code.
		Order order = new Order(1, 1, new Date(), orderPayments, 0.0);
		return order; // You can return null from this function if order fails
	}

	public static Transaction makeTranction(Customer customer,
			AccountChoice fromAccountChoice, Account toAccount) {
		// This will contain only 1
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		// Add DB Code.
		return null; // You can return null from this function if transfer fails
	}

	private static boolean checkTransaction(Customer customer,
			AccountChoice accountChoice) throws InsufficientBalanceException{

		// We have introduced these problems which we need to handle

		// 1. Make sure if payment account is a rewards account, it should be of
		// the customer only.
		// i.e. a person cannot add some other customer's reward account as a
		// payment option

		return true;
	}

	private Transaction(int transactionID, int customerID, int fromAccountID,
			int toAccountID, double amount, Date timestamp) {
		this.transactionID = transactionID;
		this.customerID = customerID;
		this.fromAccountID = fromAccountID;
		this.toAccountID = toAccountID;
		this.amount = amount;
		this.timestamp = timestamp;
	}

}
