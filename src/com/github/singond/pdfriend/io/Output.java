package com.github.singond.pdfriend.io;

/**
 * Represents data output.
 * @author Singon
 */
public interface Output {
	
	/** Returns the input as a byte array */
	public void acceptBytes(byte[] bytes) throws OutputException;
	
	/** Invite an InputVisitor object */
	public <T, P> T invite (OutputVisitor<T, P> visitor, P param) throws OutputException;
}
