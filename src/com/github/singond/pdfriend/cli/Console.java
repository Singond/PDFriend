package com.github.singond.pdfriend.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.Out;
import com.github.singond.pdfriend.Util;
import com.github.singond.pdfriend.Version;
import com.github.singond.pdfriend.cli.parsing.GlobalOptions;

/**
 * The root of the command-line interface.
 * Handles global arguments to the application and dispatches subcommands.
 *
 * @author Singon
 *
 */
public class Console {
	/** Container of global options */
	private final GlobalOptions global = new GlobalOptions();
	/** Logger */
	private static ExtendedLogger logger = Log.logger(Console.class);
	
	/** Subcommands (ie. modules) */
	private final Map<String, SubCommand> subcommands = new HashMap<>();
	{
		subcommands.put("impose", new Impose());
	}
	
	/**
	 * Run PDFriend in command-line mode.
	 * @param args the whole argument array passed into the program
	 */
	public void execute(String[] args) {
		/* Parse the CLI arguments */
		JCommander.Builder jcbuilder = JCommander.newBuilder()
				.addObject(global)
				.acceptUnknownOptions(false);
		// Register subcommands with the parser
		for (Map.Entry<String, SubCommand> cmd : subcommands.entrySet()) {
			jcbuilder.addCommand(cmd.getKey(), cmd.getValue());
		}
		JCommander jcommander = jcbuilder.build();
		jcommander.parse(args);
		// Set verbosity level as early as possible
		setVerbosity(global.quiet(), global.verbose(), global.debug());
		SubCommand subcommand = subcommands.get(jcommander.getParsedCommand());
		subcommand.postParse();
		
		/* Run */
		
		logger.debug("The working directory is {}", Util.getWorkingDir());
		logger.debug("The application directory is {}", Util.getApplicationDir());
		logger.debug("PDFriend arguments: " + Arrays.toString(args));
		
		// Display version and exit (--version)
		if (global.version()) {
			version();
			System.exit(0);
		}
		
		// Display help and exit (--help)
		if (global.help()) {
			help(jcommander);
			System.exit(0);
		}
		
		// End global-level option processing and run the subcommand
		subcommand.execute();
	}
	
	/** Prints version info. */
	private void version() {
		Out.line("This is PDFriend version %s", Version.current().toString());
	}
	
	/**
	 * Prints help page.
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
