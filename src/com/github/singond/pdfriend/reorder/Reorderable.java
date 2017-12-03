package com.github.singond.pdfriend.reorder;

import com.github.singond.pdfriend.document.VirtualDocument;

public interface Reorderable {

	/**
	 * Returns a name of this reordering task.
	 * This should be a short and simple description to be used internally
	 * and for logging, not a pretty name meant for end-users.
	 */
	public String getName();
	
	/**
	 * Reorders the pages of the single given source document and places
	 * them into a new virtual document.
	 * @param source the document whose pages are to be reordered
	 * @return the output document
	 */
	public VirtualDocument reorder(VirtualDocument source);
}
