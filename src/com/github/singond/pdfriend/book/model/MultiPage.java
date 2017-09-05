package com.github.singond.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.singond.geometry.plane.RectangleFrame;
import com.github.singond.pdfriend.book.control.PageVisitor;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.document.Contents;


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

	/**
	 * {@inheritDoc}
	 * @return {@code true} if there are no pagelets on this MultiPage
	 */
	@Override
	public boolean isBlank() {
		return pagelets.isEmpty();
	}
	
	@Override
	public Contents getContents() {
		Set<Contents> contents = new HashSet<>();
		for (Pagelet p : pagelets) {
			if (p.getSource() == null) continue;
			Contents c = p.getSource().getContents();
			c.transform(p.getPosition());
			contents.add(c);
//			for (Content.Movable cm : p.source.getContents().get()) {
//				cm.getTransform().preConcatenate(p.getPositionInPage());
//				contents.add(cm);
//			}
		}
		return Contents.of(contents);
	}

	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}

	/**
	 * A frame representing a single source page along with its position
	 * on the parent page (the {@code MultiPage}).
	 * This frame has well-defined dimensions and position on the parent page.
	 * @param <T> the concrete subtype of Pagelet
	 */
	protected interface Pagelet {

		/**
		 * A copy method.
		 * Constructs a new Pagelet which is a copy of this Pagelet.
		 * The copy must be of the same concrete type as this object.
		 * @return a new Pagelet of the same type as this pagelet
		 */
		public Pagelet copy();

		/**
		 * Returns the width of this pagelet.
		 */
		public double getWidth();

		/**
		 * Returns the height of this pagelet.
		 */
		public double getHeight();

		/**
		 * Returns the content source of this pagelet.
		 * @return the {@code VirtualPage} set as the content source of
		 *         this pagelet
		 */
		public VirtualPage getSource();
		
		/**
		 * Sets the given VirtualPage as the content source for this Pagelet.
		 * @param source the page to be used as the source
		 */
		public void setSource(VirtualPage source);

		/**
		 * Returns the position of the source page on the parent Page.
		 * This is the transformation needed to transform the page from
		 * its initial position with lower left corner in [0, 0] to the
		 * desired position specified by this pagelet.
		 */
		public AffineTransform getPosition();
	}
	
	/** A skeletal implementation of a pagelet */
	protected static abstract class APagelet implements Pagelet {
		/** Width of this pagelet */
		final double width;
		/** Height of this pagelet */
		final double height;
		/**
		 * The position of the pagelet frame on the page.
		 * Specifically, this is the transformation needed to bring
		 * a rectangle with its two vertices at [0, 0] and [width, height]
		 * to the desired position on the page.
		 * This may <strong>not</strong> be the final position of the source page,
		 * if the subclass chooses to transform the page further.
		 */
		final AffineTransform position;
		/** The source page */
		VirtualPage source;
		
		/**
		 * Constructs a new Pagelet with empty source
		 * at the given position in the parent MultiPage.
		 * @param position
		 */
		APagelet(double width, double height, AffineTransform position) {
			this.width = width;
			this.height = height;
			this.position = position;
		}
		
		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		@Override
		public VirtualPage getSource() {
			return source;
		}

		@Override
		public void setSource(VirtualPage source) {
			this.source = source;
		}
		
		/**
		 * This method simply returns a defensive copy of the internal
		 * matrix of the pagelet position.
		 */
		@Override
		public AffineTransform getPosition() {
			return new AffineTransform(position);
		}
	}
	
	/**
	 * A Pagelet implementation which further transforms the source pages
	 * relative to the pagelet's frame using the given constraints.
	 * This pagelet enables further positioning of the source page relative
	 * to itself, like resizing to fit or centering. These transformations
	 * are carried out internally by the pagelet according to constraints
	 * given by calling methods on this pagelet.
	 */
	protected static class AutoPagelet extends APagelet implements Pagelet {
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
		public AutoPagelet(double width, double height, AffineTransform position) {
			super(width, height, position);
			this.positioner = new RectangleFrame(width, height);
		}
		
		/**
		 * A copy constructor.
		 * Constructs a new AutoPagelet which is a copy of the given AutoPagelet.
		 * The copy is a shallow one, ie. the source page reference is copied
		 * to the new Pagelet.
		 * @param original the pagelet to be copied
		 * @return a new instance of {@code AutoPagelet}
		 */
		@Override
		public AutoPagelet copy() {
			AutoPagelet copy = new AutoPagelet(width, height, position);
			copy.source = this.source;
			return copy;
		}

		/**
		 * Returns the position of this pagelet frame on the page.
		 * This is <strong>not</strong> the final position of the source page,
		 * see {@link #getPosition}.
		 * @return a copy of the internal transformation matrix
		 */
		public AffineTransform getFramePosition() {
			return new AffineTransform(position);
		}
		
		/**
		 * Returns the position of the source page on the parent Page.
		 * The position of the source page is based on the pagelet position
		 * (see {@link #getFramePosition}), but further transformed with
		 * respect to the pagelet.
		 * This method returns the value calculated from the current
		 * pagelet position and position of the source page inside the pagelet,
		 * recalculating it only when the prerequisite values have changed.
		 * @return a copy of the internal transformation matrix
		 */
		@Override
		public AffineTransform getPosition() {
			if (source == null) {
				throw new IllegalStateException(
						"No source page has been set for this pagelet yet");
			}
			if (positionValid) {
				return new AffineTransform(positionInPage);
			} else {
				AffineTransform result = positioner.positionRectangle(
						source.getWidth(), source.getHeight());
				result.preConcatenate(position);
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
		
		/**
		 * Rotates the source page by the given angle.
		 * @param angle the angle of rotation in counter-clockwise direction
		 *        in radians
		 */
		public void rotatePage(double angle) {
			positioner.setRotation(angle);
			positionValid = false;
		}
	}
	
	/**
	 * A simple implementation of Pagelet where the source page becomes
	 * the pagelet, effectively erasing the distinction.
	 * In this pagelet the source page position is the same as the pagelet
	 * position.
	 */
	protected static class SimplePagelet extends APagelet implements Pagelet {

		public SimplePagelet(double width, double height, AffineTransform position) {
			super(width, height, position);
		}
		
		@Override
		public SimplePagelet copy() {
			return new SimplePagelet(width, height, position);
		}

		@Override
		public AffineTransform getPosition() {
			return super.getPosition();
		}
		
	}
}
