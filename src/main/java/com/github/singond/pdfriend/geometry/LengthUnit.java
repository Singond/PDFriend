package com.github.singond.pdfriend.geometry;

public interface LengthUnit {

	/**
	 * Returns the value of this unit in metres.
	 * This value is used as the reference when comparing and converting units.
	 */
	public double inMetres();
	
	/**
	 * Returns the textual symbol for the unit.
	 */
	public String symbol();
	
	/**
	 * Returns the English unit name in singular.
	 */
	public String nameInSingular();
	
	/**
	 * Returns the English unit name in plural.
	 */
	public String nameInPlural();
}
