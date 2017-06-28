package com.github.singond.pdfriend.io;

/**
 * Thrown to indicate a problem with the input data.
 *
 * @author Singon
 *
 */
public class OutputException extends Exception {

	private static final long serialVersionUID = -8259470374566506024L;

	public OutputException() {
		super();
	};
	
	public OutputException(String message) {
		super(message);
	}
	
	public OutputException(String message, Output output, Throwable cause) {
		super(message, cause);
	}
	
	public OutputException(Throwable cause) {
		super(cause);
	}
}
