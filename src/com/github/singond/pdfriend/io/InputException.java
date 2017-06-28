package com.github.singond.pdfriend.io;

/**
 * Thrown to indicate a problem with the input data.
 *
 * @author Singon
 *
 */
public class InputException extends Exception {

	private static final long serialVersionUID = 295097331725042528L;

	public InputException() {
		super();
	};
	
	public InputException(String message) {
		super(message);
	}
	
	public InputException(String message, Input input, Throwable cause) {
		super(message, cause);
	}
	
	public InputException(Throwable cause) {
		super(cause);
	}
}
