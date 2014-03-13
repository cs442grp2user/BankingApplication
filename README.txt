CS442Project

This README is also available in the Eclipse Project.

Please follow these coding guidelines(and the FORCE will be with you! :D)

It will help you write code quickly, of much quality, and error-free
Add/Amend to this guide anytime. 

# Writing style
1. Highly use Source -> Organize imports and Source -> Add Import via shortcuts (Ctrl+Shift+O or [MAC]Cmd+Shift+O)
2. Highly use Source -> Format to format source code (Ctrl+Shift+F or [MAC]Cmd+Shift+F)
3. Always use single line comments for a line or a paragraph (I start by writing everything of comment 
   on a line and then do Source -> Format which brings it to multiple lines with proper comments applied)
   Again use proper keyboard shortcut on the selection you want to comment
4. Use Reporting.err.println(exception) to generate error long and 
5. Use Reporting.out.println(anything) to output anything (defaults to System.out.. check below why this)
6. If you find Sytem.out.println anywhere, my bad, change it to Reporting.out.println

7. IMPORTANT*** If there a warning icon to a file, don't try to fix it if variable/import is unused or some 
   reason which is very minor. If its not causing any error,  
   Let it be there, its ok, your code is completely fine!

# Coding style 
1. Always use PreparedStatement even for SELECT queries without any placeholders.
2. Never try to sanitize input for PreparedStatements via custom code, let the library take care of it.
3. IMPORTANT*** Never return a null from any function accept these: (i say this because I think it would be easier in JUnit testing[correct me if wrong])
     a. Customer.authenticate(...) (if null means credentials invalid or no user)
     b. Transaction Transaction.makeTransaction(...) (if null means transfer failed)
     c. Order Transaction.makeTransaction(...) (if null means order failed)
     d. Any other function where i have written in comments that you can return null;
4. Never throw any Exception from the functions in our package.
5. If a function contains code which might possibly be throw an Exception, use try-catch{-finally if needed}

       try{
            DB Code / Code that may throw exception
       }
       catch(Exception e){ // Will catch all kinds of Exception
            Reporting.err.println(e);
       }   

6. Always use Reporting.out.println to show any output. It is configured to System.out for now
	but we can change it to a file or anything if required for web server code.

7. Always use transactions with INSERT/ UPDATE statements (because see below)

# Transaction Coding style
1. I haven't done more of transaction coding in Java but here is an overview.
2. Our connection object is initialized by setting auto commit to false.
3. Hence after all the changes are made (say you do multiple inserts or an update or combination)
   then, perform a commit by calling conn.commit()
4. IMPORTANT*** Never ever close the connection object you get from BankConnect.getConnection()
5. IMPORTANT*** Never commit in a function which only execute SELECT statements, 
   it will create problems in functions like order or transfer 
6. It is perfectly fine to use nested try-catch when writing transactions like this:

try{
	try{
		Connection conn = BankConnect.getConnection();
		if(conn != null){
			// One more guideline, put all the 'SQL' statements in a static String variable below the class
			// so that its easy to go look below for any SQL code for any body else
			// Also, the code is visually less cluttered by following this practice.
			
			// SQL for the statement below is defined at the end of class as this
			// private static String sqlStringForInsertOrUpdate = " YOUR SQL CODE ;";
			
			// I usually do this quickly by writing the variable name in the call below.
			// Eclipse show me the error that the variable is undefined
			// I then use Ctrl+1 or Cmd+1 to assign it by the option 'Create Field'
			// This variable i then make it private static and place it at the end of class definition.
			
			PreparedStatement statement = conn.prepareStatement(sqlStringForInsertOrUpdate);
			// Call the right execute function for insert or update
			
			// everything went well
			conn.commit(); // rollback is below
		} else {
			// Time to check errorInBankPortal.log because the connection to database
			// is not working.
		}
	} catch(SQLException e){
		Reporting.err.println(e); // Some problem happened. check our error log
		conn.rollBack(); // <- This guy also throws error :/ 
	}
}
catch(Exception e){
	Reporting.err.println(e); // Something really bad happened if it comes here
}