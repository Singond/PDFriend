package com.github.singond.pdfriend.reorder;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

public interface ReorderTask {

	/**
	 * Returns a name of this reordering task.
	 * This should be a short and simple description to be used internally
	 * and for logging, not a pretty name meant for end-users.
	 */
	public String getName();

	/**
	 * Reorders the pages of the given source documents and places
	 * them into a new virtual document.
	 *
	 * @param sources the document whose pages are to be reordered
	 * @return the output document
	 */
	public VirtualDocument reorder(List<VirtualDocument> sources);
}
