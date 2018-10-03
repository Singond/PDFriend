package com.github.singond.pdfriend.geometry;

public enum PaperFormats implements PaperFormat {

	A0(841, 1189, LengthUnits.MILLIMETRE, "A0"),
	A1(594, 841, LengthUnits.MILLIMETRE, "A1"),
	A2(420, 594, LengthUnits.MILLIMETRE, "A2"),
	A3(297, 420, LengthUnits.MILLIMETRE, "A3"),
	A4(210, 297, LengthUnits.MILLIMETRE, "A4"),
	A5(148, 210, LengthUnits.MILLIMETRE, "A5"),
	A6(105, 148, LengthUnits.MILLIMETRE, "A6"),
	A7(74, 105, LengthUnits.MILLIMETRE, "A7"),
	A8(52, 74, LengthUnits.MILLIMETRE, "A8"),
	LETTER(8.5, 11, LengthUnits.INCH, "US Letter"),
	LEGAL(8.5, 14, LengthUnits.INCH, "US Legal"),
	LEDGER(11, 17, LengthUnits.INCH, "US Ledger");
	
	/** The shorter of the two sides */
	private final Length shortSide;
	/** The longer of the two sides */
	private final Length longSide;
	/** The dimensions object representing this format */
	private Dimensions dimensions;
	/** The symbol of this paper size */
	private final String name;
	
	/**
	 * Constructs a new page format.
	 * @param sideOne one of the two sides
	 * @param sideTwo the other of the two sides
	 * @param unit the unit of both dimensions
	 * @param name of the paper size
	 */
	private PaperFormats(double sideOne, double sideTwo, LengthUnit unit,
	                     String name) {
		if (sideOne <= 0 || sideTwo <=0)
			throw new IllegalArgumentException("Side length must be a positive number");
		if (sideOne < sideTwo) {
    		this.shortSide = new Length(sideOne, unit);
    		this.longSide = new Length(sideTwo, unit);
		} else {
			this.longSide = new Length(sideOne, unit);
			this.shortSide = new Length(sideTwo, unit);
		}
		this.name = name;
	}

	@Override
	public Length width(Orientation or) {
		if (or==PaperFormat.Orientation.PORTRAIT)
			return shortSide;
		else
			return longSide;
	}

	@Override
	public Length height(Orientation or) {
		if (or==PaperFormat.Orientation.PORTRAIT)
			return longSide;
		else
			return shortSide;
	}
	
	@Override
	public String formatName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public Dimensions dimensions(Orientation or) {
		if (dimensions == null)
			dimensions = new Dimensions(width(or), height(or));
		return dimensions;
	}
}
