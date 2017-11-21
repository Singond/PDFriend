package com.github.singond.pdfriend.io;

public interface Input {

	/**
	 * Returns the next piece of input data.
	 */
	public InputElement next();
	
	/** Checks whether there is more data to be returned. */
	public boolean hasNext();
}
