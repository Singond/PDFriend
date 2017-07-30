package com.github.singond.pdfriend.modules;

/**
 * An exception signifying an error during module operation.
 * @author Singon
 *
 */
public class ModuleException extends Exception {

	private static final long serialVersionUID = -2909204466624598226L;
	
	public ModuleException() {
		super();
	};
	
	public ModuleException(String message) {
		super(message);
	}
	
	public ModuleException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ModuleException(Throwable cause) {
		super(cause);
	}

}
