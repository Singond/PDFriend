package com.github.singond.pdfriend.cli.parsing;

import java.io.File;

import com.beust.jcommander.Parameter;

public class OutputFile {

	/** The output file. */
	@Parameter(names={"-o", "--output"}, description="Output file name")
	private File outputFile;

	/** Gets the output file */
	public File getFile() {
		return outputFile;
	}
}
