package com.github.singond.pdfriend.pipe;

import java.io.IOException;

interface PipeInput extends AutoCloseable {

	/**
	 * Returns the next unit of pipe data.
	 * @throws PipeException
	 */
	PipeData getPipeData() throws PipeException;
	
	/** Checks whether there is unconsumed input. */
	boolean hasMore();
	
	public void close() throws IOException;
}
