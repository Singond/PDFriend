package cz.slanyj.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cz.slanyj.pdfriend.book.control.PageVisitor;
import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.Content.Movable;
import cz.slanyj.pdfriend.document.VirtualPage;

/**
 * A page of a document, ie. one side of a Leaf.
 * This page can hold multiple pages of a source document at arbitrary
 * positions. These source pages are called "pagelets".
 * @author Singon
 */
public class MultiPage extends Page {

	/**
	 * Individual pages which make up this MultiPage along with their
	 * respective positions. These "pagelets" are stored in the order
	 * of their insertion.
	 */
	private final LinkedHashSet<Pagelet> pagelets;

	public MultiPage(double width, double height) {
		super(width, height);
		pagelets = new LinkedHashSet<>();
	}

	/**
	 * Adds the given virtual page at the given position to this page
	 * as a new pagelet.
	 */
	public void addPage(VirtualPage page, AffineTransform position) {
		pagelets.add(new Pagelet(page, position));
	}

	/**
	 * Returns a list of the individual VirtualPages comprising this
	 * MultiPage. The pages are listed in the order of their insertion.
	 * @return A new list of the pages, sorted in the order of insertion.
	 */
	public List<VirtualPage> getPages() {
		return pagelets.stream().map(p -> p.source).collect(Collectors.toList());
	}

	@Override
	public Collection<Movable> getContent() {
		Set<Content.Movable> contents = new HashSet<>();
		for (Pagelet p : pagelets) {
			for (Content.Movable cm : p.source.getMovableContent()) {
				cm.getTransform().preConcatenate(p.position);
				contents.add(cm);
			}
		}
		return contents;
	}

	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}

	/** A source page along with its position on this MultiPage. */
	protected static class Pagelet {
		private VirtualPage source;
		private final AffineTransform position;

		/**
		 * Constructs a new Pagelet with empty source
		 * at the given position in the parent MultiPage.
		 * @param position
		 */
		protected Pagelet(AffineTransform position) {
			this.position = position;
		}

		/**
		 * Constructs a new Pagelet representing the given page (source)
		 * at the given position in the parent MultiPage.
		 * @param source
		 * @param position
		 */
		protected Pagelet(VirtualPage source, AffineTransform position) {
			this.source = source;
			this.position = position;
		}

		/**
		 * Sets the source VirtualPage for this Pagelet.
		 * @param source
		 */
		protected void setSource(VirtualPage source) {
			this.source = source;
		}
	}
}
