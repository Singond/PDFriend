package com.github.singond.pdfriend.cli;

/**
 * Thrown to indicate that the command-line arguments do not combine into
 * a consistent state.
 * <p>
 * The need for this exception is the fact that the individual command-line
 * parameters may represent conflicting notions, like, for example,
 * specifying both landscape and portrait orientation of a page at the same
 * time. If such a conflict is discovered, this exception is thrown.
 *
 * @author Singon
 *
 */
public class ArgumentParsingException extends Exception {
	
	private final String key;
	private static final long serialVersionUID = -2318967003570281216L;
	
	public ArgumentParsingException(String message, String key) {
		super(message);
		this.key = key;
	}
	
	public ArgumentParsingException(String message, String key, Throwable cause) {
		super(message, cause);
		this.key = key;
	}

}
