package com.github.singond.pdfriend.geometry;

/**
 * A width and height of a rectangle aligned with x-and y-axis.
 *
 * @author Singon
 *
 */
public class Dimensions {
	private final double width;
	private final double height;
	
	/**
	 * Constructs a new object representing the pair of dimensions given.
	 * @param width
	 * @param height
	 */
	public Dimensions(double width, double height) {
		if (width < 0) {
			throw new IllegalArgumentException("Width must not be a negative number");
		}
		if (height < 0) {
			throw new IllegalArgumentException("Heiight must not be a negative number");
		}
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the width.
	 */
	public double width() {
		return width;
	}

	/**
	 * Returns the height.
	 */
	public double height() {
		return height;
	}
}
