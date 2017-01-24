package cz.slanyj.pdfriend.book;

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
	
	public SourcePage getSource() {
		return source;
	}

	public void setSource(SourcePage page) {
		this.source = page;
	}
}
