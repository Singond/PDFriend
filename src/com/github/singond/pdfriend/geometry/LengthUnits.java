package com.github.singond.pdfriend.geometry;

/** The basic length units */
public enum LengthUnits implements LengthUnit {
	/** The SI metre */
	METRE(1, "m"),
	/** The SI decimetre, ie 1/10 of a metre */
	DECIMETRE(1E-1, "dm"),
	/** The SI centimetre, ie 1/100 of a metre */
	CENTIMETRE(1E-2, "cm"),
	/** The SI millimetre, ie 1/1000 of a metre */
	MILLIMETRE(1E-3, "mm"),
	/** The international yard ie. 3 international feet */
	YARD(0.9144, "yd"),
	/** The international foot, ie. 12 international inches */
	FOOT(0.3048, "ft"),
	/** The international inch */
	INCH(0.0254, "in"),
	/** The desktop publishing pica, ie. 12 PostScript points */
	PICA_POSTSCRIPT(0.0254/6, "pc"),
	/** The desktop publishing point (DTP point, PostScript point), ie. 1/72 of the international inch */
	POINT_POSTSCRIPT(0.0254/72, "pt"),
	/** The TeX point, defined as 800/803 PostScript points */
	POINT_TEX((0.0254/72)*800/803, "pt_tex");
	
	/** The reference value of this length unit in metres */
	private final double value;
	/** The textual symbol for this unit */
	private final String symbol;
	
	/**
	 * @param valueInMetres the value of the new length unit in metres
	 */
	private LengthUnits(double valueInMetres, String symbol) {
		this.value = valueInMetres;
		this.symbol = symbol;
	}

	@Override
	public double inMetres() {
		return value;
	}
	
	@Override
	public String symbol() {
		return symbol;
	}
}
