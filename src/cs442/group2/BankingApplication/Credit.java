package cs442.group2.BankingApplication;

public class Credit extends Account {

	private String cardNumber;
	private String cvv2;
	private double approvedCredit;

	protected Credit(int accountID, int customerID, String accountType,
			int pin, String cardNumber, String cvv2, double approvedCredit) {

		super(accountID, customerID, accountType, pin);
		this.cardNumber = cardNumber;
		this.cvv2 = cvv2;
		this.approvedCredit = approvedCredit;

	}

	public String getCardNumber() {
		return cardNumber;
	}

	public String getCvv2() {
		return cvv2;
	}

	
	public double getApprovedCredit() {
		return approvedCredit;
	}

}
