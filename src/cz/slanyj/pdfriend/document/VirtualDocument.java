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
public class VirtualDocument {

	/** The list of pages comprising this document. */
	private final List<VirtualPage> pages;
	
	
	/**
	 * Constructs a new document composed of the given pages.
	 * @param pages
	 */
	public VirtualDocument(List<VirtualPage> pages) {
		this.pages = new ArrayList<>(pages);
	}
	
	
	/**
	 * Returns a list of all pages in this document.
	 * @return A defensive copy of the list of pages.
	 */
	public List<VirtualPage> getPages() {
		return new ArrayList<>(pages);
	}

	/**
	 * Returns a specific page of the document.
	 * @param index The number of the page, starting from zero.
	 * @return The document page.
	 */
	public VirtualPage getPage(int index) {
		return pages.get(index);
	}

	/**
	 * Returns the number of pages in this document.
	 * @return
	 */
	public int getLength() {
		return pages.size();
	}
	
	
	/**
	 * Mutable class for constructing Document objects easily and incrementally. 
	 * @author Singon
	 *
	 */
	public static class Builder {
		
		/** The list of pages in the future document. */
		private List<VirtualPage> pages;
		
		/**
		 * Constructs an empty Document.Builder.
		 */
		public Builder() {
			pages = new ArrayList<>();
		};
		/**
		 * Constructs a new Document.Builder initialized from an existing
		 * Document object.
		 */
		public Builder(VirtualDocument doc) {
			pages = new ArrayList<>(doc.pages);
		}
		
		/**
		 * Adds a page at the specified position in the document,
		 * shifting any subsequent pages right.
		 * @param page
		 * @param index
		 */
		public void addPage(VirtualPage page, int index) {
			pages.add(index, page);
		}
		
		/**
		 * Adds a page to the end of the document.
		 */
		public void addPage(VirtualPage page) {
			pages.add(page);
		}
		
		/**
		 * Removes the first occurenceof the given page from the list.
		 */
		public void removePage(VirtualPage page) {
			pages.remove(page);
		}
		
		/**
		 * Provides direct access to the internal list of pages.
		 * @return The internal list itself.
		 */
		public List<VirtualPage> getPages() {
			return pages;
		}
		
		/**
		 * Creates a new Document instance from this builder.
		 */
		public VirtualDocument build() {
			return new VirtualDocument(pages);
		}
	}
}
