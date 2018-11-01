package com.github.singond.pdfriend.book;

import java.awt.geom.AffineTransform;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.TransformableContents;
import com.github.singond.pdfriend.document.VirtualPage;

/**
 * A page of a document, ie. one side of a Leaf.
 * @author Singon
 *
 */
public abstract class Page implements BookElement {

	/** The page number in the bound document, numbering from page 1. */
	private int number = -1;
	/** The page width (x-direction) */
	private final double width;
	/** The page height (y-direction) */
	private final double height;

	private static ExtendedLogger logger = Log.logger(Page.class);

	public Page(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	/**
	 * Returns the page number of this page.
	 * @return
	 * @throws IllegalStateException if the page number has not been set yet.
	 */
	public int getNumber() {
		if (number < 1) {
			throw new IllegalStateException("Page number has not been set for this page yet");
		}
		return number;
	}

	/**
	 * Sets the page number of this page.
	 * @param n
	 * @throws IllegalArgumentException if the page number is lower than 1.
	 */
	public void setNumber(int n) {
		if (n < 1) {
			throw new IllegalArgumentException
				(toString()+": Page number must be greater than one");
		}
		number = n;
	}

	/**
	 * Indicates that this page can be considered blank.
	 * <p>
	 * This flag indicates that this page may be safely skipped in rendering
	 * without affecting the visible result. It should be noted that this
	 * method in general does <strong>not</strong> provide complete
	 * protection against null values.
	 * </p>
	 * @return {@code true} if skipping the page in rendering would not
	 *         make any visible changes to the output
	 */
	public abstract boolean isBlank();


	/**
	 * Returns the content of this page collected from its VirtualPage(s)
	 * as a container of transformable pieces of content.
	 * <p>This is the main interface for retrieving this Page's content.
	 * It intentionally returns a container of Content instead of
	 * a VirtualPage, because the representation of content as VirtualPages
	 * should remain an implementation detail. This is to enable subclasses
	 * use more than one VirtualPage.</p>
	 * @return The collection of Content obtained from the source page.
	 */
	public abstract TransformableContents getContents();

	/**
	 * Renders this page directly into a new virtual page.
	 * This method places this page onto the virutal page without any
	 * transformation. It ignores any notion of leaves, sheets or signatures
	 * ans as such is only useful for simple imposition tasks which can be
	 * handled by the Page subclasses themselves.
	 * @return a new VirtualPage object representing this page
	 */
	public VirtualPage render() {
		if (logger.isDebugEnabled()) logger.debug("page_rendering", this);
		/** Front side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		paper.setWidth(width);
		paper.setHeight(height);

		if (!isBlank()) {
			paper.addContent(getContents());
		}
		return paper.build();
	}

	/**
	 * Renders this page directly into a new virtual page.
	 * This method places this page onto the virutal page without any
	 * transformation except for simple rotation by a multiple of 90 degrees.
	 * It ignores any notion of leaves, sheets or signatures
	 * ans as such is only useful for simple imposition tasks which can be
	 * handled by the Page subclasses themselves.
	 *
	 * @param rotation the rotation of the page
	 * @return a new VirtualPage object representing this page,
	 *         rotated by {@code rotation}
	 */
	public VirtualPage render(Rotation rotation) {
		if (logger.isDebugEnabled())
			logger.debug("page_renderingRotated", this, rotation);
		/** Front side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		final double outputWidth, outputHeight;
		switch (rotation) {
			case UPRIGHT:
			case UPSIDE_DOWN:
				outputWidth = width;
				outputHeight = height;
				break;
			case LEFT:
			case RIGHT:
				outputWidth = height;
				outputHeight = width;
				break;
			default:
				throw new AssertionError(rotation);
		}
		paper.setWidth(outputWidth);
		paper.setHeight(outputHeight);

		if (!isBlank()) {
			TransformableContents contents = getContents();
			AffineTransform transform = new AffineTransform();
			transform.translate(outputWidth/2, outputHeight/2);
			transform.concatenate(rotation.getTransformation());
			transform.translate(-width/2, -height/2);
			contents.transform(transform);
			paper.addContent(contents);
		}
		return paper.build();
	}

	/**
	 * Invites a PageVisitor.
	 * @param <R> Return type of the visitor.
	 * @param <P> Parameter type for the vistor.
	 * @param <E> Exception type thrown by the visitor.
	 */
	public abstract <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E;

	/**
	 * Question mark in the output means that page number has not yet
	 * been set for this Page.
	 */
	@Override
	public String toString() {
		if (number < 1) {
			return "Page ?";
		} else {
			return "Page "+number;
		}
	}

	enum Rotation {
		UPRIGHT(0),
		LEFT(1),
		UPSIDE_DOWN(2),
		RIGHT(3);

		private final int quadrants;

		private Rotation(int quadrants) {
			this.quadrants = quadrants;
		}

		AffineTransform getTransformation() {
			return AffineTransform.getQuadrantRotateInstance(quadrants);
		}

		@Override
		public String toString() {
			return name().toLowerCase().replace('_', ' ');
		}
	}
}
