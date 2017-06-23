package com.github.singond.pdfriend.cli;

import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.cli.parsing.InputFiles;

public abstract class SubCommand {

	/**
	 * The input files.
	 * All files in the list are taken as the input files, and concatenated
	 * in the order they appear in the command.
	 */
	@ParametersDelegate
	private InputFiles inputFiles = new InputFiles();

	public InputFiles getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(InputFiles inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	/**
	 * Method to be invoked after the SubCommand object is built
	 * and initialized with values from the command line.
	 */
	public abstract void postParse();
	
	/**
	 * Returns a PDFriend module which performs the task represented
	 * by this subcommand.
	 */
	public abstract Module getModule();
}
