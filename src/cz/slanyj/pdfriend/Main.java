package cz.slanyj.pdfriend;

import cz.slanyj.pdfriend.cli.Console;

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
public class Main {

	/** A map which binds subcommand names to their implementations */
//	private static Map<String, Supplier<SubCommand>> subcommands = new HashMap<>();
//	static {
//		subcommands.put("impose", Impose::new);
//	}
	private static ExtendedLogger logger = Log.logger(Main.class);
	

	/**
	 * Runs PDFriend.
	 */
	public static void main(String[] args) {
		logger.debug("The working directory is {}", Util.getWorkingDir());
		logger.debug("The application directory is {}", Util.getApplicationDir());

		if (args.length <= 0) {
			// GUI (to be added later). For now, just print version.
			Out.line("This is PDFriend version %s", Version.current().toString());
			System.exit(0);
		} else {
			new Console().execute(args);
		}
	}
}
