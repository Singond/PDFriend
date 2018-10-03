package com.github.singond.pdfriend.io;

/**
 * Represents a single piece of data input.
 * @author Singon
 */
public interface InputElement {
	
	/** Returns the input as a byte array */
	public byte[] getBytes() throws Exception;
}
