package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;

public interface Content {

	/**
	 * Gets the position of this piece of content as a transformation matrix.
	 * Changes to the matrix do not write through to the content piece.
	 *
	 * @return a defensive copy of the transformation matrix
	 */
	AffineTransform getPosition();

	/**
	 * Returns a piece of content which is identical to this instance
	 * transformed using the given transformation, keeping the original
	 * unchanged.
	 *
	 * If the original transformation matrix is T, this method returns
	 * content at position given by matrix T2, such that T2 is the product
	 * of {@code transform} and T. In mathematical formulation:
	 * <pre>[T2] = [trans] x [T]</pre>
	 *
	 * @param transform the transformation matrix to be applied on top of
	 *        the current transformation of this Content element
	 * @return an instance representing the transformed piece of content
	 */
	Content transform(AffineTransform transform);

	/**
	 * Returns a piece of content which is identical to this instance
	 * moved to the position specified by the given transformation matrix,
	 * keeping the original unchanged.
	 *
	 * @param position the transformation matrix to be used as the position
	 *        of the returned Content element
	 * @return the transformed piece of content
	 */
	Content atPosition(AffineTransform position);

	/**
	 * Invites a ContentVisitor.
	 *
	 * @param <T> Return type for the visitor.
	 * @param <P> Parameter type for the visitor.
	 * @param <E> Exception type thrown by the visitor.
	 */
	<T, P, E extends Throwable> T invite(ContentVisitor<T, P, E> visitor,
			P param) throws E;

}