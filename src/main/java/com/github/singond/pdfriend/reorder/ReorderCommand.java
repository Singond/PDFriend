package com.github.singond.pdfriend.reorder;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.SubCommand;
import com.github.singond.pdfriend.modules.Module;

/**
 * A command-line interface for the reordering module.
 * This handles the {@code pdfriend reorder} command.
 * @author Singon
 *
 */
@Parameters(separators="=",
		commandDescription="Reorder the pages of the source document")
public class ReorderCommand extends SubCommand {
	@SuppressWarnings("unused")
	private static ExtendedLogger logger = Log.logger(ReorderCommand.class);
	
	/** The imposition task */
	@ParametersDelegate
	private ReorderableResolver reorderable = new ReorderableResolver();

	@Override
	public ReorderCommand newInstance() {
		return new ReorderCommand();
	}
	
	@Override
	protected void postParseSpecific() throws ParameterConsistencyException {
		reorderable.postParse();
	}
	
	@Override
	public Module getModule() {
		Reordering reordering = new Reordering();
		Reorderable task = reorderable.getReorderingTask();
		reordering.setTask(task);
		return reordering;
	}
	
}
