package com.github.singond.pdfriend.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
	/** Pattern to split the argument array at. This must be one element. */
	private static final String SUBCOMMAND_DELIMITER = "+";
	
	/** Subcommands (ie. modules) */
	private static class SubCommands extends HashMap<String, SubCommand> {{
		put("impose", new Impose());
	}}
	
	/**
	 * Run PDFriend in command-line mode.
	 * @param args the whole argument array passed into the program
	 */
	public void execute(String[] args) {
		List<List<String>> splitArgs = splitArgs(Arrays.asList(args));
		/** Parsed subcommands */
		List<SubCommand> subcommands = new ArrayList<>(splitArgs.size());
		
		parseGlobalArguments(splitArgs.get(0), global);
		parseSubCommands(splitArgs, subcommands);

		// Set verbosity level as early as possible
		setVerbosity(global.quiet(), global.verbose(), global.debug());
		SubCommand subcommand = subcommands.get(0);
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
//			help(jcommander);
			System.exit(0);
		}
		
		// End global-level option processing and run the subcommand
		subcommand.execute();
	}
	
	/**
	 * Splits the given list of arguments into several sublists at the
	 * elements matching {@code SUBCOMMAND_DELIMITER}.
	 * @param args
	 * @return
	 */
	private List<List<String>> splitArgs(List<String> args) {
		List<List<String>> result = new ArrayList<>();
		List<String> sublist = new ArrayList<>();
		for (String elem : args) {
			if (isDelimiter(elem)) {
				result.add(sublist);
				sublist = new ArrayList<>();
			} else {
				sublist.add(elem);
			}
		}
		result.add(sublist);	// Add the last sublist
		return result;
	}
	
	/**
	 * Checks whether the given String is to be considered
	 * a subcommand delimiter.
	 * @param element the String to be checked
	 * @return true if it is a delimiter
	 */
	private boolean isDelimiter(String element) {
		return SUBCOMMAND_DELIMITER.equals(element);
	}
	
	/**
	 * Parses the arguments list into the given global options object.
	 * @param arguments a list of the command-line arguments up to
	 *        (and including) the first subcommand
	 * @param target the global options object to receive the parsed values
	 */
	private void parseGlobalArguments(List<String> arguments,
	                                  GlobalOptions target) {
		JCommander.Builder globalParserBldr = JCommander.newBuilder()
				.addObject(target)
				.acceptUnknownOptions(true);
		JCommander globalParser = globalParserBldr.build();
		globalParser.parse(arguments.toArray(new String[arguments.size()]));
	}
	
	/**
	 * Parses each of the split arguments into a new SubCommand and collects
	 * the parsed subcommands into the given list.
	 * @param splitArgs a 2D list of arguments, with each sublist standing
	 *        for one subcommand and its arguments
	 * @param subCmdList the list to put parsed subcommands into
	 */
	private void parseSubCommands(List<List<String>> splitArgs,
	                              List<SubCommand> subCmdList) {
		for (List<String> sublist : splitArgs) {
			SubCommands subcmds = new SubCommands();
			JCommander.Builder subCmdParserBldr = JCommander.newBuilder();
			for (Map.Entry<String, SubCommand> cmd : subcmds.entrySet()) {
				subCmdParserBldr.addCommand(cmd.getKey(), cmd.getValue());
			}
			JCommander subCmdParser = subCmdParserBldr.build();
			subCmdParser.parse(sublist.toArray(new String[sublist.size()]));
			subCmdList.add(subcmds.get(subCmdParser.getParsedCommand()));
		}
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
