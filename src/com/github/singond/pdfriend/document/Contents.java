package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the whole content in a document page.
 * The view is not live, ie. adding or removing content elements
 * and changes to their position will not be visible in the page.
 * Other changes to the content elements (if allowed by the implementation),
 * like modifying their underlying data, may or may not result in changes
 * in the page, depending on the implementation of {@code Content}.
 * <p>
 * This is a part of the uniform document interface shared between modules.
 * This class is not thread-safe.
 * @author Singon
 *
 */
public abstract class Contents {
	
	/**
	 * Returns all pieces of content represented by this object.
	 * @return a shallow copy of the internal collection of content elements.
	 *         The returned collection can be empty, but will not be null.
	 */
	public abstract Collection<Content> get();
	
	/**
	 * Transforms all contents using the given transformation.
	 * If the original transformation matrix is T, this method moves each
	 * content element to the position given by matrix T2, such that T2 is
	 * the product of {@code transform} and T. In mathematical formulation:
	 * <pre>[T2] = [trans] x [T]</pre>
	 * @param transform the transformation matrix to be applied on top of
	 *        the current transformation in each content element
	 */
	public abstract void transform(AffineTransform transform);
	
	/**
	 * Gets the collection of all content elements wrapped in movable wrapper.
	 * @return
	 */
	abstract Collection<Content.Movable> getMovable();
	
	/**
	 * Returns a new instance of Contents containing all content elements
	 * given in the argument. The returned object allows transformations.
	 * @param contents
	 * @return a new instance of Contents
	 */
	public static Contents of(Collection<Contents> contents) {
		Set<Content.Movable> all = new HashSet<>();
		for (Contents c : contents) {
			all.addAll(c.getMovable());
		}
		return new ContentsMovable(all);
	}
	
	public abstract boolean isEmpty();
}
