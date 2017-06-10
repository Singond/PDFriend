package cz.slanyj.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.slanyj.geometry.plane.RectangleFrame;
import cz.slanyj.pdfriend.book.control.PageVisitor;
import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.Content.Movable;
import cz.slanyj.pdfriend.document.VirtualPage;

/**
 * A page of a document, ie. one side of a Leaf.
 * This page can hold multiple pages of a source document at arbitrary
 * positions. These source pages are contained in frames called "pagelets".
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
//				cm.getTransform().preConcatenate(p.framePosition);
				cm.getTransform().preConcatenate(p.getPositionInPage());
				contents.add(cm);
			}
		}
		return contents;
	}

	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}

	/**
	 * A frame containing a single source page along with position.
	 * This frame has well-defined dimensions and position on the MultiPage
	 * and enables further positioning of the source page relative to itself.
	 */
	public static class Pagelet {
		/** Width of this pagelet */
		private final double width;
		/** Height of this pagelet */
		private final double height;
		/**
		 * The position of the pagelet frame on the page.
		 * Specifically, this is the transformation needed to bring
		 * a rectangle with its two vertices at [0, 0] and [width, height]
		 * to the desired position on the page.
		 * This is <strong>not</strong> the final position of the source page,
		 * see {@link #positionInFrame}.
		 */
		private final AffineTransform framePosition;

		/** The source page */
		private VirtualPage source;
		/** A helper object for positioning the source page in the frame */
		private final RectangleFrame positioner;
		/**
		 * The position of the source relative to the page.
		 * This is calculated by composing {@code positionInFrame} and
		 * {@code position}.
		 * This field should be calculated once and updated only when
		 * necessary.
		 */
		private AffineTransform positionInPage;
		/** Indicates that {@code positionInPage} is up to date. */
		private boolean positionValid = false;

		/**
		 * Constructs a new Pagelet with empty source
		 * at the given position in the parent MultiPage.
		 * @param position
		 */
		public Pagelet(double width, double height, AffineTransform position) {
			this.width = width;
			this.height = height;
			this.framePosition = new AffineTransform(position);
			this.positioner = new RectangleFrame(width, height);
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		/**
		 * Returns the position of this pagelet frame on the page.
		 * This is <strong>not</strong> the final position of the source page,
		 * see {@link #getPositionInPage}.
		 * @return a copy of the internal transformation matrix
		 */
		public AffineTransform getFramePosition() {
			return new AffineTransform(framePosition);
		}

		/**
		 * Sets the source VirtualPage for this Pagelet.
		 * @param source
		 */
		public void setSource(VirtualPage source) {
			this.source = source;
		}
		
		/**
		 * Returns the position of the source page on the parent page.
		 * The position of the source page is based on the pagelet position
		 * (see {@link #getPosition}), but further transformed with respect
		 * to the pagelet frame.
		 * This method returns the value calculated from the current
		 * {@code framePosition} and {@code positionInFrame}, recalculating
		 * it only when the prerequisite values have changed.
		 * @return a copy of the cached or freshly recalculated
		 *         transformation matrix
		 */
		public AffineTransform getPositionInPage() {
			if (source == null) {
				throw new IllegalStateException(
						"No source page has been set for this pagelet yet");
			}
			if (positionValid) {
				return new AffineTransform(positionInPage);
			} else {
				AffineTransform result = positioner.positionRectangle(
						source.getWidth(), source.getHeight());
				result.preConcatenate(framePosition);
				positionInPage = result;
				positionValid = true;
				return new AffineTransform(result);
			}
		}
		
		/** Makes the source page fit this frame. */
		public void fitPage() {
			positioner.setSize(positioner.new Fit());
			positionValid = false;
		}
		
		/**
		 * Scales the source page by a constant amount.
		 * @param scale the scale as magnification (values greater
		 *        than 1 increase the size of the source page)
		 */
		public void scalePage(double scale) {
			positioner.setSize(positioner.new Scale(scale));
			positionValid = false;
		}
	}
}
