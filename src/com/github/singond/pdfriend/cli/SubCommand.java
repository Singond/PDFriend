package com.github.singond.pdfriend.cli;

import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.cli.parsing.InputFiles;
import com.github.singond.pdfriend.cli.parsing.OutputFile;
import com.github.singond.pdfriend.modules.Module;

public abstract class SubCommand {

	/**
	 * The input files.
	 * <p>
	 * Parsing the command line requires that an InputFiles object be
	 * present in each subcommand. This field may be set to some instance
	 * known to the object which performs the parsing, so that the parsing
	 * object can later access its value directly.
	 * However, due to the limitations of JCommander, this field must not be
	 * null after initialization, which is why the field is initialized;
	 * </p>
	 */
	@ParametersDelegate
	private InputFiles inputFiles = new InputFiles();
	
	/** The output file. */
	@ParametersDelegate
	private OutputFile outputFile = new OutputFile();

	public InputFiles getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(InputFiles inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	public OutputFile getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(OutputFile outputFile) {
		this.outputFile = outputFile;
	}
	
	/**
	 * Creates a new instance of this SubCommand.
	 */
	public abstract SubCommand newInstance();
	
	/**
	 * Method to be invoked after this SubCommand object is built
	 * and initialized with values from the command line.
	 */
	public void postParse() {
		inputFiles.postParse();
		outputFile.postParse();
		postParseSpecific();
	}
	
	/**
	 * Method to be invoked after the SubCommand subclass object is built
	 * and initialized with values from the command line.
	 * This is a subclass-specific implementation which is invoked by
	 * {@code postParseDefault} in {@SubCommand}.
	 */
	protected abstract void postParseSpecific();
	
	/**
	 * Returns a PDFriend module which performs the task represented
	 * by this subcommand.
	 */
	public abstract Module getModule();
}
