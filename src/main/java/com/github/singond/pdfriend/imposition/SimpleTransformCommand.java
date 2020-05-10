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
 * A command-line interface for the simple transformation module.
 * This handles the {@code pdfriend transform} command.
 * @author Singon
 *
 */
@Parameters(separators="=",
		commandDescription="Resize and rotate pages, add margins")
@Command(name="transform")
public class SimpleTransformCommand extends SubCommandOld {
	@SuppressWarnings("unused")
	private static ExtendedLogger logger = Log.logger(SimpleTransformCommand.class);

	/** Page pre-processing settings */
	@ParametersDelegate
	private PreprocessorSettingsCli pageOpts = new PreprocessorSettingsCli();

	/** Common imposition settings */
	@ParametersDelegate
	private CommonSettingsCliSimple commonOpts = new CommonSettingsCliSimple();

	@Override
	public SimpleTransformCommand newInstance() {
		return new SimpleTransformCommand();
	}

	@Override
	protected void postParseSpecific() throws ParameterConsistencyException {
		pageOpts.postParse();
		commonOpts.postParse();
	}

	@Override
	public Module getModule() {
		SimpleTransform impose = new SimpleTransform();
		SimpleTransformTask.Builder taskBuilder = new SimpleTransformTask.Builder();
		taskBuilder.acceptPreprocessSettings(pageOpts.getPreprocessorSettings());
		taskBuilder.acceptCommonSettings(commonOpts.getCommonSettings());
		impose.setTask(taskBuilder.build());
		return impose;
	}

}
