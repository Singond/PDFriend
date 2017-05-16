package cz.slanyj.pdfriend.book.model;

import java.util.Collection;
import cz.slanyj.pdfriend.book.control.PageVisitor;
import cz.slanyj.pdfriend.document.Content;
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
	 * {@inheritDoc}
	 */
	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
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
