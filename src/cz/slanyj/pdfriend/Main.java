package cz.slanyj.pdfriend;

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

	/**
	 * If the first argument exists and begins with letter,
	 * treat it as a console subcommand.
	 */
	public static void main(String[] args) {
		Log.debug("The working directory is %s", Util.getWorkingDir());
		Log.debug("The application directory is %s", Util.getApplicationDir());
		
		if (args.length <= 0) {
			// GUI (to be added later). For now, just print usage.
			usage();
			
		} else if (Character.isLetter(args[0].charAt(0))) {
			// A subcommand, execute in CLI
			String sub = args[0];
			String[] subArgs = new String[args.length-1];
			System.arraycopy(args, 1, subArgs, 0, args.length-1);
			subcommand(sub, subArgs);
		} else {
			// Top-level command with arguments.
			// Could either start GUI or CLI.
			// In either case, don't forget to pass command-line parameters.
			usage();
		}
	}
	
	/** Prints usage info and exits */
	private static void usage() {
		Out.line("This is PDFriend version %s", Version.current().toString());
		System.exit(0);
	}
	
	/**
	 * Executes a pdfriend subcommand.
	 * @param cmd The subcommand.
	 * @param args Its command-line arguments.
	 */
	private static void subcommand(String cmd, String[] args) {
		switch (cmd) {
			default:
				Log.error(Bundle.console, "unknownCommand", cmd);
				System.exit(1);
		}
	}
}
