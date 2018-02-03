package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * An imposition task which can process a list of input documents into
 * an output document.
 *
 * @author Singon
 */
interface ImpositionTask {

	/**
	 * Returns a name of this imposition task.
	 * This should be a short and simple description to be used internally
	 * and for logging, not a pretty name meant for end-users.
	 */
	public String getName();
	
	/**
	 * Imposes the given multiple source documents into a new virtual document.
	 * @param sources the list of documents to be imposed
	 * @return the document resulting from imposing all the input documents
	 */
	public VirtualDocument process(List<VirtualDocument> sources);
}
