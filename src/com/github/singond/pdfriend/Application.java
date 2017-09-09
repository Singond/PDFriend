package com.github.singond.pdfriend;

import com.github.singond.pdfriend.cli.Console;

/**
 * The main class; this executes PDFriend.
 * This is the branching point for the application, ie. when a subcommand
 * is given right after {@code pdfriend} command, it is executed.
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
	 * Runs PDFriend.
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		//logger.debug("The working directory is {}", Util.getWorkingDir());
		//logger.debug("The application directory is {}", Util.getApplicationDir());
		
		if (args.length <= 0) {
			// GUI (to be added later). For now, just print version.
			Out.line("This is PDFriend version %s", Version.current().toString());
			System.exit(0);
		} else {
			new Console().execute(args);
			long end = System.currentTimeMillis();
			logger.info("total_time", end-start);
		}
	}
}
