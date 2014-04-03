package cs442.group2.BankingApplication.exceptions;

import cs442.group2.BankingApplication.Account;

@SuppressWarnings("serial")
public class InsufficientBalanceException extends RuntimeException {
	// Throw this exception in CheckTransaction
	// Thrown when not sufficient balance.

	private String message;

	public InsufficientBalanceException(Account account, double deductAmount) {
		super();
		this.message = "Total deductAmount: " + deductAmount
				+ " is more than Account(accountID = " + account.getAccountID()
				+ ") balance";
	}

	@Override
	public String toString() {
		return message;
	}
}
