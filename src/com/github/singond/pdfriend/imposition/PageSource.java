package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.util.RepeatingIterator;

/**
 * A helper objects which facilitates returning a page multiple times.
 * 
 * This is basically a list of pages which allows to set how many times
 * a page will be returned before proceeding to the next page,
 * and another number to perform similar repetition at the level of the
 * whole document.
 *
 * @author Singon
 */
class PageSource implements Iterable<VirtualPage> {

	/** The queue of pages */
	private final List<VirtualPage> pages;
	/** How many times to repeat each page before proceeding to next page */
	private final int repeatPage;
	/** How many times to repeat the whole document before finishing */
	private final int repeatDoc;
	
	/**
	 * Constructs a new PageSource object which repeats the pages of the
	 * given document.
	 * @param doc the document whose pages are to be returned
	 * @param repeatPage how many times to repeat each page before
	 *        proceeding to next page
	 * @param repeatDoc how many times to repeat the whole document
	 */
	PageSource(VirtualDocument doc, int repeatPage, int repeatDoc) {
		if (doc == null)
			throw new IllegalArgumentException("The source of pages is null");
		if (repeatPage < 1)
			throw new IllegalArgumentException(
					"The number of page repetitions must be a positive number");
		if (repeatDoc < 1)
			throw new IllegalArgumentException(
					"The number of document repetitions must be a positive number");
		
		this.pages = new ArrayList<>(doc.getPages());
		this.repeatPage = repeatPage;
		this.repeatDoc = repeatDoc;
	}

	@Override
	public Iterator<VirtualPage> iterator() {
		return new PageIterator();
	}
	
	public final class PageIterator implements Iterator<VirtualPage> {
		
		/** An object to provide page iterators */
		private final Iterator<Iterable<VirtualPage>> docIterator;
		/** The current page iterator */
		private Iterator<VirtualPage> pageIterator;
		
		private PageIterator() {
			this.docIterator = new RepeatingIterator<>(Collections.singleton(pages), repeatDoc);
			if (docIterator.hasNext())
				pageIterator = newPageIterator();
			else
				throw new AssertionError("The document iterator has no elements");
		}

		private final Iterator<VirtualPage> newPageIterator() {
			return new RepeatingIterator<>(docIterator.next(), repeatPage);
		}
		
		@Override
		public boolean hasNext() {
			return pageIterator.hasNext() || docIterator.hasNext();
		}

		@Override
		public VirtualPage next() {
			if (pageIterator.hasNext()) {
				return pageIterator.next();
			} else if (docIterator.hasNext()) {
				pageIterator = newPageIterator();
				if (pageIterator.hasNext()) {
					return pageIterator.next();
				} else {
					// This shouldn't happen
					throw new NoSuchElementException("The new page iterator has no elements");
				}
			} else {
				throw new NoSuchElementException("No more documents");
			}
		}
	}
}
