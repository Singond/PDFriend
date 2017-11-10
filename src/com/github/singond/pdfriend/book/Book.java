package com.github.singond.pdfriend.book;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A generalization of any book-like medium, which can be rendered into
 * a {@code VirtualDocument}.
 *
 * @author Singon
 *
 */
public interface Book {

	/**
	 * Renders this Book into a new VirtualDocument.
	 * @return a new VirtualDocument instance
	 */
	public VirtualDocument renderDocument();
}
