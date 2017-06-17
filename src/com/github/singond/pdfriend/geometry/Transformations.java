package com.github.singond.pdfriend.geometry;

import java.awt.geom.AffineTransform;

/**
 * A utility class for basic transformations.
 * @author Singon
 *
 */
public abstract class Transformations {

	/**
	 * Returns a new AffineTransform representing transformation by axial
	 * symmetry in the given axis.
	 * @param axis The axis of symmetry.
	 * @return A new AffineTransform object representing the symmetry.
	 */
	public static AffineTransform mirror(Line axis) {
		if (axis.isParallelToX()) {
			return mirrorParallelToX(axis.getYIntercept());
		} else if (axis.isParallelToY()) {
			return mirrorParallelToY(axis.getXIntercept());
		} else {
			return mirrorGeneral(axis);
		}
	}
	
	/**
	 * Returns a new AffineTransform representing transformation by axial
	 * symmetry in the given axis.
	 * @param axis The axis of symmetry.
	 * @return A new AffineTransform object.
	 */
	private static AffineTransform mirrorGeneral(Line axis) {
		double x = axis.getXIntercept();
		double y = axis.getYIntercept();
		double xx = Math.pow(x, 2);
		double yy = Math.pow(y, 2);
		// Elements of the transformation matrix
		double t11 = (xx - yy)/(xx + yy);
		double t12 = (-2*x*y)/(xx + yy);
		double t13 = x*(1 + (yy - xx)/(xx + yy));
		double t21 = t12;
		double t22 = (yy - xx)/(xx + yy);
		double t23 = (2*xx*y)/(xx + yy);
		return new AffineTransform(t11, t21, t12, t22, t13, t23);
	}
	
	/**
	 * Returns a new AffineTransform representing transformation by axial
	 * symmetry in the given axis parallel to x.
	 * @param yTrace The y-coordinate of the axis of symmetry.
	 * @return A new AffineTransform object.
	 */
	private static AffineTransform mirrorParallelToX(double yTrace) {
		return new AffineTransform(1, 0, 0, -1, 0, 2*yTrace);
	}
	
	/**
	 * Returns a new AffineTransform representing transformation by axial
	 * symmetry in the given axis parallel to y.
	 * @param xTrace The x-coordinate of the axis of symmetry.
	 * @return A new AffineTransform object.
	 */
	private static AffineTransform mirrorParallelToY(double xTrace) {
		return new AffineTransform(-1, 0, 0, 1, 2*xTrace, 0);
	}
}
