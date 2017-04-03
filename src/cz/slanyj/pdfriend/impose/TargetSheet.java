package cz.slanyj.pdfriend.impose;

import java.util.Collection;
import java.util.HashSet;

/**
 * An object representing a Sheet in the output.
 * Contains all information necessary for rendering the sheet in the
 * output document.
 * @author Singon
 *
 */
public class TargetSheet {

	/** The width of the output. */
	private final double width;
	/** The height of the output. */
	private final double height;
	/** A collection of all pages along with their positions. */
	private final Collection<TargetPage> pages;


	/**
	 * Creates a new instance of TargetSheet with the given dimensions
	 * and content.
	 * The content (given as a collection of pages), is defensively copied
	 * into an internal collection. 
	 * @param width The width of the output sheet.
	 * @param height The height of the output sheet.
	 * @param pages The content of the sheet, ie. a collection of pages
	 * along with their positions.
	 */
	public TargetSheet(double width, double height, Collection<TargetPage> pages) {
		this.width = width;
		this.height = height;
		this.pages = new HashSet<>(pages);
	}


	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	/**
	 * Returns the content of the sheet as a collection of all imposed pages.
	 * @return A shallow copy of the internal collection of target pages.
	 */
	public Collection<TargetPage> getPages() {
		return new HashSet<>(pages);
	}
}
