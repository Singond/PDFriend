package cz.slanyj.pdfriend.book;

import java.util.List;

import cz.slanyj.pdfriend.SourcePage;

/**
 * A document page, ie. one side of a Leaf.
 * @author Singon
 *
 */
public class Page {

	/** The page number in the final document */
	private int number;
	/** The page width (x-direction) */
	private final double width;
	/** The page height (y-direction) */
	private final double height; 
	
	/** The source page and its parent */
	private SourcePage source;
	
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
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Sets the page number of this page.
	 * @param n
	 */
	public void setNumber(int n) {
		number = n;
	}
	
	public SourcePage getSource() {
		return source;
	}

	/**
	 * Sets the page of a source document for this Page directly.
	 * @param page
	 */
	public void setSource(SourcePage page) {
		this.source = page;
	}
	/**
	 * Sets the page of a source document for this Page.
	 * This variant selects the source page from the given list using
	 * this Page's page number, assuming the pages in the list are sorted
	 * in ascending order starting with page number one.
	 * This assumes the page number has already been set for this Page.
	 * @param pagesList A list of source pages sorted in ascending order
	 * starting with page number one. Note that while page numbers are
	 * indexed from one, the indices in the list are standard zero-based,
	 * ie. page 1 is placed at index 0 in the list.
	 */
	public void setSource(List<SourcePage> pagesList) {
		this.source = pagesList.get(number-1);
	}
	
	@Override
	public String toString() {
		return "Page "+number;
	}
}
