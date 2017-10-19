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
	 * @param pages the list of pages which are to be returned
	 * @param repeatPage how many times to repeat each page before
	 *        proceeding to next page
	 * @param repeatDoc how many times to repeat the whole document
	 */
	private PageSource(List<VirtualPage> pages, int repeatPage, int repeatDoc) {
		if (pages == null)
			throw new IllegalArgumentException("The source of pages is null");
		if (repeatPage < 1)
			throw new IllegalArgumentException(
					"The number of page repetitions must be a positive number");
		if (repeatDoc < 1)
			throw new IllegalArgumentException(
					"The number of document repetitions must be a positive number");
		
		this.pages = new ArrayList<>(pages);
		this.repeatPage = repeatPage;
		this.repeatDoc = repeatDoc;
	}
	
	static Builder of(VirtualDocument doc) {
		return new Builder(doc.getPages());
	}
	
	/**
	 * Returns the number of pages this {@code PageSource} will provide.
	 * @return the number of input pages multiplied by page repetitions
	 *         and document repetitions
	 */
	public int size() {
		return pages.size() * repeatPage * repeatDoc;
	}

	@Override
	public Iterator<VirtualPage> iterator() {
		return new PageIterator();
	}
	
	private final class PageIterator implements Iterator<VirtualPage> {
		
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
	
	static final class Builder {
		/** The queue of pages */
		private final List<VirtualPage> pages;
		/** How many times to repeat each page before proceeding to next page */
		private int repeatPage = 1;
		/** How many times to repeat the whole document before finishing */
		private int repeatDoc = 1;
		/** The first page to be used (inclusive) */
		private int startPage = -1;
		/** The last page to be used (exclusive) */
		private int endPage = -1;
		
		private Builder(List<VirtualPage> pages) {
			this.pages = pages;
		}
		
		/** Sets how many times each page should be repeated */
		Builder setPageRepeated(int repeatPage) {
			this.repeatPage = repeatPage;
			return this;
		}
		
		/** Sets how many times the whole document should be repeated */
		Builder setDocRepeated(int repeatDoc) {
			this.repeatDoc = repeatDoc;
			return this;
		}
		
		/**
		 * Selects the pages to be used.
		 * 
		 * @param from the first page to be used (inclusive)
		 * @param to the last page to be used (exclusive)
		 */
		Builder setPageRange(int from, int to) {
			if (from < 0)
				throw new IndexOutOfBoundsException(
						"The starting page must be greater than 0, but was " + from);
			if (from < 0)
				throw new IndexOutOfBoundsException(
						"The end page must be between greater than 0, but was " + from);
			if (from >= to)
				throw new IndexOutOfBoundsException(
						"The end page must come after the start page. "
						+ "Start page: " + from + ", end page: " + to);
			
			this.startPage = from;
			this.endPage = to;
			return this;
		}
		
		PageSource build() {
			List<VirtualPage> pageList = pages;
			
			// If the page range is not default (the indices are not -1),
			// use that page range
			if (startPage != -1 || endPage != -1) {
				// Resolve automatic values (if any)
				if (startPage == -1) {
					startPage = 0;
				}
				if (endPage == -1) {
					endPage = pages.size();
				}
				
				// Check validity
				if (startPage < 0 || startPage > pages.size())
					throw new IndexOutOfBoundsException(
							"The end page must be between 0 and the document length. "
							+ "Page index: " + startPage + ", document length: " + pages.size());
				if (startPage >= endPage)
					throw new IndexOutOfBoundsException(
							"The end page must come after the start page. "
							+ "Start page: " + startPage + ", end page: " + endPage);
				
				// Select the page range
				pageList = pages.subList(startPage, endPage);
			}
			
			return new PageSource(pageList, repeatPage, repeatDoc);
		}
	}
}
