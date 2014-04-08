package cs442.group2.BankingApplication.helpers;

import cs442.group2.BankingApplication.Account;

public class AccountChoice {
	Account account;
	double deductAmount;

	@Override
	public String toString() {
		return String.format("AccountChoice(%s, %4.2f)", account.toString(), -deductAmount);
	}
	public AccountChoice(Account account, double deductAmount) {
		this.account = account;
		this.deductAmount = deductAmount;
	}

	public Account getAccount() {
		return account;
	}

	public double getDeductAmount() {
		return deductAmount;
	}

	public void setDeductAmount(double deductAmount) {
		this.deductAmount = deductAmount;
	}

}
