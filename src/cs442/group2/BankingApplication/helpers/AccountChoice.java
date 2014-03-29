package cs442.group2.BankingApplication.helpers;

import cs442.group2.BankingApplication.Account;

public class AccountChoice {
	Account account;
	double deductAmount;
	
	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * @return the deductAmount
	 */
	public double getDeductAmount() {
		return deductAmount;
	}

	/**
	 * @param deductAmount the deductAmount to set
	 */
	public void setDeductAmount(double deductAmount) {
		this.deductAmount = deductAmount;
	}

	

	public AccountChoice(Account account, double deductAmount) {
		this.account = account;
		this.deductAmount = deductAmount;
	}

}
