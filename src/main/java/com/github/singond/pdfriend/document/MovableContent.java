package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;

/**
 * A transformable wrapper for a Content object.
 * Enables bypassing new instance creation if many transformations
 * are to be applied to an immutable Content object in sequence.
 * This is accomplished by accumulating the transformations in
 * a separate field and then applying them all at once.
 *
 * @author Singon
 */
final class MovableContent {

	private final Content content;

	/**
	 * The current position of {@code content}.
	 * This is the transformation matrix representing the position of
	 * this Content element if it were built at this moment using the
	 * <code>transformed</code> method.
	 * The matrix is initialized with the current transformation and
	 * serves to accumulate transformations to the Content element.
	 */
	private final AffineTransform transform;

	public MovableContent(Content value) {
		content = value;
		transform = new AffineTransform(content.getPosition());
	}

	/**
	 * Provides direct access to the current position.
	 * Changes to the returned object will be reflected in the Content's
	 * position.
	 * @return the internal transformation matrix
	 */
	public AffineTransform getTransform() {
		return transform;
	}

	/**
	 * Gets the transformed Content object.
	 * @return
	 */
	public Content transformed() {
		return content.atPosition(transform);
	}
}