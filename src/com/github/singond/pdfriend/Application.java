package com.github.singond.pdfriend;

import com.github.singond.pdfriend.cli.Console;
import com.github.singond.pdfriend.cli.ExitStatus;

/**
 * The main class; this represents a single instance of PDFriend.
 * 
 * <p>Exit codes:
 * <ul>
 *     <li>-1: General error</li>
 *     <li> 0: Success</li>
 *     <li> 1: Unknown command</li>
 * </ul></p>
 * 
 * @author Singon
 *
 */
public class Application {

	private static ExtendedLogger logger = Log.logger("Main");

	/**
	 * Runs this instance of PDFriend.
	 */
	private void execute(String[] args) {
		long start = System.currentTimeMillis();
		
		if (args.length <= 0) {
			// GUI (to be added later). For now, just print version.
			Out.line("This is PDFriend version %s", Version.current().toString());
			System.exit(0);
		} else {
			ExitStatus status = new Console().execute(args);
			if (status == ExitStatus.SUCCESS) {
				long end = System.currentTimeMillis();
				logger.info("total_time", end-start);
			}
		}
	}
	
	/**
	 * Runs PDFriend.
	 */
	public static void main(String[] args) {
		new Application().execute(args);
	}
}
