package cs442.group2.BankingApplication;

public class CreditCard extends Account{
	
	private String cardNumber;
	private short cvv2;
	//private float availableBalance;
	private float approvedCredit;
	
		
	protected CreditCard(int accountID, int customerID, String accountType, int pin, String cardNumber,short cvv2,float approvedCredit) {
		
		super(accountID, customerID, accountType, pin);
		this.cardNumber = cardNumber;
		this.cvv2 = cvv2 ;
		//this.availableBalance = availableBalance;
		this.approvedCredit = approvedCredit;		
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}


	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}


	/**
	 * @return the cvv2
	 */
	public short getCvv2() {
		return cvv2;
	}


	/**
	 * @param cvv2 the cvv2 to set
	 */
	public void setCvv2(short cvv2) {
		this.cvv2 = cvv2;
	}


	/**
	 * @return the approvedCredit
	 */
	public float getApprovedCredit() {
		return approvedCredit;
	}


	/**
	 * @param approvedCredit the approvedCredit to set
	 */
	public void setApprovedCredit(float approvedCredit) {
		this.approvedCredit = approvedCredit;
	}
	
}
