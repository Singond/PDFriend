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
public class SinglePage extends Page {
	
	/** The page of a virtual document represented by this Page object. */
	private VirtualPage source;
	
	public SinglePage(double width, double height) {
		super(width, height);
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
//	public void setSourceFrom(List<VirtualPage> pagesList) {
//		this.source = pagesList.get(getNumber()-1);
//	}
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
//	public void setSourceFrom(VirtualDocument document) {
//		this.source = document.getPage(getNumber());
//	}
	
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
	@Override
	public Collection<Content.Movable> getContent() {
		return source.getMovableContent();
	}
	
	/**
	 * Question mark in the output means that page number has not yet
	 * been set for this Page.
	 */
	@Override
	public String toString() {
		try {	
			return "Page " + getNumber();
		} catch(IllegalStateException e) {
			return "Page ?";
		}
	}
}
