package cs442.group2.BankingApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Scanner;


public class Reporting {

	static class MyErrorPrinter {
		public PrintStream err;

		public MyErrorPrinter() {
			try {
				this.err = new PrintStream(new FileOutputStream(
						"./errorInBankPortal.log.txt"));
				this.err.println(Calendar.getInstance().getTime());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out
						.println("\nFATAL ERROR: Problem creating error log file. "
								+ "Stop everything & inspect the Reporting class");
				System.exit(1);

			}

		}

		public void println(Exception e) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			this.err.println(stringWriter.toString());
		}

		@Override
		protected void finalize() throws Throwable {
			this.err.close();
		}
	}

	public static PrintStream out = System.out;
	public static MyErrorPrinter err = new MyErrorPrinter();

	public static void showLog() {
		try {
			System.out.println("*******************************");
			System.out.println("ERROR File:");
			System.out.println("*******************************");
			Scanner in = new Scanner(new FileInputStream(
					"errorInBankPortal.log.txt"));
			while (in.hasNextLine()) {
				System.out.println(in.nextLine());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
