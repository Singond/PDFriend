package com.github.singond.pdfriend.book;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A generalization of any paged medium which treats pages differently
 * depending on whether they are placed on the "front" side of the medium
 * or the "back" side.
 * A typical example is a sheet of paper printed on both sides.
 *
 * @author Singon
 */
public interface TwoSidedBook extends Book {

	/**
	 * Renders this book into a new VirtualDocument.
	 *
	 * @param flip the spatial relationship between the front and back side
	 * @return a new VirtualDocument instance
	 */
	public VirtualDocument renderTwoSided(FlipDirection flip);
}
