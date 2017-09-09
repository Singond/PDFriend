package com.github.singond.pdfriend.book.model;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.PageVisitor;
import com.github.singond.pdfriend.document.Contents;
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
	public abstract Contents getContents();
	
	/**
	 * Renders this page directly into a new virtual page.
	 * This method places this page onto the virutal page without any
	 * transformation. It ignores any notion of leaves, sheets or signatures
	 * ans as such is only useful for simple imposition tasks which can be
	 * handled by the Page subclasses themselves.
	 * @return a new VirtualPage object representing this page
	 */
	public VirtualPage render() {
		logger.verbose("page_rendering", this);
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
}
