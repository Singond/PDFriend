package com.github.singond.pdfriend.geometry;

/**
 * Represents a point in 2D space.
 * @author Singon
 *
 */
public class Point {

	/** The x-coordinate */
	private final double x;
	/** The y-coordinate */
	private final double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
}
