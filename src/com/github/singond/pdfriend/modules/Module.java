package com.github.singond.pdfriend.modules;

import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A module of pdfriend, ie. a component which performs aa concrete operation
 * on the processed document.
 */
public interface Module {
	
	/**
	 * Process the document with this module. This method takes as its
	 * single argument the document to be processed, meaning the module
	 * must be fully initialized to perform the intended task before
	 * invoking this method.
	 * @param document the virtual document to be processed
	 * @return the processed document as a new virtual document
	 * @throws RenderingException 
	 */
	public VirtualDocument process(VirtualDocument document) throws RenderingException;
}
