package cz.slanyj.pdfriend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import cz.slanyj.pdfriend.cli.Impose;
import cz.slanyj.pdfriend.cli.SubCommand;

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
	
	private static ExtendedLogger logger = Log.logger(Main.class);
	
	private static Map<String, Supplier<SubCommand>> subcommands = new HashMap<>();
	static {
		subcommands.put("impose", Impose::new);
	}

	/**
	 * Runs PDFriend.
	 */
	public static void main(String[] args) {
		logger.debug("The working directory is {}", Util.getWorkingDir());
		logger.debug("The application directory is {}", Util.getApplicationDir());
		
		if (args.length <= 0) {
			// GUI (to be added later). For now, just print usage.
			usage();
			
		} else {
			/*
			 * Split the arguments array at the first element which matches
			 * the name of any subcommand. Pass the part before that as arguments
			 * to PDFriend, use the rest (excluding the subcommand itself)
			 * as arguments to the subcommands.
			 */
			String[] globalArgs = null;
			String cmdName = null;
			String[] cmdArgs = null;
			for (int i=0; i<args.length; i++) {
				String arg = args[i];
				if (subcommands.containsKey(arg)) {
					globalArgs = new String[i];
					System.arraycopy(args, 0, globalArgs, 0, i);
					cmdArgs = new String[args.length-i-1];
					System.arraycopy(args, i+1, cmdArgs, 0, args.length-i-1);
					cmdName = arg;
					break;
				}
			}
			if (cmdName == null) {
				master(args);
			} else {
				globalOptions(globalArgs);
				subcommands.get(cmdName).get().execute(cmdArgs);
			}
			
			
			// A subcommand, execute in CLI
//			String sub = args[0];
//			String[] subArgs = new String[args.length-1];
//			System.arraycopy(args, 1, subArgs, 0, args.length-1);
//			subcommand(sub, subArgs);
		}
	}
	
	/**
	 * Action to invoke when no valid subcommand has been specified.
	 * @param args
	 */
	private static void master(String[] args) {
		globalOptions(args);
		usage();
	}
	
	/**
	 * Handles global options (ie. options which do not belong with
	 * specific module).
	 */
	private static void globalOptions(String[] args) {
		logger.debug("Global arguments " + Arrays.toString(args));
		// Implement as necessary
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
//	private static void subcommand(String cmd, String[] args) {
//		switch (cmd) {
//			case "impose":
//				Impose.main(args);
//				break;
//			default:
//				logger.error("unknownCommand", cmd);
//				System.exit(1);
//		}
//	}
}
