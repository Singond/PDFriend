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
}
