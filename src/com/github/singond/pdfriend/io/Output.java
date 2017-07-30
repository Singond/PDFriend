package com.github.singond.pdfriend.io;

/**
 * Represents data output.
 * @author Singon
 */
public interface Output {
	
	/** Outputs the given byte array */
	public void acceptBytes(byte[] bytes) throws OutputException;
}
