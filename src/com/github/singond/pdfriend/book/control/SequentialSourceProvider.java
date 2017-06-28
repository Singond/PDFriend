package com.github.singond.pdfriend.book.control;

import java.util.LinkedList;
import java.util.Queue;

import com.github.singond.pdfriend.NoException;
import com.github.singond.pdfriend.book.model.GridPage;
import com.github.singond.pdfriend.book.model.MultiPage;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.book.model.SinglePage;
import com.github.singond.pdfriend.book.model.MultiPage.Pagelet;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;

/**
 * Assigns VirtualPages to Page objects by visiting Pages in their order,
 * putting VirtualPages into them sequentially.
 * 
 * @author Singon
 *
 */
public class SequentialSourceProvider implements SourceProvider<Page> {

	private final Queue<VirtualPage> sourcePages;
	
	public SequentialSourceProvider(VirtualDocument doc) {
		sourcePages = new LinkedList<>(doc.getPages());
	}
	
	/**
	 * Uses the given queue of pages directly without creating new object.
	 * @param pages
	 */
	SequentialSourceProvider(Queue<VirtualPage> pages) {
		sourcePages = pages;
	}

	@Override
	public void setSourceTo(Iterable<Page> pages) {
		for (Page p : pages) {
			p.invite(sourceSetter, null);
		}
	}
	
	@Override
	public void setSourceTo(Page page) {
		page.invite(sourceSetter, null);
	}
	
	/** A PageVisitor which sets source to a page. */
	private final PageVisitor<Void, Void, NoException> sourceSetter
			= new PageVisitor<Void, Void, NoException>() {
	
		@Override
		public Void visit(SinglePage p, Void param) throws NoException {
			p.setSource(sourcePages.poll());
			return null;
		}

		/**
		 * Fills the pagelets in multipage in their order of insertion.
		 */
		@Override
		public Void visit(MultiPage p, Void param) throws NoException {
			throw new UnsupportedOperationException(
					"MultiPage cannot be used directly, please choose one of its subclasses");
		}

		/**
		 * Fills the pagelets in a grid page in its preferred order.
		 */
		@Override
		public Void visit(GridPage p, Void param) throws NoException {
			for (Pagelet pg : p.pagelets()) {
				pg.setSource(sourcePages.poll());
			}
			return null;
		}
	};
}
