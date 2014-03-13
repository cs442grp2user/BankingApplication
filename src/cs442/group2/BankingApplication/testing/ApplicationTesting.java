package cs442.group2.BankingApplication.testing;


import cs442.group2.BankingApplication.Customer;
import cs442.group2.BankingApplication.Reporting;

public class ApplicationTesting {
	public static void main(String[] args) {
		// Add this file to .gitignore since it can be used by everyone to test their code.
		
		Customer me = Customer.authenticate("apatil4", "password");
		Reporting.out.println(me);
		
	}
}
