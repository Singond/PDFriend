package com.github.singond.pdfriend.document;

import java.util.Collection;

/**
 * Represents the whole content in a document page.
 * This is not a live view of the contents of a page, but a separate
 * collection, ie. adding or removing content elements
 * and changes to their position will not be visible in the page.
 * Other changes to the content elements (if allowed by the implementation),
 * like modifying their underlying data, may or may not result in changes
 * in the page, depending on the implementation of {@code Content}.
 * <p>
 * This is a part of the uniform document interface shared between modules.
 * Implementations are not required to be thread-safe.
 *
 * @author Singon
 */
public interface Contents {

	/**
	 * Returns all pieces of content represented by this object.
	 *
	 * @return a shallow copy of the internal collection of content elements.
	 *         The returned collection can be empty, but can never be null.
	 */
	Collection<Content> get();

	/**
	 * Checks whether this object contains any content units.
	 *
	 * @return {@code true} if this object does not contain any units of content
	 */
	boolean isEmpty();

}