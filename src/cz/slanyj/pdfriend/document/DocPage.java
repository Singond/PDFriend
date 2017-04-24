package cz.slanyj.pdfriend.document;

import java.util.Collection;
import java.util.HashSet;

/**
 * A page in the output document.
 * This is a part of the uniform document interface shared between modules.
 * @author Singon
 *
 */
public class DocPage {

	/** The width of the output. */
	private final double width;
	/** The height of the output. */
	private final double height;
	/** A collection of all pages along with their positions. */
	private final Collection<Content> content;


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
	public DocPage(double width, double height, Collection<Content> content) {
		this.width = width;
		this.height = height;
		this.content = new HashSet<>(content);
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
	public Collection<Content> getPages() {
		return new HashSet<>(content);
	}
}
