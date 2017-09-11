package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A represetation of a document produced by imposition.
 * @author Singon
 */
public interface Imposable {

	/**
	 * Returns a name of this imposition task.
	 * This should be a short and simple description to be used internally
	 * and for logging, not a pretty name meant for end-users.
	 */
	public String getName();
	
	/** Imposes the given source documents into a new virtual document. */
	public VirtualDocument impose(List<VirtualDocument> sources);
}
