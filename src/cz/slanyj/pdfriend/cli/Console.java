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
	@Parameter(names={"-V", "--version"}, description="Print version info and exit", order=0)
	private boolean version = false;
	
	/** Print help info and exit */
	@Parameter(names={"-h", "-?", "--help"}, description="Print this help page and exit", order=1)
	private boolean help = false;
	
	/** Set Log4j to VERBOSE level */
	@Parameter(names={"-v", "--verbose"}, description="Verbose output", order=4)
	private boolean verbose = false;
	
	/** Set Log4j to DEBUG level */
	@Parameter(names={"-vv", "--debug"}, description="Extra verbose output, used for debugging", order=5)
	private boolean debug = false;
	
	/** Set Log4j to WARN level */
	@Parameter(names={"-q", "--quiet"}, description="Be less verbose than normal, display only warnings", order=6)
	private boolean quiet = false;
	
	/**
	 * Run PDFriend in command-line mode.
	 * @param args the whole argument array passed into the program
	 */
	public void execute(String[] args) {
		logger.debug("PDFriend arguments: " + Arrays.toString(args));
		
		// Parse arguments
		JCommander.Builder jcbuilder = JCommander.newBuilder()
				.addObject(this)
				.acceptUnknownOptions(true);
		JCommander jcommander = jcbuilder.build();
		jcommander.parse(args);
		
		/* Run */
		// Display version
		if (version) {
			version();
			System.exit(0);
		}
		// Display help
		if (help) {
			help(jcommander);
			System.exit(0);
		}
		// Set verbosity level
		setVerbosity(quiet, verbose, debug);
		
	}
	
	/** Prints version info and exits. */
	private void version() {
		Out.line("This is PDFriend version %s", Version.current().toString());
		System.exit(0);
	}
	
	/**
	 * Prints help page and exits.
	 * @param jcommander an instance of jcommander used to parse the CLI args
	 */
	private void help(JCommander jcommander) {
		//jcommander.setColumnSize(80);
		jcommander.usage();
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
