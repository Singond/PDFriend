package com.github.singond.pdfriend.pipe;

interface PipeOutput {

	/**
	 * Consumes a unit of pipe data.
	 * @throws PipeException
	 */
	void consumePipeData(PipeData data) throws PipeException;
}
