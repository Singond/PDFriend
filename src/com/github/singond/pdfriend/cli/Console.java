package com.github.singond.pdfriend.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.beust.jcommander.JCommander;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.Out;
import com.github.singond.pdfriend.Util;
import com.github.singond.pdfriend.Version;
import com.github.singond.pdfriend.imposition.ImposeCommand;
import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleException;
import com.github.singond.pdfriend.pipe.Pipe;
import com.github.singond.pdfriend.pipe.PipeException;

/**
 * The root of the command-line interface.
 * Handles global arguments to the application and dispatches subcommands.
 *
 * @author Singon
 *
 */
public class Console {
	/** Object to receive parsed global options */
	private final GlobalOptions global = new GlobalOptions();
	/** Object to receive parsed subcommands */
	private List<SubCommand> subcommands = new ArrayList<>();
	/** Object to receive parsed input files */
	private final InputFiles inputFiles = new InputFiles();
	/** Object to receive parsed output file */
	private final OutputFile outputFile = new OutputFile();
	/** Pattern to split the argument array at. This must be one element. */
	private static final String SUBCOMMAND_DELIMITER = "+";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(Console.class);
	
	/** A container class grouping all possible subcommands (ie. modules) */
	@SuppressWarnings("serial")
	private static class SubCommands extends HashMap<String, SubCommand> {
		{
			put("impose", new ImposeCommand());
		}
		
		/**
		 * Returns the value mapped to the given key (or null, if the map
		 * contains no mapping for the key) and replaces the value at that
		 * key with a new instance of the same type as the returned object.
		 * @param key
		 * @return
		 */
		public SubCommand getAndReplace(String key) {
			SubCommand value = super.get(key);
			if (value != null) {
				put((String) key, value.newInstance());
			}
			return value;
		}
		
		/**
		 * Prevents calling get().
		 * @throws UnsupportedOperationException on every invocation
		 */
		@Override
		public SubCommand get(Object key) {
			throw new UnsupportedOperationException(
					"Cannot retrieve key from this map without replacing it. Use getAndReplace.");
		}
	}
	
	/**
	 * Run PDFriend in command-line mode.
	 * @param args the whole argument array passed into the program
	 */
	public void execute(String[] args) {
		/** The argument line split into sections by subcommand */
		List<List<String>> splitArgs = splitArgs(Arrays.asList(args));
		/** A helper object grouping the parsed objects */
		Arguments arguments = new Arguments(global, inputFiles, outputFile, subcommands);
		
		/* Parse and setup */
		
		parse(splitArgs, arguments);
		// Set verbosity level as early as possible
		setVerbosity(global.quiet(), global.verbose(), global.debug());
		
		Pipe pipe = new Pipe();
		try {
			for (SubCommand subcmd : subcommands) {
				subcmd.postParse();
				pipe.addOperation(subcmd.getModule());
			}
		} catch (ParameterConsistencyException e) {
			// TODO Handle the exception somehow
			logger.error("Conflicting arguments", e);
			return;
		}
		
		/* Run the whole thing */
		
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
			help();
			System.exit(0);
		}
		
		// End global-level option processing and run the subcommand
		try {
			pipe.setInput(inputFiles.getInput());
			pipe.setOutput(outputFile.getOutput());
			pipe.execute();
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in module", e);
			return;
		} catch (PipeException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in pipe", e);
			return;
		}
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
	 * <p>
	 * The global options must be specified in the first section, while
	 * the input files must be listed in the last section of the command
	 * line.
	 * </p>
	 * @param argSections the argument string split into sections with
	 *        one subcommand in each
	 * @param arguments the object to receive the parsed arguments
	 */
	private void parse(List<List<String>> argSections, Arguments arguments) {
		/** A loop counter */
		int i = 0;
		SubCommands subcmds = new SubCommands();
		for (List<String> argSection : argSections) {
			JCommander.Builder cmdrBldr = JCommander.newBuilder();
			// If this is the first section of the command line,
			// parse global options
			if (i == 0) {
				cmdrBldr.addObject(arguments.globalOptions);
			}
			// If this is the last section of the command line,
			// parse input and output files as well
			if (i == argSections.size()-1) {
				/*
				 * Pass the reference to fields we want to have filled to the
				 * subcommands, otherwise the parser will fill new instances,
				 * which are unknown here.
				 */
				for (SubCommand sc : subcmds.values()) {
					sc.setInputFiles(inputFiles);
					sc.setOutputFile(outputFile);
				}
			}
			// Add all the subcommands
			for (Map.Entry<String, SubCommand> cmd : subcmds.entrySet()) {
				cmdrBldr.addCommand(cmd.getKey(), cmd.getValue());
			}
			// Parse it
			JCommander cmdr = cmdrBldr.build();
			cmdr.parse(argSection.toArray(new String[argSection.size()]));
			// Retrieve the initialized subcommand and put it to the output
			SubCommand subcmd = subcmds.getAndReplace(cmdr.getParsedCommand());
			if (subcmd != null)
				arguments.subCommands.add(subcmd);
			i++;
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
	private void help() {
		/** A mock instance of JCommander used to generate the help text. */
		final JCommander helpParser;
		SubCommands subcmds = new SubCommands();
		JCommander.Builder globalParserBldr = JCommander.newBuilder()
				.addObject(new GlobalOptions())
				.addObject(new InputFiles())
				.addObject(new ArrayList<Module>());
		for (Map.Entry<String, SubCommand> cmd : subcmds.entrySet()) {
			globalParserBldr.addCommand(cmd.getKey(), cmd.getValue());
		}
		helpParser = globalParserBldr.build();
		//jcommander.setColumnSize(80);
		helpParser.usage();
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
	
	/**
	 * A helper class which groups all the objects parseable from the
	 * command line into one.
	 */
	@SuppressWarnings("unused")
	private static class Arguments {
		private final GlobalOptions globalOptions;
		private final InputFiles inputFiles;
		private final OutputFile outputFile;
		private final List<SubCommand> subCommands;
		
		private Arguments(GlobalOptions globalOpts, InputFiles inputFiles,
		                  OutputFile outputFile, List<SubCommand> subcmds) {
			this.globalOptions = globalOpts;
			this.inputFiles = inputFiles;
			this.outputFile = outputFile;
			this.subCommands = subcmds;
		}
		
		private Arguments() {
			this.globalOptions = new GlobalOptions();
			this.inputFiles = new InputFiles();
			this.outputFile = new OutputFile();
			this.subCommands = new ArrayList<SubCommand>();
		}
	}
}
