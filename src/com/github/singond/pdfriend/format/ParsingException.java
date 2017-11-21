package com.github.singond.pdfriend.format;

public class ParsingException extends Exception {
	
	private static final long serialVersionUID = -2318967003570281216L;

	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ParsingException(Throwable cause) {
		super(cause);
	}
}
