package com.github.singond.pdfriend.book.control;

import java.util.Iterator;

import com.github.singond.pdfriend.book.model.Leaf;
import com.github.singond.pdfriend.book.model.Page;

/**
 * A utility class for the book object model package
 * (com.github.singond.pdfriend.book).
 *
 * @author Singon
 *
 */
public abstract class BookUtils {

	/**
	 * Wraps a Leaf iterator to iterate through the Leaves' Pages.
	 * This assumes that each leaf has both recto and verso page set
	 * (ie. none of the two is null).
	 * Iterates through the pages in the order of the Leaves with recto
	 * pages coming right before verso pages from the same Leaf.
	 * @return A new Iterator object starting at the first Page.
	 */
	public static Iterator<Page> pageIterator(final Iterator<Leaf> leafIterator) {
		return new Iterator<Page>() {
			/** The current Leaf object */
			private Leaf currentLeaf;
			/** The last page returned was recto */
			private boolean isRecto;

			@Override
			public boolean hasNext() {
				return leafIterator.hasNext() || isRecto;
			}

			@Override
			public Page next() {
				if (isRecto) {
					isRecto = false;
					return currentLeaf.getVerso();
				} else {
					Leaf next = leafIterator.next();
					currentLeaf = next;
					isRecto = true;
					return next.getRecto();
				}
			}
		};
	}
}
