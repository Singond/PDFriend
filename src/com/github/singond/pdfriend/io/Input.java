package com.github.singond.pdfriend.io;

public interface Input {

	/**
	 * Returns the next piece of binary data (be it a file, a stream etc.).
	 * @throws InputException 
	 */
	public byte[] next() throws InputException;
	
	/** Checks whether there is more data to be returned. */
	public boolean hasNext();
}
