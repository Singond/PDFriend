package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;

/**
 * An extension of {@link Contents} which allows transformations.
 *
 * @author Singon
 */
public interface TransformableContents extends Contents {

	/**
	 * Transforms all contents using the given transformation.
	 * If the original transformation matrix is T, this method moves each
	 * content element to the position given by matrix T2, such that T2 is
	 * the product of {@code transform} and T. In mathematical formulation:
	 * <pre>[T2] = [trans] x [T]</pre>
	 *
	 * @param transform the transformation matrix to be applied on top of
	 *        the current transformation in each content element
	 */
	void transform(AffineTransform transform);

}