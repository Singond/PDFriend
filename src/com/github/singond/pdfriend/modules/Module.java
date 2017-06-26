package com.github.singond.pdfriend.modules;

import com.github.singond.pdfriend.document.RenderingException;

/**
 * A module of pdfriend, ie. a component which performs aa concrete operation
 * on the processed document.
 */
public interface Module {
	
	/**
	 * Process the documents with this module. This method takes as its
	 * single argument the list of documents to be processed, meaning the
	 * module must be fully initialized to perform the intended task before
	 * invoking this method.
	 * @param document the virtual documents to be processed
	 * @return the processed document as a new virtual document
	 * @throws RenderingException 
	 */
	public ModuleData process(ModuleData document) throws RenderingException;
}
