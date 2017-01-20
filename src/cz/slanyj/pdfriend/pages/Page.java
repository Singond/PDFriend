package cz.slanyj.pdfriend.pages;

/**
 * A document page, ie. one side of a Sheet.
 * @author Sorondil
 *
 */
public class Page {

	/** The page number in the document */
	private int number;
	/** The page width (x-direction) */
	private final double width;
	/** The page height (y-direction) */
	private final double height; 
	
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
}
