package com.github.singond.pdfriend.io;

/**
 * Represents data input.
 * @author Singon
 */
public interface Input {
	
	/** Returns the input as a byte array */
	public byte[] getBytes() throws InputException;
	
	/** Invite an InputVisitor object */
	public <T, P, E extends Throwable> T invite (InputVisitor visitor, P param) throws E;
}
