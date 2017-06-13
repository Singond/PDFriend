package cz.slanyj.pdfriend.cli;

import java.util.Arrays;

import org.apache.logging.log4j.Level;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.Out;
import cz.slanyj.pdfriend.Version;

/**
 * The root of the command-line interface.
 * Handles global arguments to the application and dispatches subcommands.
 *
 * @author Singon
 *
 */
public class Console {
	private static final ExtendedLogger logger = Log.logger(Console.class);

	/* Root options */
	
	/** Print version info and exit */
	@Parameter(names={"-V", "--version"}, description="Print version info and exit")
	private boolean version = false;
	
	/** Set Log4j to VERBOSE level */
	@Parameter(names={"-v", "--verbose"}, description="Verbose output")
	private boolean verbose = false;
	
	/** Set Log4j to DEBUG level */
	@Parameter(names={"-vv", "--debug"}, description="Extra verbose output, used for debugging")
	private boolean debug = false;
	
	/** Set Log4j to WARN level */
	@Parameter(names={"-q", "--quiet"}, description="Be less verbose than normal, display only warnings")
	private boolean quiet = false;
	
	/**
	 * Run PDFriend in command-line mode.
	 * @param args the whole argument array passed into the program
	 */
	public void execute(String[] args) {
		logger.debug("PDFriend arguments: " + Arrays.toString(args));
		
		// Parse arguments
		JCommander.newBuilder()
		          .addObject(this)
		          .acceptUnknownOptions(true)
		          .build()
		          .parse(args);
		
		/* Run */
		// Display version
		if (version) {
			version();
			System.exit(0);
		}
		// Set verbosity level
		setVerbosity(quiet, verbose, debug);
		
	}
	
	/** Prints version info and exits */
	private void version() {
		Out.line("This is PDFriend version %s", Version.current().toString());
		System.exit(0);
	}
	
	/**
	 * Sets the verbosity level to use in this application instance.
	 * <p>
	 * TODO If more than one flag is set to true, issue a warning or error.
	 * </p>
	 * @param quiet Toggles WARN level on.
	 * @param verbose Toggles VERBOSE level on.
	 * @param debug Toggles DEBUG level on.
	 */
	private void setVerbosity(boolean quiet, boolean verbose, boolean debug) {
		if (quiet) {
			Log.setLevel(Level.WARN);
		} else if (verbose) {
			Log.setLevel(Log.VERBOSE);
		} else if (debug) {
			Log.setLevel(Level.DEBUG);
		}
	}
}
