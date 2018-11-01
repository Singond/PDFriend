package com.github.singond.pdfriend.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.util.Formatting;

/**
 * Represents an output document.
 * Pages in this document are indexed from 1.
 * This is a part of the uniform document interface shared between modules.
 * If the used implementation of Content is immutable, this document
 * itself is immutable.
 *
 * @author Singon
 */
public final class VirtualDocument implements Iterable<VirtualPage> {

	/**
	 * The list of pages comprising this document.
	 * Note that the document pages are numbered from one, therefore their
	 * numbers do not correspond to their indices in this internal list!
	 */
	private final List<VirtualPage> pages;

	/**
	 * An optional name of the document to be used e.g. in logs.
	 * Allowed to be null.
	 */
	private final String name;

	private static ExtendedLogger logger = Log.logger(VirtualDocument.class);

	private static final int CONCAT_TO_STRING_LIMIT = 4;

	/**
	 * Constructs a new named document composed of the given pages.
	 *
	 * @param pages the pages of the document to be created
	 * @param name the name to be given to the document
	 */
	public VirtualDocument(List<VirtualPage> pages, String name) {
		this.pages = new ArrayList<>(pages);
		this.name = name;
	}

	/**
	 * Constructs a new document composed of the given pages.
	 *
	 * @param pages the pages of the document to be created
	 */
	public VirtualDocument(List<VirtualPage> pages) {
		this(pages, null);
	}


	/**
	 * Returns a list of all pages in this document.
	 * Note that the document pages are numbered from one, therefore their
	 * numbers do not correspond to their indices in this internal list!
	 *
	 * @return A defensive copy of the list of pages.
	 */
	public List<VirtualPage> getPages() {
		return new ArrayList<>(pages);
	}

	/**
	 * Returns a specific page of the document.
	 *
	 * @param index The number of the page, starting from number one.
	 * @return The document page.
	 */
	public VirtualPage getPage(int index) {
		return pages.get(index-1);
	}

	/**
	 * Returns the number of pages in this document.
	 *
	 * @return the number of pages in this document
	 */
	public int getLength() {
		return pages.size();
	}

	/**
	 * Returns the dimensions of the minimal rectangle into which all pages
	 * of this document can fit.
	 *
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

	/**
	 * Joins several virtual document into one in the order they are given.
	 *
	 * @param docs a list of virtual documents to be joined, listed in the
	 *        order they should appear in the output
	 * @return a new instance of VirtualDocument containing all input
	 *         documents merged into one
	 */
	public static VirtualDocument concatenate(List<VirtualDocument> docs) {
		if (logger.isDebugEnabled())
			logger.debug("vdoc_concatenating", docs.size());
		final List<VirtualPage> pages = new ArrayList<>();
		for (VirtualDocument doc : docs) {
			pages.addAll(doc.getPages());
		}
		return new VirtualDocument(pages, Formatting.listDigest(
				docs, CONCAT_TO_STRING_LIMIT).toString());
	}

	/**
	 * Joins several virtual document into one in the order they are given.
	 *
	 * @param docs virtual documents to be joined, listed in the order they
	 *        should appear in the output
	 * @return a new instance of VirtualDocument containing all input
	 *         documents merged into one
	 */
	public static VirtualDocument concatenate(VirtualDocument... docs) {
		if (logger.isDebugEnabled())
			logger.debug("vdoc_concatenating", docs.length);
		final List<VirtualPage> pages = new ArrayList<>();
		for (VirtualDocument doc : docs) {
			pages.addAll(doc.getPages());
		}
		return new VirtualDocument(pages, Formatting.listDigest(
				Arrays.asList(docs), CONCAT_TO_STRING_LIMIT).toString());
	}

	/**
	 * Returns the dimensions of the minimal rectangle into which any page
	 * of the given documents can fit.
	 *
	 * @return the pair of dimensions [width, height]
	 */
	public static double[] maxPageDimensions(List<VirtualDocument> docs) {
		double[] max = new double[2];
		for (VirtualDocument doc : docs) {
			double[] docSize = doc.maxPageDimensions();
			if (docSize[0] > max[0])
				max[0] = docSize[0];
			if (docSize[1] > max[1])
				max[1] = docSize[1];
		}
		return max;
	}

	/**
	 * Returns the total number of pages in the given documents.
	 *
	 * @param docs the total number of pages in the given documents
	 * @return the sum of lengths of the given documents
	 */
	public static int totalLength(List<VirtualDocument> docs) {
		int count = 0;
		for (VirtualDocument doc : docs) {
			count += doc.getLength();
		}
		return count;
	}

