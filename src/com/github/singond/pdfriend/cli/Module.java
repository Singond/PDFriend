package com.github.singond.pdfriend.cli;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A module of pdfriend, ie. a component which performs aa concrete operation
 * on the processed document.
 */
public interface Module {

	/**
	 * Method to be invoked after the Module object is built
	 * and initialized with values from the command line.
	 */
	public void postParse();
	
	/**
	 * Process the document with this module. This method takes as its
	 * single argument the document to be processed, meaning the module
	 * must be fully initialized to perform the intended task before
	 * invoking this method.
	 * @param document the virtual document to be processed
	 */
	public void process(VirtualDocument document);
}
