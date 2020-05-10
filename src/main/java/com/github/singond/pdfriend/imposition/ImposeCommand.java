package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import picocli.CommandLine.Command;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.SubCommandOld;
import com.github.singond.pdfriend.modules.Module;

/**
 * A command-line interface for the imposition module.
 * This handles the {@code pdfriend impose} command.
 * @author Singon
 *
 */
@Parameters(separators="=",
		commandDescription="Lay out pages of the source documents onto pages of a new document")
@Command(name="impose")
public class ImposeCommand extends SubCommandOld {
	@SuppressWarnings("unused")
	private static ExtendedLogger logger = Log.logger(ImposeCommand.class);

	/** Page pre-processing settings */
	@ParametersDelegate
	private PreprocessorSettingsCli pageOpts = new PreprocessorSettingsCli();

	/** Common imposition settings */
	@ParametersDelegate
	private CommonSettingsCli commonOpts = new CommonSettingsCli();

	/** Common imposition settings */
	@ParametersDelegate
	private RenderingSettingsCli renderOpts = new RenderingSettingsCli();

	/** The imposition task */
	@ParametersDelegate
	private ImposableResolver imposable = new ImposableResolver();

	@Override
	public ImposeCommand newInstance() {
		return new ImposeCommand();
	}

	@Override
	protected void postParseSpecific() throws ParameterConsistencyException {
		pageOpts.postParse();
		commonOpts.postParse();
		renderOpts.postParse();
		imposable.postParse();
	}

	@Override
	public Module getModule() {
		Imposition impose = new Imposition();
		ImposableBuilder<?> taskBuilder = imposable.getImpositionTask();
		taskBuilder.acceptPreprocessSettings(pageOpts.getPreprocessorSettings());
		taskBuilder.acceptCommonSettings(commonOpts.getCommonSettings());
		taskBuilder.acceptRenderingSettings(renderOpts.getRenderingSettings());
		impose.setTask(taskBuilder.buildTask());
		return impose;
	}

}
