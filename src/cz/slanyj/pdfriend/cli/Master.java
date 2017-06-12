package cz.slanyj.pdfriend.cli;

import java.util.Arrays;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.Out;
import cz.slanyj.pdfriend.Version;

/**
 * Handles the command-line operation which is not bound to any module.
 *
 * @author Singon
 *
 */
public class Master {
	private static final ExtendedLogger logger = Log.logger(Master.class);
	
	public static void execute(String[] args) {
		logger.debug("PDFriend arguments: " + Arrays.toString(args));
//		version();
	}
	
	/** Prints version info and exits */
	private static void version() {
		Out.line("This is PDFriend version %s", Version.current().toString());
		System.exit(0);
	}
}