	/**
	 * Returns the length of the longest document from the list.
	 *
	 * @param docs the list of document to be searched
	 * @return the length of the longest document
	 */
	public static int maxLength(List<VirtualDocument> docs) {
		int max = 0;
		for (VirtualDocument doc : docs) {
			int length = doc.getLength();
			if (length > max)
				max = length;
		}
		return max;
	}

	/**
	 * Returns the optional name of this virtual document.
	 *
	 * @return the name of this document (if any), or {@code null}
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns a short description of this document's contents.
	 *
	 * @return the names of this document's contents
	 */
	public String contentToString() {
		return Formatting.listDigest(pages, 4).toString();
	}

	@Override
	public String toString() {
		if (name != null) {
			return name;
		} else {
			return contentToString();
		}
	}

	public ListIterator<VirtualPage> iterator(int index) {
		// TODO Any other way to provide non-mutating iterator?
		return getPages().listIterator(index);
	}

	@Override
	public ListIterator<VirtualPage> iterator() {
		return iterator(0);
	}

	/**
	 * Mutable class for constructing {@code VirtualDocument} objects
	 * easily and incrementally.
	 */
	public static class Builder {

		/** The list of pages in the future document. */
		private List<VirtualPage.Builder> pages;

		/** The internal name of the future document. */
		private String name;

		/**
		 * Constructs an empty builder.
		 */
		public Builder() {
			pages = new ArrayList<>();
		};

		/**
		 * Constructs a new builder initialized from an existing
		 * document object.
		 *
		 * @param doc the document whose pages are to be copied to this builder
		 */
		public Builder(VirtualDocument doc) {
			pages = new ArrayList<>(doc.pages.size());
			for (VirtualPage pg : doc.pages) {
				pages.add(new VirtualPage.Builder(pg));
			}
		}

		/**
		 * Adds a page at the specified position in the document,
		 * shifting any subsequent pages right.
		 *
		 * @param page the page to be added
		 * @param index page number in the document, starting from number 1
		 */
		public void addPage(VirtualPage page, int index) {
			pages.add(index-1, new VirtualPage.Builder(page));
		}

		/**
		 * Adds a page builder at the specified position in the document,
		 * shifting any subsequent pages right.
		 *
		 * @param page the page builder to be added
		 * @param index page number in the document, starting from number 1
		 */
		public void addPage(VirtualPage.Builder page, int index) {
			pages.add(index-1, page);
		}

		/**
		 * Adds a page to the end of the document.
		 *
		 * @param page the page to be added
		 */
		public void addPage(VirtualPage page) {
			pages.add(new VirtualPage.Builder(page));
		}

		/**
		 * Adds a page builder to the end of the document.
		 *
		 * @param page the page builder to be added
		 */
		public void addPage(VirtualPage.Builder page) {
			pages.add(page);
		}

		/**
		 * Appends all pages of the given document into this document
		 * in the order they appear in {@code doc}.
		 *
		 * @param doc the document whose pages are to be added
		 */
		public void appendDocument(VirtualDocument doc) {
			for (VirtualPage p : doc) {
				pages.add(new VirtualPage.Builder(p));
			}
		}

		/**
		 * Appends all pages of the given document builder into this document
		 * in the order they appear in {@code doc}.
		 *
		 * @param doc the document builder whose pages are to be added
		 */
		public void appendDocument(VirtualDocument.Builder doc) {
			pages.addAll(doc.pages);
		}

		/**
		 * Removes the first occurence of the given page from the list.
		 *
		 * @param page the page to be removed
		 */
		public void removePage(VirtualPage.Builder page) {
			pages.remove(page);
		}

		/**
		 * Provides direct access to the internal list of page builders.
		 * Note that the document pages are numbered from one, therefore their
		 * numbers do not correspond to their indices in this internal list!
		 *
		 * @return the internal list itself
		 */
		public List<VirtualPage.Builder> getPages() {
			return pages;
		}

		/**
		 * Sets the name of the document.
		 *
		 * @param name name to be given to the document
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Creates a new Document instance from this builder.
		 */
		public VirtualDocument build() {
			if (logger.isDebugEnabled())
				logger.debug("Building virtual document wiht {} pages", pages.size());
			return new VirtualDocument(pages.stream()
					.map(VirtualPage.Builder::build)
					.collect(Collectors.toList()), name);
		}
	}
}
