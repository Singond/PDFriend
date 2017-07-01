package com.github.singond.pdfriend.io;

/**
 * Represents a single piece of data input.
 * @author Singon
 */
public interface InputElement {
	
	/** Returns the input as a byte array */
	public byte[] getBytes() throws InputException;
	
	/** Invite an InputVisitor object */
	public <T, P> T invite (InputVisitor<T, P> visitor, P param) throws InputException;
}
