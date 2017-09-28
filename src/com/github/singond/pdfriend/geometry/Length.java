package com.github.singond.pdfriend.geometry;

/**
 * Represents length in the geometric sense, ie. a single spatial dimension.
 * Instances of this class are immutable.
 * 
 * @author Singon
 *
 */
public final class Length implements Comparable<Length> {
	/** The value of this length normalized to reference units */
	private final double value;
	/** The reference length unit used for the internal representation */
	private static final LengthUnit REFERENCE_UNIT = LengthUnits.METRE;
	
	/**
	 * Constructs a new object representing the given length.
	 * @param value the number
	 * @param unit the unit
	 */
	public Length(double value, LengthUnit unit) {
		this.value = convertUnits(value, unit, REFERENCE_UNIT);
	}
	
	/**
	 * Constructs a new object representing a zero length.
	 */
	public Length() {
		this.value = 0;
	}
	
	/**
	 * Constructs a new object of the given value in the reference units.
	 * @param value the value in reference units
	 */
	private Length(double value) {
		this(value, REFERENCE_UNIT);
	}
	
	/**
	 * Converts this length into the given units.
	 * @param unit the unit to convert into
	 * @return this length expressed in terms of {@code otherUnit}
	 */
	public double in(LengthUnit unit) {
		return convertUnits(value, REFERENCE_UNIT, unit);
	}
	
	/**
	 * Converts a length between units.
	 * @param value the numeric value of the length
	 * @param fromUnit the unit of the length
	 * @param toUnit the unit to convert into
	 * @return the length of {@code value [fromUnit]} expressed
	 *         in terms of {@code otherUnit}
	 */
	private static double convertUnits(double value, LengthUnit fromUnit, LengthUnit toUnit) {
		if (fromUnit == toUnit) {
			return value;
		} else {
			return value * fromUnit.inMetres()/toUnit.inMetres();
		}
	}
	
	/**
	 * Returns a product of this length and a given scalar
	 * @param factor the number to multiply this length by
	 * @return a {@code Length} instance whose value is {@code factor}
	 *         times larger than the value of this instance
	 */
	public Length times(double factor) {
		return new Length(value*factor);
	}
	
	/**
	 * Returns a sum of the given lengths.
	 * @param lengths
	 * @return a {@code Length} instance whose value is the sum of {@code lengths}
	 */
	public static Length sum(Length... lengths) {
		double sum = 0;
		for (Length l : lengths) {
			sum += l.value;
		}
		return new Length(sum);
	}
	
	/**
	 * Returns the difference of the given two lengths.
	 * @param lengthOne the length to be subtracted from (minuend)
	 * @param lengthTwo the length to be subtracted (subtrahend). This must be
	 *        less than or equal to {@code lengthOne}.
	 * @return a {@code Length} instance whose value is equal to
	 *         {@code lengthOne - lengthTwo}
	 */
	public static Length subtract(Length lengthOne, Length lengthTwo) {
		if (lengthOne.compareTo(lengthTwo) < 0) {
			throw new ArithmeticException
					("The subtrahend is larger than the minuend");
		}
		return new Length(lengthOne.value - lengthTwo.value);
	}
	
	/**
	 * Returns the product of the given length and a positive number.
	 * @param length the length to be multiplied
	 * @param scalar the factor to multiply length by
	 * @return a {@code Length} instance whose value is equal to
	 * {@code length * scalar}
	 */
	public static Length multiply(Length length, double scalar) {
		if (scalar <= 0)
			throw new ArithmeticException
					("Cannot multiply length by a negative number");
		return new Length(length.value * scalar);
	}
	
	/**
	 * Returns the quotient of the given length and a positive number.
	 * @param length the length to be multiplied
	 * @param scalar the factor to multiply length by
	 * @return a {@code Length} instance whose value is equal to
	 * {@code length / scalar}
	 */
	public static Length divide(Length length, double scalar) {
		if (scalar <= 0)
			throw new ArithmeticException
					("Cannot divide length by a negative number");
		return new Length(length.value / scalar);
	}
	
	@Override
	public int hashCode() {
		long temp = Double.doubleToLongBits(value);
		return 31 + (int) (temp ^ (temp >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Length)) return false;
		Length other = (Length) obj;
		return (Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value));
	}

	@Override
	public String toString() {
		return value + REFERENCE_UNIT.symbol();
	}
	
	@Override
	public int compareTo(Length other) {
		return Double.compare(value, other.value);
	}

	public String toString(LengthUnit u) {
		return in(u) + u.symbol();
	}
}
