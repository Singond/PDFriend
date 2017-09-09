package com.github.singond.pdfriend.cli;

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
