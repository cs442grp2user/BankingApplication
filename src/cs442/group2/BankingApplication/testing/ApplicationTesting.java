package cs442.group2.BankingApplication.testing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cs442.group2.BankingApplication.Account;
import cs442.group2.BankingApplication.BankConnect;
import cs442.group2.BankingApplication.Cart;
import cs442.group2.BankingApplication.Customer;
import cs442.group2.BankingApplication.Item;
import cs442.group2.BankingApplication.Order;
import cs442.group2.BankingApplication.Reporting;
import cs442.group2.BankingApplication.Transaction;
import cs442.group2.BankingApplication.helpers.AccountChoice;

@SuppressWarnings({ "unused" })
public class ApplicationTesting {
	public static void main(String[] args) {
		// transferFromSavingsToCheckings();

		// listAllAccountsOfCustomer();

		// listAccountByID();

		// getTransactionHistory();

		// makeOrder();

		 //getOrderHistory();
		
		getAccountByCreditCard();

		Reporting.showLog();
	}

	private static void getAccountByCreditCard() {
		Customer abhishekKanchan = Customer.authenticate("akanch4", "password");
		Account account = Account.getAccountByCreditCardNumber("8888888888888882", "888");
		System.out.println(account);
		
	}

	private static void makeOrder() {

		ArrayList<Account> listAcc = new ArrayList<Account>();
		Account acc1 = Account.getAccount(2);

		Account acc2 = Account.getAccount(14);

		Customer abhishekKanchan = Customer.authenticate("akanch4", "password");

		if (abhishekKanchan.getCart().getAllItems().size() == 0) {
			List<Item> items = Item.getAllItems();
			for (int i = 0; i < 3; i++) {
				abhishekKanchan.addItemToCart(items.get(i), i + 1);
			}
		}

		// Choose option for payment

		ArrayList<AccountChoice> accChoices = new ArrayList<AccountChoice>();
		AccountChoice choice1 = new AccountChoice(acc1, abhishekKanchan
				.getCart().getItemTotalCost() - 2);
		AccountChoice choice2 = new AccountChoice(acc2, 2);
		
		accChoices.add(choice1);
		accChoices.add(choice2);

		boolean orderstatus = abhishekKanchan.order(accChoices);
		System.out.println("Order Status is : " + orderstatus);

	}

	private static void getOrderHistory() {
		Customer me = Customer.authenticate("akanch4", "password");
		List<Order> orders = me.orderHistory();
		for (Order order : orders) {
			Reporting.out.println(order);
		}
	}

	private static void getTransactionHistory() {
		Customer me = Customer.authenticate("apatil4", "password");
		List<Account> accounts = me.getAccounts();
		Account account = accounts.get(0);
		List<Transaction> accountHistory = account.accountHistory();
		for (Transaction transaction : accountHistory) {
			System.out.println(transaction);
		}
	}

	private static void listAccountByID() {
		int accountIDs[] = { 7, 13, 19, 25 };
		for (int accountID : accountIDs) {
			Account account = Account.getAccount(accountID);
			System.out.println(account);

		}

	}

	private static void listAllAccountsOfCustomer() {
		// TODO: Change database schema, update balance for credits to 1500

		Customer me = Customer.authenticate("apatil4", "password");
		List<Account> accounts = me.getAccounts();
		String accountHistory = Arrays.toString(accounts
				.toArray(new Account[0]));
		Reporting.out.println(accountHistory);

	}

	private static void transferFromSavingsToCheckings() {
		Customer me = Customer.authenticate("apatil4", "password");

		List<Account> accounts = me.getAccounts();

		Account savings = null, checkings = null;
		for (Account account : accounts) {
			if (account.getAccountType().equals("Savings"))
				savings = account;
			if (account.getAccountType().equals("Checkings"))
				checkings = account;
		}

		Reporting.out.println(checkings);

		boolean transfer = me.transfer(savings, checkings, 10);
		Reporting.out.println("Transfer status: " + transfer);
		Reporting.out.println("Checkings updated balance: "
				+ checkings.getBalance());

	}
}
