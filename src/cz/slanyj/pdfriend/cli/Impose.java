package cz.slanyj.pdfriend.cli;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;

/**
 * The impose command of pdfriend.
 * @author Singon
 *
 */
public abstract class Impose {
	
	private static final ExtendedLogger logger = Log.logger(Impose.class);
	
	public static void main(String[] args) {
		logger.trace("executing impose");
		// Do something
	}
}
