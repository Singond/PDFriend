package com.github.singond.pdfriend.geometry;

public final class Angle implements Comparable<Angle> {

	/** The reference value of this angle expressed in radians */
	private final double value;
	
	/**
	 * Constructs a new Angle object.
	 * @param value the value of the angle in radians
	 */
	public Angle(double value) {
		this.value = value;
	}
	
	public Angle(double value, AngularUnit unit) {
		this.value = value * unit.inRadians();
	}
	
	public double in(AngularUnit unit) {
		return value / unit.inRadians();
	}
	
	@Override
	public String toString() {
		return value + " rad (" + in(AngularUnits.DEGREE) + " deg)";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Angle)) {
			return false;
		} else {
			Angle a = (Angle) o;
			return Double.compare(value, a.value) == 0;
		}
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		long valueBits = Double.doubleToLongBits(value);
		int valueHash = (int) (valueBits ^ (valueBits >>> 32));
		result = 31 * result + valueHash;
		return result;
	}
	
	@Override
	public int compareTo(Angle a) {
		return new Double(value).compareTo(new Double(a.value));
	}
}
