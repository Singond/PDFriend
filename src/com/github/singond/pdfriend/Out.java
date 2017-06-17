package com.github.singond.pdfriend;

/**
 * Class for standard system output.
 * As opposed to Log class, this is not intended for application
 * messages but rather for data output.
 * @author Singon
 *
 */
public class Out {

	/**
	 * Writes a single line to system standard output.
	 * The line is automatically terminated with a newline character.
	 * @param The line to be output.
	 */
	public static void line(String string) {
		System.out.println(string);
	}
	
	/**
	 * Formats a line and writes it to system standard output.
	 * The line is automatically terminated with a newline character.
	 * @param pattern Formatter pattern.
	 * @param args Arguments for the formatter pattern.
	 */
	public static void line(String pattern, Object... args) {
		System.out.println(String.format(pattern, args));
	}
}
