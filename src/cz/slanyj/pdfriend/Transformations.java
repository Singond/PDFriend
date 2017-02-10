package cz.slanyj.pdfriend;

import java.awt.geom.AffineTransform;

/**
 * A utility class for basic transformations.
 * @author Singon
 *
 */
public class Transformations {

	/**
	 * Returns a new AffineTransform representing transformation by axial
	 * symmetry in the given axis.
	 * @param xTrace The x-coordinate of the intersection of the axis with
	 * the x-axis.
	 * @param yTrace The y-coordinate of the intersection of the axis with
	 * the y-axis.
	 * @return A new AffineTransform object.
	 */
	public static AffineTransform mirror(double xTrace, double yTrace) {
		double x = xTrace;
		double y = yTrace;
		double xx = Math.pow(xTrace, 2);
		double yy = Math.pow(yTrace, 2);
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
	public static AffineTransform mirrorParallelToX(double yTrace) {
		return new AffineTransform(1, 0, 0, -1, 0, 2*yTrace);
	}
	
	/**
	 * Returns a new AffineTransform representing transformation by axial
	 * symmetry in the given axis parallel to y.
	 * @param xTrace The x-coordinate of the axis of symmetry.
	 * @return A new AffineTransform object.
	 */
	public static AffineTransform mirrorParallelToY(double xTrace) {
		return new AffineTransform(-1, 0, 0, 1, 2*xTrace, 0);
	}
}
