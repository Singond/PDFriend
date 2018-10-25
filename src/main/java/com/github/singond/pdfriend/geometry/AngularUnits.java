package com.github.singond.pdfriend.geometry;

import static java.lang.Math.PI;

/** The basic angular units */
public enum AngularUnits implements AngularUnit {
	/** The radian */
	RADIAN(1, "rad", "radian"),
	/** The degree of arc, ie 1/360th of a circle */
	DEGREE(PI/180, "deg", "degree"),
	/** The gradian (also called "grad" or "gon"), ie 1/400th of a circle */
	GRAD(PI/200, "gon", "gradian");
	
	/** The reference value of this angular unit in radians */
	private final double value;
	/** The textual symbol for this unit */
	private final String symbol;
	/** The English name of the unit in singular */
	private final String nameSingular;
	/** The English name of the unit in plural */
	private final String namePlural;
	
	/**
	 * @param valueInRadians the value of the new length unit in radians
	 * @param symbol the textual symbol for this unit
	 * @param nameSg the English name of the unit in singular
	 * @param namePl the English name of the unit in plural
	 */
	private AngularUnits(double valueInRadians, String symbol,
	                     String nameSg, String namePl) {
		this.value = valueInRadians;
		this.symbol = symbol;
		this.nameSingular = nameSg;
		this.namePlural = namePl;
	}
	
	/**
	 * Infers the English plural name by appending "-s".
	 * @param valueInRadians the value of the new length unit in radians
	 * @param symbol the textual symbol for this unit
	 * @param nameSg the English name of the unit in singular
	 */
	private AngularUnits(double valueInRadians, String symbol, String nameSg) {
		this.value = valueInRadians;
		this.symbol = symbol;
		this.nameSingular = nameSg;
		this.namePlural = nameSg + "s";
	}

	@Override
	public double inRadians() {
		return value;
	}
	
	@Override
	public String symbol() {
		return symbol;
	}

	@Override
	public String nameInSingular() {
		return nameSingular;
	}

	@Override
	public String nameInPlural() {
		return namePlural;
	}
}
