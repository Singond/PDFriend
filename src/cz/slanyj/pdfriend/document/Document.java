package cz.slanyj.pdfriend.document;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an output document.
 * This is a part of the uniform document interface shared between modules.
 * If the used implementation of Content is immutable, this document
 * itself is immutable.
 * @author Singon
 *
 */
public abstract class Document {

	/** The list of pages comprising this document. */
	private final List<DocPage> pages;
	/** Number of pages */
	private final int length;
	
	/**
	 * Constructs a new document composed of the given pages.
	 * @param pages
	 */
	public Document(List<DocPage> pages) {
		this.pages = new ArrayList<>(pages);
		this.length = pages.size();
	}
	

	/**
	 * Returns a list of all pages in this document.
	 * @return A defensive copy of the list of pages.
	 */
	public List<DocPage> getPages() {
		return new ArrayList<>(pages);
	}

	/**
	 * Returns a specific page of the document.
	 * @param index The number of the page, starting from zero.
	 * @return The document page.
	 */
	public DocPage getPage(int index) {
		return pages.get(index);
	}

	/**
	 * Returns the number of pages in this document.
	 * @return
	 */
	public int getLength() {
		return length;
	}
	
}
