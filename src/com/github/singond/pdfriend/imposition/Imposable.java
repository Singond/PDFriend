package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A represetation of a document produced by imposition.
 * @author Singon
 */
public interface Imposable {

	/** Renders this imposed document into a new virtual document. */
	public VirtualDocument getDocument();
}
