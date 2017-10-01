package com.github.singond.pdfriend.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A simple document consisting of a sequence of loose pages with no
 * relationship in between them apart from their order.
 *
 * @author Singon
 *
 */
public class LoosePages implements Book {

	/** The pages of this document */
	private final List<Page> pages;

	/**
	 * Constructs a new document consisting of the given pages.
	 * @param pages
	 */
	public LoosePages(List<Page> pages) {
		this.pages = new ArrayList<>(pages);
	}

	/**
	 * Returns the sequence of leaves in this document.
	 * @return an unmodifiable shallow copy of the internal list of pages.
	 *         Attempts to modify the returned list will fail with
	 *         {@code UnsupportedOperationException}.
	 */
	public List<Page> getPages() {
		return Collections.unmodifiableList(pages);
	}

	@Override
	public VirtualDocument renderDocument() {
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		for (Page page : pages) {
			doc.addPage(page.render());
		}
		return doc.build();
	}
}
