package cz.slanyj.pdfriend.cli;

import java.util.Arrays;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;

/**
 * The impose command of pdfriend.
 * @author Singon
 *
 */
public class Impose implements SubCommand {
	
	private static final ExtendedLogger logger = Log.logger(Impose.class);

	@Override
	public void execute(String[] args) {
		logger.debug("PDFriend Impose Module");
		logger.debug("Module arguments: " + Arrays.toString(args));
		// Do something
		
	}

	public static void main(String[] args) {
		new Impose().execute(null);
	}

}
