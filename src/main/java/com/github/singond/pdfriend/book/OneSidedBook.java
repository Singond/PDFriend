package com.github.singond.pdfriend.book;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A generalization of any paged medium which does not distinguish
 * pages into odd and even.
 *
 * @author Singon
 *
 */
public interface OneSidedBook extends Book {

	/**
	 * Renders this book into a new VirtualDocument.
	 * @return a new VirtualDocument instance
	 */
	public VirtualDocument renderOneSided();
}
