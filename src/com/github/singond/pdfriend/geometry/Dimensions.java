package com.github.singond.pdfriend.geometry;

/**
 * A width and height of a rectangle aligned with x-and y-axis.
 *
 * @author Singon
 */
public final class Dimensions {
	private final Length width;
	private final Length height;
	@Deprecated
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
	 * @param width width in {@code unit}s
	 * @param height height in {@code unit}s
	 * @param unit the length unit of both width and height
	 */
	public Dimensions(double width, double height, LengthUnit unit) {
		if (width < 0) {
			throw new IllegalArgumentException("Width must not be a negative number");
		}
		if (height < 0) {
			throw new IllegalArgumentException("Height must not be a negative number");
		}
		this.width = new Length(width, unit);
		this.height = new Length(height, unit);
	}
	
	/**
	 * Constructs a new object representing the pair of dimensions given.
	 * @param width width in PostScript points
	 * @param height height in PostScript points
	 */
	@Deprecated
	public Dimensions(double width, double height) {
		this(width, height, dfltUnit);
	}
	
	/**
	 * Constructs a dummy object.
	 * This object is not equal to any other instance and does not have
	 * valid behaviour except for {@code equals()} and {@code hashCode()}.
	 * It is intended to be used as a placeholder, for example in the
	 * null object design pattern.
	 */
	private Dimensions() {
		width = height = null;
	}
	
	/**
	 * A factory method.
	 * Returns a Dimensions object representing the pair of dimensions given.
	 * In the current implementation, a new instance is created each time,
	 * but this is subject to change in future.
	 * @param width width in {@code unit}s
	 * @param height height in {@code unit}s
	 * @param unit the length unit of both width and height
	 * @return an instance of Dimensions representing the given dimensions
	 */
	public static final Dimensions of(double width, double height, LengthUnit unit) {
		// Reuse instances?
		return new Dimensions(width, height, unit);
	}
	
	/**
	 * A factory method.
	 * Returns a Dimensions object representing the pair of dimensions given.
	 * In the current implementation, a new instance is created each time,
	 * but this is subject to change in future.
	 * @param width the width
	 * @param height the height
	 * @return an instance of Dimensions representing the given dimensions
	 */
	public static final Dimensions of(Length width, Length height) {
		// Reuse instances?
		return new Dimensions(width, height);
	}
	
	/**
	 * Returns a new dummy {@code Dimensions} object.
	 * The returned object is always a new instance which is not equal to
	 * any other instance of this class in terms of {@code equals}
	 * and does not have any valid behaviour except for {@code equals()}
	 * and {@code hashCode()}.
	 * It is intended to be used as a placeholder, for example in the
	 * null object design pattern.
	 */
	public static final Dimensions dummy() {
		return new Dimensions();
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
	
	/**
	 * Returns a Dimensions instace representing a rectangle which has been
	 * scaled up by the given constant, ie. whose sides are a multiple
	 * of the corresponding side in this rectangle and the magnification.
	 * @param magnification the constant to scale up by
	 * @return a rectangle resulting by scaling this rectangle up by
	 *         {@code magnification}
	 */
	public Dimensions scaleUp(double magnification) {
		return new Dimensions(width.times(magnification),
		                      height.times(magnification));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!( obj instanceof Dimensions)) return false;
		Dimensions other = (Dimensions) obj;
		if (height == null) {
			if (other.height != null) return false;
		} else if (!height.equals(other.height)) return false;
		if (width == null) {
			if (other.width != null) return false;
		} else if (!width.equals(other.width)) return false;
		return true;
	}

	/**
	 * Returns a string representation of the pair of dimensions.
	 * The format is subject to change, but the following is usual:
	 * {@code width x height}
	 */
	@Override
	public String toString() {
		return width + " x " + height;
	}
	
	public String toString(LengthUnit widthUnit, LengthUnit heightUnit) {
		return width.toString(widthUnit) + " x " + height.toString(heightUnit);
	}
	
	public String toString(LengthUnit unit) {
		return toString(unit, unit);
	}
}
