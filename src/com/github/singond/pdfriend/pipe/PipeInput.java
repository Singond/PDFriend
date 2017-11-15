package com.github.singond.pdfriend.pipe;

interface PipeInput extends AutoCloseable {

	/**
	 * Returns the next unit of pipe data.
	 * @throws PipeException
	 */
	PipeData getPipeData() throws PipeException;
	
	/** Checks whether there is unconsumed input. */
	boolean hasMore();
}
