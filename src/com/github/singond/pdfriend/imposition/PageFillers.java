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
	 * @param pagesIterator an iterator of pages to be filled with content
	 * @param sourceIterator the iterator of the pages to be passed to {@code pages}
	 */
	public static void fillSequentially(Iterator<? extends Page> pagesIterator,
	                                    Iterator<VirtualPage> sourceIterator) {
		while (pagesIterator.hasNext() && sourceIterator.hasNext()) {
			Page p = pagesIterator.next();
			p.invite(sequentialSourceSetter, sourceIterator);
		}
	}
	
	/**
	 * Feeds input pages from the source one by one to the given blank pages.
	 * @param pages an iterable of pages to be filled with content
	 * @param source the pages to be passed to {@code pages}
	 */
	public static void fillSequentially(Iterable<? extends Page> pages,
	                                    Iterable<VirtualPage> source) {
		fillSequentially(pages.iterator(), source.iterator());
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
