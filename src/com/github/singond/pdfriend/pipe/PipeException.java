package com.github.singond.pdfriend.pipe;

/**
 * Thrown to indicate a problem with the pipe.
 *
 * @author Singon
 *
 */
public class PipeException extends Exception {

	private static final long serialVersionUID = 7973543569121762961L;

	public PipeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PipeException(Throwable cause) {
		super(cause);
	}
}
