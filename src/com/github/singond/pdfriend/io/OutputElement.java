package com.github.singond.pdfriend.io;

/**
 * Represents a single piece of data output.
 * @author Singon
 */
interface OutputElement {
	
	/** Returns the input as a byte array */
	public void acceptBytes(byte[] bytes) throws OutputException;
}
