package com.github.singond.pdfriend.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.util.Formatting;

/**
 * A generalized representation of a page in a document.
 * This is a part of the uniform document interface shared between modules.
 * If the used implementation of Content is immutable, this VirtualPage
 * itself is immutable.
 *
 * @author Singon
 */
public final class VirtualPage {

	/** The width of the page in output. */
	private final double width;
	/** The height of the page in output. */
	private final double height;
	/** A collection of all content elements along with their positions. */
	private final Set<Content> content;

	private static final int TO_STRING_LIMIT = 4;

	private static ExtendedLogger logger = Log.logger(VirtualPage.class);


	/**
	 * Creates a blank new VirtualPage with the given dimensions.
	 *
	 * @param width the width of the output sheet
	 * @param height the height of the output sheet
	 */
	public VirtualPage(double width, double height) {
		this.width = width;
		this.height = height;
		this.content = new LinkedHashSet<>();
	}

	/**
	 * Creates a new instance of VirtualPage with the given dimensions
	 * and content.
	 * The collection of content elements is defensively copied into the
	 * internal collection of content.
	 *
	 * @param width the width of the new page
	 * @param height the height of the new page
	 * @param content the content of the sheet, given as collection of
	 *        content elements
	 */
	public VirtualPage(double width, double height, Collection<Content> content) {
		this.width = width;
		this.height = height;
		this.content = new LinkedHashSet<>(content);
	}

	/**
	 * Creates a new instance of VirtualPage with the given dimensions
	 * and content.
	 * The piece of content is automatically put into the internal collection.
	 *
	 * @param width the width of the new page
	 * @param height the height of the new page
	 * @param content the single piece of content on the page
	 */
	public VirtualPage(double width, double height, Content content) {
		this.width = width;
		this.height = height;
		this.content = new LinkedHashSet<>();
		this.content.add(content);
	}

	/**
	 * A copy constructor.
	 * Creates a new instance of VirtualPagae which is a copy of the given
	 * VirtualPage, including all of its content.
	 *
	 * @param original the page to be copied
	 */
	public VirtualPage(VirtualPage original) {
		this.width = original.width;
		this.height = original.height;
		this.content = new LinkedHashSet<>(original.content);
	}


	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	/**
	 * Returns an object representing all content elements of this page.
	 * The returned object allows transforming the content using arbitrary
	 * affine transform, but this ability is associated with some overhead.
	 * <p>
	 * In case that transformable content is not required, the object returned
	 * by {@link #getContentStatic} is expected to perform better.
	 *
	 * @return a non-live view (but see {@link Contents}) of the content
	 */
	public TransformableContents getContents() {
		return new ContentsMovable(getMovableContent());
	}

	/**
	 * Returns an object representing all content elements of this page.
	 * The returned object does not permit transformations and throws
	 * {@code UnsupportedOperationException} on invocation of the
	 * {@link Content#transform} method.
	 * <p>
	 * The object returned by this method is expected to perform better when
	 * compared to that returned by {@link #getContents}.
	 *
	 * @return a non-live view (but see {@link Contents}) of the content
	 */
	public Contents getContentStatic() {
		return new ContentsStatic(new ArrayList<>(content));
	}

	/**
	 * Returns the content of the sheet as a collection of all content
	 * elements, wrapped in a builder object to facilitate transforming.
	 *
	 * @return a shallow copy of the internal collection of content
	 *         elements, each converted to a new Content.Movable
	 */
	List<MovableContent> getMovableContent() {
		return content.stream()
		              .map(MovableContent::new)
		              .collect(Collectors.toList());
	}

	/**
	 * Indicates that this page can be considered blank.
	 * <p>
	 * This flag indicates that this page may be safely skipped in rendering
	 * without affecting the visible result.
	 * While individual pieces of content may themselves be invisible
	 * or empty, this method currently only checks whether the number of
	 * content elements is zero.
	 *
	 * @return {@code true} if skipping the page in rendering would not
	 *         make any visible changes to the output
	 */
	public boolean isBlank() {
		return content.isEmpty();
	}

	/**
	 * Returns a short description of the page.
	 * <p>
	 * The specific value may change, but is typically a list of
	 * content units in the page.
	 */
	@Override
	public String toString() {
		/*
		 * Consistency between invocations is achieved by using LinkedHashSet
		 * for contents.
		 */
		return Formatting.listDigest(content, TO_STRING_LIMIT).toString();
	}

	/**
	 * A builder class for {@code VirtualPage}.
	 * Enables specifying a new {@code VirtualPage} incrementally.
	 */
	public static class Builder {
		private double width;
		private double height;
		private List<Content> content;

		/**
		 * Constructs a new page builder with a default page size and no conent.
		 * The default size of the page is 595x842 pt, which is roughly
		 * equivalent to an A4 page. These dimensions can be changed any time.
		 */
		public Builder() {
			width = 595;
			height = 842;
			content = new ArrayList<>();
		};

		/**
		 * Constructs a new page builder initialized from an existing
		 * virtual page object.
		 *
		 * @param page the page to be copied in the new instance
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
		 *
		 * @param contents the contents to be set to this page
		 */
		public void setContent(Contents contents) {
			if (!this.content.isEmpty()) {
				logger.warn("vpage_overwritingContent", Builder.this);
			}
			this.content = new ArrayList<>(contents.get());
		}

		/**
		 * Adds a single piece of content.
		 *
		 * @param content the piece of content to be added
		 */
		public void addContent(Content content) {
			this.content.add(content);
		}

		/**
		 * Adds all given contents.
		 *
		 * @param contents the contents to be added
		 */
		public void addContent(Contents contents) {
			this.content.addAll(contents.get());
		}

		/**
		 * Builds the VirtualPage.
		 *
		 * @return a new instance of VirtualPage
		 */
		public VirtualPage build() {
			if (logger.isDebugEnabled())
				logger.debug("vpage_building", width, height,
						Formatting.listDigest(content, TO_STRING_LIMIT));
			return new VirtualPage(width, height, content);
		}
	}
}
