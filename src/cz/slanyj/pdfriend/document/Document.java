package cz.slanyj.pdfriend.document;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an output document.
 * This is a part of the uniform document interface shared between modules.
 * @author Singon
 *
 */
public abstract class Document {

	private final List<DocPage> pages;
	
	
	public Document() {
		pages = new ArrayList<>();
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
}
