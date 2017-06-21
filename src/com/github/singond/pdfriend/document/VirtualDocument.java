package com.github.singond.pdfriend.document;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;

/**
 * Represents an output document.
 * Pages in this document are indexed from 1.
 * This is a part of the uniform document interface shared between modules.
 * If the used implementation of Content is immutable, this document
 * itself is immutable.
 * @author Singon
 *
 */
public class VirtualDocument {

	/**
	 * The list of pages comprising this document.
	 * Note that the document pages are numbered from one, therefore their
	 * numbers do not correspond to their indices in this internal list!
	 */
	private final List<VirtualPage> pages;
	
	private static ExtendedLogger logger = Log.logger(VirtualDocument.class);
	
	
	/**
	 * Constructs a new document composed of the given pages.
	 * @param pages
	 */
	public VirtualDocument(List<VirtualPage> pages) {
		this.pages = new ArrayList<>(pages);
	}
	
	
	/**
	 * Returns a list of all pages in this document.
	 * Note that the document pages are numbered from one, therefore their
	 * numbers do not correspond to their indices in this internal list!
	 * @return A defensive copy of the list of pages.
	 */
	public List<VirtualPage> getPages() {
		return new ArrayList<>(pages);
	}

	/**
	 * Returns a specific page of the document.
	 * @param index The number of the page, starting from number one.
	 * @return The document page.
	 */
	public VirtualPage getPage(int index) {
		return pages.get(index-1);
	}

	/**
	 * Returns the number of pages in this document.
	 * @return
	 */
	public int getLength() {
		return pages.size();
	}
	
	/**
	 * Returns the dimensions of the minimal rectangle into which all pages
	 * of this document can fit.
	 * @return the pair of dimensions [width, height]
	 */
	public double[] maxPageDimensions() {
		double width = 0;
		double height = 0;
		for (VirtualPage p : pages) {
			double w = p.getWidth();
			if (w > width) {
				width = w;
			}
			double h = p.getHeight();
			if (h > height) {
				height = h;
			}
		}
		return new double[]{width, height};
	}
	
	@Override
	public String toString() {
		return "VirtualDocument@"+hashCode()+" ("+pages.size()+" pages)";
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
		 * @param index Page number in the document, starting from number 1.
		 */
		public void addPage(VirtualPage page, int index) {
			pages.add(index-1, page);
		}
		
		/**
		 * Adds a page to the end of the document.
		 */
		public void addPage(VirtualPage page) {
			pages.add(page);
		}
		
		/**
		 * Removes the first occurence of the given page from the list.
		 */
		public void removePage(VirtualPage page) {
			pages.remove(page);
		}
		
		/**
		 * Provides direct access to the internal list of pages.
		 * Note that the document pages are numbered from one, therefore their
		 * numbers do not correspond to their indices in this internal list!
		 * @return The internal list itself.
		 */
		public List<VirtualPage> getPages() {
			return pages;
		}
		
		/**
		 * Creates a new Document instance from this builder.
		 */
		public VirtualDocument build() {
			logger.verbose("Building VirtualDocument {} pages long", pages.size());
			return new VirtualDocument(pages);
		}
	}
}
