package com.github.singond.pdfriend.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;

/**
 * A generalized representation of a page in a document.
 * This is a part of the uniform document interface shared between modules.
 * If the used implementation of Content is immutable, this VirtualPage
 * itself is immutable.
 * @author Singon
 *
 */
public class VirtualPage {

	/** The width of the output. */
	private final double width;
	/** The height of the output. */
	private final double height;
	/** A collection of all pages along with their positions. */
	private final Collection<Content> content;
	
	private static ExtendedLogger logger = Log.logger(VirtualPage.class);


	/**
	 * Creates a blank new VirtualPage with the given dimensions.
	 * @param width the width of the output sheet
	 * @param height the height of the output sheet
	 */
	public VirtualPage(double width, double height) {
		this.width = width;
		this.height = height;
		this.content = new HashSet<>();
	}
	
	/**
	 * Creates a new instance of VirtualPage with the given dimensions
	 * and content.
	 * The content (given as a collection of content elements), is
	 * defensively copied into the internal collection of content.
	 * @param width the width of the output sheet
	 * @param height the height of the output sheet
	 * @param content the content of the sheet, given as collection of
	 *        content elements
	 */
	public VirtualPage(double width, double height, Collection<Content> content) {
		this.width = width;
		this.height = height;
		this.content = new HashSet<>(content);
	}
	
	/**
	 * Creates a new instance of VirtualPage with the given dimensions
	 * and content.
	 * The piece of content is automatically put into the internal collection.
	 * @param width the width of the output sheet
	 * @param height the height of the output sheet
	 * @param content the single piece of content on the page
	 */
	public VirtualPage(double width, double height, Content content) {
		this.width = width;
		this.height = height;
		this.content = new HashSet<>();
		this.content.add(content);
	}
	
	/**
	 * A copy constructor.
	 * Creates a new instance of VirtualPagae which is a copy of the given
	 * VirtualPage, including all its content.
	 */
	public VirtualPage(VirtualPage original) {
		this.width = original.width;
		this.height = original.height;
		this.content = new HashSet<>(original.content);
	}


	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	/**
	 * Returns the content of the sheet as a collection of all content elements.
	 * @return a shallow copy of the internal collection of content elements.
	 *         The returned collection can be empty, but will not be null.
	 */
	public Collection<Content> getContent() {
		return new HashSet<>(content);
	}
	
	/**
	 * Returns the content of the sheet as a collection of all content
	 * elements, wrapped in a builder object to facilitate transforming.
	 * @return a shallow copy of the internal collection of content
	 *         elements, each converted to a new Content.Movable
	 */
	public Collection<Content.Movable> getMovableContent() {
		return content.stream()
		              .map(c -> c.new Movable())
		              .collect(Collectors.toSet());
	}
	
	/**
	 * Indicates that this page can be considered blank.
	 * <p>
	 * This flag indicates that this page may be safely skipped in rendering
	 * without affecting the visible result.
	 * While individual pieces of content may themselves be invisible
	 * or empty, this method currently only checks whether the number of
	 * content elements is zero.
	 * </p>
	 * @return {@code true} if skipping the page in rendering would not
	 *         make any visible changes to the output
	 */
	public boolean isBlank() {
		return content.isEmpty();
	}
	
	@Override
	public String toString() {
//		return "Virtualpage@"+hashCode()+" ("+content.size()+" pieces of content)";
		return "VirtualPage@"+hashCode();
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
		public Builder(VirtualPage page) {
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

		/**
		 * Overwrites the content of this to-be page with the given list
		 * of content elements.
		 * <p><b>Warning:</b> This removes all previously set content
		 * in this page!</p>
		 * @param content
		 */
		public void setContent(List<Content> content) {
			if (!this.content.isEmpty()) {
				logger.warn("vpage_overwritingContent", Builder.this);
			}
			this.content = content;
		}

		/**
		 * Adds a single piece of content.
		 * @param content
		 */
		public void addContent(Content content) {
			this.content.add(content);
		}
		
		/**
		 * Builds the VirtualPage.
		 * @return A new instance of VirtualPage.
		 */
		public VirtualPage build() {
			logger.debug("vpage_building",
			             width, height, content.size());
			return new VirtualPage(width, height, content);
		}
	}
}
