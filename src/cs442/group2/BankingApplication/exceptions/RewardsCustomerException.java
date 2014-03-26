/**
 * 
 */
package cs442.group2.BankingApplication.exceptions;

/**
 * @author Abhishek Kanchan
 *
 */
import cs442.group2.BankingApplication.Account;
import cs442.group2.BankingApplication.Customer;

@SuppressWarnings("serial")
public class RewardsCustomerException extends RuntimeException {
	//Exception thrown in checkTransaction() 
	//When customerID of Rewards account is not same as customerID of customer making the Transaction
	
	private String message;
	
	public RewardsCustomerException(Account account, Customer customer) {
		super();
		this.message = "Rewards account: "+account.getAccountID() + 
						"does not belong to the customer "+customer.getCustomerID() +
						"initiating the transaction";
		
	}
	
	@Override
	public String toString() {
		return message;
		
	}
	
}
