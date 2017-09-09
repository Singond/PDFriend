package com.github.singond.pdfriend.book;

import java.awt.geom.AffineTransform;

/**
 * A general concept of flip direction, which represents the mutual position
 * of the front side and back side of a medium (leaf or sheet). Taking the
 * leaf as an example, this means the position of the back page with respect
 * to the front page.
 * 
 * Exactly speaking, it provides the transformation matrix necessary to
 * bring an image (ie. page or side of a sheet) from its centered position
 * on a front surface of the medium to a position it would appear in if it
 * was centered on the back side and viewed from the front side of a
 * transparent medium.
 * 
 * @author Singon
 */
public enum FlipDirection {
	/** Flipped around x-axis */
	AROUND_X(1, -1),
	/** Flipped around y-axis */
	AROUND_Y(-1, 1);
	
	/** The transformation matrix */
	private final AffineTransform backOrientation;
	
	private FlipDirection(double xScale, double yScale) {
		backOrientation = AffineTransform.getScaleInstance(xScale, yScale);
	}
	
	/**
	 * Returns the transformation matrix necessary to bring an image from
	 * its centered position on a front surface of the medium to a position
	 * it would appear in if it was centered on the back side and viewed
	 * from the front side of a transparent medium.
	 */
	public AffineTransform getBackOrientation() {
		return backOrientation;
	}
}