package com.github.singond.pdfriend.geometry;

/** The basic length units */
public enum LengthUnits implements LengthUnit {
	/** The SI metre */
	METRE(1, "m", "metre"),
	/** The SI decimetre, ie 1/10 of a metre */
	DECIMETRE(1E-1, "dm", "decimetre"),
	/** The SI centimetre, ie 1/100 of a metre */
	CENTIMETRE(1E-2, "cm", "centimetre"),
	/** The SI millimetre, ie 1/1000 of a metre */
	MILLIMETRE(1E-3, "mm", "millimetre"),
	/** The international yard ie. 3 international feet */
	YARD(0.9144, "yd", "yard"),
	/** The international foot, ie. 12 international inches */
	FOOT(0.3048, "ft", "foot", "feet"),
	/** The international inch */
	INCH(0.0254, "in", "inch", "inches"),
	/** The desktop publishing pica, ie. 12 PostScript points */
	PICA_POSTSCRIPT(0.0254/6, "pc", "pica"),
	/** The desktop publishing point (DTP point, PostScript point), ie. 1/72 of the international inch */
	POINT_POSTSCRIPT(0.0254/72, "pt", "point"),
	/** The TeX point, defined as 800/803 PostScript points */
	POINT_TEX((0.0254/72)*800/803, "pt_tex", "point");
	
	/** The reference value of this length unit in metres */
	private final double value;
	/** The textual symbol for this unit */
	private final String symbol;
	/** The English name of the unit in singular */
	private final String nameSingular;
	/** The English name of the unit in plural */
	private final String namePlural;
	
	/**
	 * @param valueInMetres the value of the new length unit in metres
	 * @param symbol the textual symbol for this unit
	 * @param nameSg the English name of the unit in singular
	 * @param namePl the English name of the unit in plural
	 */
	private LengthUnits(double valueInMetres, String symbol,
	                    String nameSg, String namePl) {
		this.value = valueInMetres;
		this.symbol = symbol;
		this.nameSingular = nameSg;
		this.namePlural = namePl;
	}
	
	/**
	 * Infers the English plural name by appending "-s".
	 * @param valueInMetres the value of the new length unit in metres
	 * @param symbol the textual symbol for this unit
	 * @param nameSg the English name of the unit in singular
	 */
	private LengthUnits(double valueInMetres, String symbol, String nameSg) {
		this.value = valueInMetres;
		this.symbol = symbol;
		this.nameSingular = nameSg;
		this.namePlural = nameSg + "s";
	}

	@Override
	public double inMetres() {
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
