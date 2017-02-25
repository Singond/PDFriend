package cz.slanyj.pdfriend.geometry;

/**
 * Represents a straight line in 2D space. 
 * @author Singon
 *
 */
public class Line {
	
	// ax + by + c = 0
	private final double a;
	private final double b;
	private final double c;
	
	/**
	 * Constructs a new line in x-y plane by specifying the coefficients
	 * of its general parametric formula: ax + by + c = 0.
	 * @param a
	 * @param b
	 * @param c
	 */
	public Line(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * Constructs a new line in x-y plane by specifying two of its points.
	 * @param a First point
	 * @param b Second point
	 */
	public Line(Point a, Point b) {
		// The direction vector [u, v]
		final double u = b.getX() - a.getX();
		final double v = b.getY() - a.getY();
		this.a = v;
		this.b = -u;
		this.c = u*a.getY() - v*a.getX();
	}
	
	/**
	 * Determines whether the given point lies on this line.
	 * @param p The point to be tested.
	 */
	public boolean liesOnLine(Point p) {
		return (a*p.getX() + b*p.getY() + c) == 0;
	}
	
	/**
	 * Returns the x-coordinate of the point where this line crosses
	 * the x-axis (called an x-intercept).
	 * @return
	 */
	public double getXIntercept() {
		// y = 0
		return -c/a;
	}
	
	/**
	 * Returns the y-coordinate of the point where this line crosses
	 * the y-axis (called an y-intercept).
	 * @return
	 */
	public double getYIntercept() {
		// x = 0
		return -c/b;
	}
	
	/**
	 * Checks whether the two given points lie in the same half plane
	 * bounded by this line.
	 */
	public boolean onSameSide(Point p, Point q) {
		/* This is true iff the left-hand sides of the equation for
		 * each of the points evaluate to the same sign. */
		double lhsP = a*p.getX() + b*p.getY() + c;
		double lhsQ = a*q.getX() + b*q.getY() + c;
		return (lhsP > 0) && (lhsQ > 0);
	}
}
