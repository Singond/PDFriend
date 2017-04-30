package cz.slanyj.pdfriend.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * A page in the output document.
 * This is a part of the uniform document interface shared between modules.
 * If the used implementation of Content is immutable, this DocPage
 * itself is immutable.
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
	public Collection<Content> getContent() {
		return new HashSet<>(content);
	}
	
	
	/**
	 * A builder class for DocPage.
	 * Enables building a new DocPage incrementally.
	 */
	public static class Builder {
		private double width;
		private double height;
		private List<Content> content;


		/**
		 * Constructs a new DocPage.Builder with a default page size.
		 * The default size of the page is 595x842 pt, which is roughly
		 * equivalent to an A4 page. These dimensions can be changed any time.
		 */
		public Builder() {
			width = 595;
			height = 842;
			content = new ArrayList<>();
		};
		/**
		 * Constructs a new DocPage.Builder initialized from an existing
		 * DocPage object.
		 */
		public Builder(DocPage page) {
			width = page.width;
			height = page.height;
			content = new ArrayList<>(page.content);
		}


		public double getWidth() {
			return width;
		}

		public void setWidth(double width) {
			this.width = width;
		}

		public double getHeight() {
			return height;
		}

		public void setHeight(double height) {
			this.height = height;
		}

		public List<Content> getContent() {
			return content;
		}

		public void setContent(List<Content> content) {
			this.content = content;
		}

		public void addContent(Content content) {
			this.content.add(content);
		}
	}
}
