/**
 * 
 */
package cs442.group2.BankingApplication.exceptions;

import cs442.group2.BankingApplication.Customer;

/**
 * @author Abhishek K
 *
 */
@SuppressWarnings("serial")
public class InvalidCustomerException extends RuntimeException {
	
	private String message;
	
	public InvalidCustomerException(Customer customer) {
		super();
		this.message = "Customer having customerID: "+customer.getCustomerID() 
						+ " is not a valid customer for making a transaction in the system";
	}
	
	@Override
	public String toString() {
		return message;
		
	}

}
