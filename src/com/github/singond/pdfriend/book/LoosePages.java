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
public class LoosePages implements OneSidedBook, TwoSidedBook {

	/** The pages of this document */
	private final List<? extends Page> pages;

	/**
	 * Constructs a new document consisting of the given pages.
	 * @param pages
	 */
	public LoosePages(List<? extends Page> pages) {
		this.pages = new ArrayList<>(pages);
	}

	/**
	 * Returns the sequence of leaves in this document.
	 * @return an unmodifiable shallow copy of the internal list of pages.
	 *         Attempts to modify the returned list will fail with
	 *         {@code UnsupportedOperationException}.
	 */
	public List<? extends Page> getPages() {
		return Collections.unmodifiableList(pages);
	}
	
	@Override
	public VirtualDocument renderOneSided() {
		return renderAllUpright();
	}

	// TODO: Implement flip direction
	@Override
	public VirtualDocument renderTwoSided(FlipDirection flip) {
		switch (flip) {
			case AROUND_X:
				return renderWithEvenPagesRotated();
			case AROUND_Y:
				return renderAllUpright();
			default:
				throw new AssertionError(flip);
		}
	}

	private VirtualDocument renderAllUpright() {
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		for (Page page : pages) {
			doc.addPage(page.render());
		}
		return doc.build();
	}
	
	private VirtualDocument renderWithEvenPagesRotated() {
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		boolean even = false;
		for (Page page : pages) {
			if (even) {
				doc.addPage(page.render(Page.Rotation.UPSIDE_DOWN));
			} else {
				doc.addPage(page.render(Page.Rotation.UPRIGHT));
			}
			even = !even;
		}
		return doc.build();
	}
}
