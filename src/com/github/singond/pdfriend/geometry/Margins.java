package com.github.singond.pdfriend.geometry;

/**
 * The width of margins around a page.
 *
 * @author Singon
 *
 */
public class Margins {

	private final Length top;
	private final Length right;
	private final Length bottom;
	private final Length left;
	
	public Margins(Length left, Length right, Length bottom, Length top) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
	
	public Margins(double left, double right,
	               double bottom, double top, LengthUnit unit) {
		this(new Length(top, unit),
		     new Length(right, unit),
		     new Length(bottom, unit),
		     new Length(left, unit));
	}

	/** Returns the width of the top margin */
	public Length top() {
		return top;
	}

	/** Returns the width of the right margin */
	public Length right() {
		return right;
	}

	/** Returns the width of the bottom margin */
	public Length bottom() {
		return bottom;
	}

	/** Returns the width of the left margin */
	public Length left() {
		return left;
	}
	
	@Override
	public String toString() {
		return toString(LengthUnits.MILLIMETRE);
	}
	
	public String toString(LengthUnit unit) {
		return new StringBuilder()
				.append("left: ").append(left.toString(unit)).append(", ")
				.append("right: ").append(right.toString(unit)).append(", ")
				.append("bottom: ").append(bottom.toString(unit)).append(", ")
				.append("top: ").append(top.toString(unit))
				.toString();
	}
}
