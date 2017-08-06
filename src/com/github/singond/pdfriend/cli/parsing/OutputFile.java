package com.github.singond.pdfriend.cli.parsing;

import java.io.File;
import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.io.Output;
import com.github.singond.pdfriend.io.OutputFactory;

/**
 * The command-line argument for the output file.
 * @author Singon
 */
public class OutputFile implements ParameterDelegate {
	
	private static ExtendedLogger logger = Log.logger(OutputFile.class);

	/** The output file. */
	@Parameter(names={"-o", "--output"}, description="Output file name")
	private File outputFile;
	
	@Override
	public void postParse() {
		if (outputFile != null)
			logger.verbose("The output file:" + outputFile.getAbsolutePath());
	}
	
	/**
	 * Returns the output file wrapped in one Output object.
	 */
	public Output getOutput() {
		return OutputFactory.of(outputFile.toPath());
	}

	/** Gets the output file */
	public File getFile() {
		return outputFile;
	}
}
