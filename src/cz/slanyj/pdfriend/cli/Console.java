package cz.slanyj.pdfriend.cli;

import java.util.Arrays;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.Out;
import cz.slanyj.pdfriend.Version;

/**
 * The root of the command-line interface.
 * Handles global arguments to the application and dispatches subcommands.
 *
 * @author Singon
 *
 */
public class Console {
	@Parameter(names={"-V", "--version"}, description="Print version info and exit")
	private boolean version = false;
	
	private static final ExtendedLogger logger = Log.logger(Console.class);
	
	public void execute(String[] args) {
		logger.debug("PDFriend arguments: " + Arrays.toString(args));
		
		// Parse arguments
		JCommander.newBuilder()
		          .addObject(this)
		          .acceptUnknownOptions(true)
		          .build()
		          .parse(args);
		
		// Run
		if (version) {
			version();
			System.exit(0);
		}
	}
	
	/** Prints version info and exits */
	private void version() {
		Out.line("This is PDFriend version %s", Version.current().toString());
		System.exit(0);
	}
}
