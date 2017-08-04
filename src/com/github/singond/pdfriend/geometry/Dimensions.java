package com.github.singond.pdfriend.geometry;

/**
 * A width and height of a rectangle aligned with x-and y-axis.
 *
 * @author Singon
 */
public class Dimensions {
	private final Length width;
	private final Length height;
	private static final LengthUnit dfltUnit = LengthUnits.POINT_POSTSCRIPT;
	
	/**
	 * Constructs a new object representing the pair of dimensions given.
	 * @param width
	 * @param height
	 */
	public Dimensions(Length width, Length height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Constructs a new object representing the pair of dimensions given.
	 * @param width width in PS points
	 * @param height height in PS points
	 */
	public Dimensions(double width, double height) {
		if (width < 0) {
			throw new IllegalArgumentException("Width must not be a negative number");
		}
		if (height < 0) {
			throw new IllegalArgumentException("Heiight must not be a negative number");
		}
		this.width = new Length(width, dfltUnit);
		this.height = new Length(height, dfltUnit);
	}

	/**
	 * Returns the width.
	 */
	public Length width() {
		return width;
	}

	/**
	 * Returns the height.
	 */
	public Length height() {
		return height;
	}
	
	/**
	 * Returns the width.
	 */
	@Deprecated
	public double widthInPoints() {
		return width.in(dfltUnit);
	}

	/**
	 * Returns the height.
	 */
	@Deprecated
	public double heightInPoints() {
		return height.in(dfltUnit);
	}
}
