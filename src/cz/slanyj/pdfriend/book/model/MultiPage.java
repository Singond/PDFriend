package cz.slanyj.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.slanyj.pdfriend.book.control.PageVisitor;
import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.Content.Movable;
import cz.slanyj.pdfriend.document.VirtualPage;

/**
 * A page of a document, ie. one side of a Leaf.
 * This page can hold multiple pages of a source document at arbitrary
 * positions. These source pages are called "pagelets".
 * This class is a skeletal implementation of multiple page and it is
 * not intended for direct use in applications.
 * @author Singon
 */
public abstract class MultiPage extends Page {

	/**
	 * Individual pages which make up this MultiPage along with their
	 * respective positions. These "pagelets" are stored in the order
	 * of their insertion.
	 */
	private final LinkedHashSet<Pagelet> pagelets;

	protected MultiPage(double width, double height) {
		super(width, height);
		pagelets = new LinkedHashSet<>();
	}

	/**
	 * Adds the given pagelet to this page.
	 * @param pagelet the pagelet to be added
	 */
	protected final void addPagelet(Pagelet pagelet) {
		pagelets.add(pagelet);
	}

	/**
	 * Removes the given pagelet from this page.
	 * @param pagelet
	 */
	protected final void removePagelet(Pagelet pagelet) {
		pagelets.remove(pagelet);
	}

	/**
	 * Returns a list of the individual pagelets comprising this MultiPage.
	 * The pagelets are listed in the order of their insertion.
	 * @return a new list of the pagelets preserving their order of insertion
	 */
	protected final List<Pagelet> getPagelets() {
		return new ArrayList<>(pagelets);
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
	public static class Pagelet {
		private VirtualPage source;
		private final AffineTransform position;

		/**
		 * Constructs a new Pagelet with empty source
		 * at the given position in the parent MultiPage.
		 * @param position
		 */
		public Pagelet(AffineTransform position) {
			this.position = position;
		}

		/**
		 * Constructs a new Pagelet representing the given page (source)
		 * at the given position in the parent MultiPage.
		 * @param source
		 * @param position
		 */
		public Pagelet(VirtualPage source, AffineTransform position) {
			this.source = source;
			this.position = position;
		}

		/**
		 * Sets the source VirtualPage for this Pagelet.
		 * @param source
		 */
		public void setSource(VirtualPage source) {
			this.source = source;
		}
	}
}
