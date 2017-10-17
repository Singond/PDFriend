package com.github.singond.pdfriend.imposition;

import java.util.Iterator;

import com.github.singond.pdfriend.NoException;
import com.github.singond.pdfriend.book.GridPage;
import com.github.singond.pdfriend.book.LayeredPage;
import com.github.singond.pdfriend.book.MultiPage;
import com.github.singond.pdfriend.book.MultiPage.PageletView;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.PageVisitor;
import com.github.singond.pdfriend.book.SinglePage;
import com.github.singond.pdfriend.document.VirtualPage;

/**
 * A utility class for assigning source pages to document pages.
 * 
 * @author Singon
 *
 */
final class PageFillers {

	private PageFillers() {}
	
	/**
	 * Feeds input pages from the source one by one to the given blank pages.
	 * @param pages an iterable of pages to be filled with content
	 * @param source the pages to be fed to {@code pages}
	 */
	public static void fillSequentially(Iterable<? extends Page> pages,
	                                    Iterable<VirtualPage> source) {
		Iterator<VirtualPage> sourceIterator = source.iterator();
		for (Page p : pages) {
			if (sourceIterator.hasNext())
				p.invite(sequentialSourceSetter, sourceIterator);
			else break;
		}
	}

	/**
	 * A PageVisitor which sets source to a page by simply filling the
	 * available page slots in order.
	 */
	private static final PageVisitor<Void, Iterator<VirtualPage>, NoException> sequentialSourceSetter
			= new PageVisitor<Void, Iterator<VirtualPage>, NoException>() {
	
		@Override
		public Void visit(SinglePage p, Iterator<VirtualPage> param) throws NoException {
			p.setSource(param.next());
			return null;
		}

		/**
		 * Fills the pagelets in multipage in their order of insertion.
		 */
		@Override
		public Void visit(MultiPage p, Iterator<VirtualPage> param) throws NoException {
			throw new UnsupportedOperationException(
					"MultiPage cannot be used directly, please choose one of its subclasses");
		}

		/**
		 * Fills the pagelets in a grid page in its preferred order.
		 */
		@Override
		public Void visit(GridPage p, Iterator<VirtualPage> param) throws NoException {
			for (PageletView pg : p.pagelets()) {
				if (param.hasNext())
					pg.setSource(param.next());
			}
			return null;
		}

		@Override
		public Void visit(LayeredPage p, Iterator<VirtualPage> param) throws NoException {
			throw new UnsupportedOperationException(
					"This SourceProvider does not accept LayeredPages");
		}
	};
}
