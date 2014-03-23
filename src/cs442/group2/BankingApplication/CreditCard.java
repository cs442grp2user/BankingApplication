package cs442.group2.BankingApplication;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreditCard extends Account{
	
	private String cardNumber;
	private short cvv2;
	//private float availableBalance;
	private float approvedCredit;
	
		
	private CreditCard(int accountID, int customerID, String accountType, int pin, String cardNumber,short cvv2,float approvedCredit) {
		
		super(accountID, customerID, accountType, pin);
		this.cardNumber = cardNumber;
		this.cvv2 = cvv2 ;
		//this.availableBalance = availableBalance;
		this.approvedCredit = approvedCredit;		
		// TODO Auto-generated constructor stub
		}
	
	
	public static CreditCard getAccount(int accountID) {
		CreditCard creditAccount = new CreditCard(1000, 100, "Credit", 888, "8888888888888882",(short)888,1000);
		// Add DB code.
		
		Connection conn = BankConnect.getConnection();
		if(conn!=null)	
			System.out.println("Connection established successfully");
			
		try {
			PreparedStatement statement = conn.prepareStatement(accID);
			statement.setInt(1, accountID);
			ResultSet rs = statement.executeQuery();
			
				
			while(rs.next()){
				String cardno = rs.getString("cardnumber");
				System.out.println("Value from the result - Credit card number "+ cardno);
				
			}
			
		} catch (SQLException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			Reporting.err.println(e);
		}
 		
		return creditAccount; // its OK to return null as account may not exist
	}
	private static String accID = "SELECT * FROM Credit WHERE accountID = ?;";
	
public static void main(String[] args){
		
		CreditCard cc = new CreditCard(1000, 100, "Credit", 888, "8888888888888882",(short)888,1000);
		cc.getAccount(14);
		//System.out.println() ;
	}	
}
