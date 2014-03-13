package cs442.group2.BankingApplication.helpers;

import cs442.group2.BankingApplication.Account;

public class AccountChoice {
	Account account;
	double deductAmount;

	public AccountChoice(Account account, double deductAmount) {
		this.account = account;
		this.deductAmount = deductAmount;
	}

}
