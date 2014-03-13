package cs442.group2.BankingApplication;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

public class Reporting {
	public static PrintStream out = System.out; // You can initialize this to a
												// file too like its done for
												// error
	public static PrintStream err;
	static {

		try {
			err = new PrintStream(new FileOutputStream(
					"./errorInBankPortal.log.txt"));
			err.println(Calendar.getInstance().getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out
					.println("\nFATAL ERROR: Problem creating error log file. "
							+ "Stop everything & inspect the Reporting class");

		}

	}

}
