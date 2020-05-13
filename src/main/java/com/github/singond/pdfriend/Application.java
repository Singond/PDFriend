package com.github.singond.pdfriend;

import com.github.singond.pdfriend.cli.Console;

/**
 * The main class; this represents a single instance of PDFriend.
 */
public class Application {

	private static ExtendedLogger logger = Log.logger("Main");

	/**
	 * Runs this instance of PDFriend.
	 */
	private void execute(String[] args) {
		long start = System.currentTimeMillis();
		ExitStatus status = new Console().execute(args);
		if (status == ExitStatus.SUCCESS) {
			long end = System.currentTimeMillis();
			logger.info("total_time", end-start);
		}
		System.exit(status.exitCode());
	}

	/**
	 * Runs PDFriend.
	 */
	public static void main(String[] args) {
		new Application().execute(args);
	}
}
