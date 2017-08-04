package com.github.singond.pdfriend.geometry;

/**
 * Represents a value of length with units.
 * Instances of this class are immutable.
 * @author Singon
 *
 */
public final class Length {
	/** The numeric value of the length */
	private final double value;
	/** The unit of the numeric value */
	private final LengthUnit unit;
	
	/**
	 * Constructs a new object representing the given length.
	 * @param value the number
	 * @param unit the unit
	 */
	public Length(double value, LengthUnit unit) {
		this.value = value;
		this.unit = unit;
	}
	
	/**
	 * Returns the unit this length is expressed in.
	 */
	public LengthUnit unit() {
		return unit;
	}
	
	/**
	 * Converts this length into the given units.
	 * @param toUnit the unit to convert into
	 * @return this length expressed in terms of {@code otherUnit}
	 */
	public double in(LengthUnit toUnit) {
		return value * unit.inMetres()/toUnit.inMetres();
	}
	
	@Override
	public String toString() {
		return value + unit.symbol();
	}
	
	public String toString(LengthUnit u) {
		return in(u) + u.symbol();
	}
}
