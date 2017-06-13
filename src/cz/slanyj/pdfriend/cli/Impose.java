package cz.slanyj.pdfriend.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;

/**
 * The impose command of pdfriend.
 * @author Singon
 *
 */
@Parameters(commandDescription="Lay out pages of the source documents onto pages of a new document")
public class Impose implements SubCommand {
	private static final ExtendedLogger logger = Log.logger(Impose.class);

	/**
	 * The input files.
	 * All files in the list are taken as the input files, and concatenated
	 * in the order they appear in the command.
	 */
	@Parameter(description="Input files")
	private List<File> inputFiles = new ArrayList<>();
	
	/** The output file. */
	@Parameter(names={"-o", "--output"}, description="Output file name")
	private File outputFile;

	@Override
	public void execute(String[] args) {
		logger.info("PDFriend Impose");
		logger.debug("Module arguments: " + Arrays.toString(args));

		for (File f : inputFiles) {
			logger.debug("Input file: " + f.getAbsolutePath());
		}
		logger.debug("Output file: "+outputFile.getAbsolutePath());
	}
}
