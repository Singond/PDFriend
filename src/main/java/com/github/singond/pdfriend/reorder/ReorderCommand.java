package com.github.singond.pdfriend.reorder;

import com.beust.jcommander.Parameters;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.CliCommand;
import com.github.singond.pdfriend.modules.Module;

/**
 * A command-line interface for the reordering module.
 * This handles the {@code pdfriend reorder} command.
 * @author Singon
 *
 */
@Parameters(separators="=",
		commandDescription="Reorder the pages of the source document")
@Command(name="reorder", description="Reorder the pages of the source document")
public class ReorderCommand extends CliCommand {
	@SuppressWarnings("unused")
	private static ExtendedLogger logger = Log.logger(ReorderCommand.class);

	@Option(names="--reverse",
			description="Reverse the order of pages in the document")
	private boolean reverse = false;

	private int sectionLength = -1;

	@Spec
	private CommandSpec cmdspec;

	// TODO: Enable specifying a list of numbers
	@Option(names="--compact",
			description="Reorders pages to minimize breaks")
	public void setSectionLength(int n) {
		if (n > 0) {
			this.sectionLength = n;
		} else {
			throw new ParameterException(cmdspec.commandLine(),
					"The argument is not a positive integer: --compact=" + n);
		}
	}

	@Override
	public Module getModule() {
		ReorderTask task;
		boolean compact = sectionLength > 0;
		if (reverse && compact) {
			// TODO: Enable setting both
			task = null;
		} else if (reverse) {
			task = new Reverse();
		} else if (compact) {
			task = new Compact(sectionLength);
		} else {
			task = null;
		}
		ReorderModule reordering = new ReorderModule();
		reordering.setTask(task);
		return reordering;
	}

}
