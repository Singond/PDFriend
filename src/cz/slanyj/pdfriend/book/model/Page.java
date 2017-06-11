package cz.slanyj.pdfriend.book.model;

import java.util.Collection;

import cz.slanyj.pdfriend.book.control.PageVisitor;
import cz.slanyj.pdfriend.document.Content;

/**
 * A page of a document, ie. one side of a Leaf.
 * @author Singon
 *
 */
public abstract class Page {

	/** The page number in the bound document, numbering from page 1. */
	private int number = -1;
	/** The page width (x-direction) */
	private final double width;
	/** The page height (y-direction) */
	private final double height; 
	
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
	 * Returns the content of this page collected from its VirtualPage(s)
	 * as a collection of transformable pieces of content.
	 * <p>This is the main interface for retrieving this Page's content.
	 * It intentionally returns a collection of Content instead of
	 * a VirtualPage, because the representation of content as VirtualPages
	 * should remain an implementation detail. This is to enable subclasses
	 * use more than one VirtualPage.</p> 
	 * @return The collection of Content obtained from the source page.
	 */
	public abstract Collection<Content.Movable> getContent();
	
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