package cz.slanyj.pdfriend.book;

import java.util.Collection;
import java.util.List;

import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;

/**
 * A page of a document, ie. one side of a Leaf.
 * This is the simplest type of Page which corresponds to one page of
 * the virtual document.
 * @author Singon
 *
 */
public class Page {

	/** The page number in the bound document, numbering from page 1. */
	private int number = -1;
	/** The page width (x-direction) */
	private final double width;
	/** The page height (y-direction) */
	private final double height; 
	
	/** The page of a virtual document represented by this Page object. */
	private VirtualPage source;
	
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
	
	public VirtualPage getSource() {
		return source;
	}

	/**
	 * Sets the page of a virtual source document as the content ("source")
	 * of this Page directly.
	 * @param page
	 */
	public void setSource(VirtualPage page) {
		this.source = page;
	}
	/**
	 * Sets the page of a virtual source document as the content ("source")
	 * of this Page.
	 * This variant selects the source page from the given page list using
	 * this Page's page number, assuming the pages in the list are sorted
	 * in ascending order with page of list index 0 having page number 1.
	 * This assumes the page number has already been set for this Page.
	 * @param pagesList A list of source pages sorted in ascending order
	 * starting with page number one. Note that while page numbers are
	 * indexed from one, the indices in the list are standard zero-based,
	 * ie. page 1 is placed at index 0 in the list.
	 * @throws IllegalStateException if the page number has not been set yet.
	 */
	public void setSourceFrom(List<VirtualPage> pagesList) {
		this.source = pagesList.get(getNumber()-1);
	}
	/**
	 * Sets the page of a virtual source document as the content ("source")
	 * of this Page.
	 * This variant selects the source page from the given document using
	 * this Page's page number, assuming the pages in the document are
	 * numbered from one.
	 * This assumes the page number has already been set for this Page.
	 * @param document A virtual source document with page number from one.
	 * Note that while pages in a VirtualDocument are indexed from one,
	 * the indices in the list are standard zero-based, ie. page 1 is placed
	 * at index 0 in the list.
	 * @throws IllegalStateException if the page number has not been set yet.
	 */
	public void setSourceFrom(VirtualDocument document) {
		this.source = document.getPage(getNumber());
	}
	
	/**
	 * Returns the content of this page collected from its VirtualPage(s)
	 * as a collection of transformable pieces of content.
	 * <p>This is the main interface for retrieveing this Page's content.
	 * It intentionally returns a collection of Content instead of
	 * a VirtualPage, because the representation of content as VirtualPages
	 * should remain an implementation detail. This is to enable subclasses
	 * use more than one VirtualPage.</p> 
	 * @return The collection of Content obtained from the source page.
	 */
	public Collection<Content.Movable> getContent() {
		return source.getMovableContent();
	}
	
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
