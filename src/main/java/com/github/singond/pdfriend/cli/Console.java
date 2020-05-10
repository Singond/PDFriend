package com.github.singond.pdfriend.cli;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.logging.log4j.Level;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

import com.github.singond.pdfriend.ExitStatus;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.Util;
import com.github.singond.pdfriend.Version;
import com.github.singond.pdfriend.imposition.ImposeCommand;
import com.github.singond.pdfriend.imposition.SimpleTransformCommand;
import com.github.singond.pdfriend.io.InputFactory;
import com.github.singond.pdfriend.io.OutputFactory;
import com.github.singond.pdfriend.modules.ModuleException;
import com.github.singond.pdfriend.pipe.Pipe;
import com.github.singond.pdfriend.pipe.PipeException;
import com.github.singond.pdfriend.reorder.ReorderCommand;

/**
 * The root of the command-line interface.
 * Handles global arguments to the application and dispatches subcommands.
 *
 * @author Singon
 *
 */
@Command(name="pdfriend",
	mixinStandardHelpOptions = true,
	subcommandsRepeatable = true,
	subcommands = {
		SimpleTransformCommand.class,
		ImposeCommand.class,
		ReorderCommand.class
	}
)
public class Console {

	/** Object to receive parsed global options */
	@Mixin
	private GlobalOptions global;

	/** Logger */
	private static ExtendedLogger logger = Log.logger(Console.class);

	/**
	 * Run PDFriend in command-line mode with the given arguments.
	 *
	 * @param args the whole argument array passed into the program
	 */
	public ExitStatus execute(String[] args) {
		CommandLine cmdline = new CommandLine(this);
		try {
			ParseResult parsed = cmdline.parseArgs(args);
			if (logger.isVerboseEnabled()) {
				logger.verbose("PDFriend version {}", Version.current().toString());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Working directory: {}", Util.getWorkingDir());
				logger.debug("Application directory: {}", Util.getApplicationDir());
				logger.debug("Arguments: " + String.join(" ", args));
			}
    		if (cmdline.isUsageHelpRequested()) {
    			cmdline.usage(cmdline.getOut());
    			return ExitStatus.SIMPLE;
    		} else if (cmdline.isVersionHelpRequested()) {
    			cmdline.printVersionHelp(cmdline.getOut());
    			return ExitStatus.SIMPLE;
    		} else {
    			List<CommandLine> parsedCmds = parsed.asCommandLineList();
    			logger.trace(parsedCmds);
    			Deque<CliCommand> cmds = new ArrayDeque<>(parsedCmds.size());
    			for (ParseResult c : parsed.subcommands()) {
    				logger.trace(c);
    				List<CommandLine> cll = c.asCommandLineList();
    				logger.trace(cll);
    				if (!cll.isEmpty()) {
    					cmds.add(cll.get(cll.size() - 1).getCommand());
    				}
    				c.asCommandLineList().get(0);
    			}
    			return executeCommands(cmds);
    		}
		} catch (ParameterException e) {
			cmdline.getErr().println(e.getMessage());
			logger.error(e);
			// TODO: Change to "invalid arguments" or smth.
			return ExitStatus.INPUT_FAILURE;
		} catch (Exception e) {
			logger.error(e);
			return ExitStatus.FAILURE;
		}
	}

	public ExitStatus executeCommands(Deque<CliCommand> commands) {
		Pipe pipe = new Pipe();
		for (CliCommand subcmd : commands) {
			pipe.addOperation(subcmd.getModule());
		}
		if (!commands.isEmpty()) {
			CliCommand lastcmd = commands.getLast();
			pipe.setInput(InputFactory.of(lastcmd.inputFiles()));
			pipe.setOutput(OutputFactory.of(lastcmd.outputFile()));
		}

		try {
			pipe.execute();
			return ExitStatus.SUCCESS;
		} catch (ModuleException e) {
			logger.error("Exception in " + e.getModule().name() + " module:",
			             e.getCause());
			return ExitStatus.FAILURE;
		} catch (PipeException e) {
			// Show the cause, hide PipeException from the user
			Throwable cause = e.getCause();
			logger.error(cause.getMessage(), cause);
			return ExitStatus.FAILURE;
		}
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
