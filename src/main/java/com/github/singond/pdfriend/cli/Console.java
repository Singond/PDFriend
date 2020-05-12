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
		CommandLine maincmdl = new CommandLine(this);
		try {
			// Parse all arguments
			ParseResult parsed = maincmdl.parseArgs(args);
			// Set verbosity level as early as possible
			setVerbosity(global.verbosity());
			// Log basic info
			if (logger.isVerboseEnabled()) {
				logger.verbose("PDFriend version {}", Version.current().toString());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Working directory: {}", Util.getWorkingDir());
				logger.debug("Application directory: {}", Util.getApplicationDir());
				logger.debug("Arguments: " + String.join(" ", args));
			}
			// Run
			if (maincmdl.isUsageHelpRequested()) {
				// Display global help (pdfriend --help)
				maincmdl.usage(maincmdl.getOut());
				return ExitStatus.SIMPLE;
			} else if (maincmdl.isVersionHelpRequested()) {
				// Display version (pdfriend --version)
				maincmdl.printVersionHelp(maincmdl.getOut());
				return ExitStatus.SIMPLE;
			} else {
				// Run commands
				List<ParseResult> cmds = parsed.subcommands();
				Deque<CliCommand> exe = new ArrayDeque<>(cmds.size());
				for (ParseResult cmd : cmds) {
					List<CommandLine> subcmdl = cmd.asCommandLineList();
					if (!subcmdl.isEmpty()) {
						// The last item is the innermost command
						CommandLine cmdl = subcmdl.get(subcmdl.size() - 1);
						if (cmdl.isUsageHelpRequested()) {
							// Display command help
							cmdl.usage(cmdl.getOut());
							return ExitStatus.SIMPLE;
						} else if (cmdl.isVersionHelpRequested()) {
							// Command version makes no sense,
							// display app version
							maincmdl.printVersionHelp(cmdl.getOut());
							return ExitStatus.SIMPLE;
						} else {
							exe.add(cmdl.getCommand());
						}
					}
				}
				return executeCommands(exe);
			}
		} catch (ParameterException e) {
			maincmdl.getErr().println(e.getMessage());
//			logger.error("Invalid usage", e);   // Already printed by picocli
			// TODO: Change to "invalid arguments" or smth.
			return ExitStatus.INPUT_FAILURE;
		} catch (Exception e) {
			logger.error("Error when running PDFriend", e);
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
	 * Sets the verbosity level. Higher numbers mean more output.
	 * Negative values are allowed, they decrease the level of verbosity
	 * below the normal value.
	 *
	 * @param level verbosity level (0 is the normal logging level)
	 */
	private void setVerbosity(int level) {
		if (level < -2) {
			Log.setLevel(Level.OFF);
		} else if (level == -2) {
			Log.setLevel(Level.ERROR);
		} else if (level == -1) {
			Log.setLevel(Level.WARN);
		} else if (level == 0) {
			Log.setLevel(Level.INFO);
		} else if (level == 1) {
			Log.setLevel(Log.VERBOSE);
		} else if (level == 2) {
			Log.setLevel(Level.DEBUG);
		} else if (level > 2) {
			Log.setLevel(Level.ALL);
		}
	}

}
