package com.github.singond.pdfriend.geometry;

/**
 * A page format, ie. a rectangle with exact dimensions.
 * The format keeps no notion of page orientation (ie. portrait or landscape).
 */
public interface PaperFormat {

	/**
	 * Returns the shorter side in the case of portrait orientation,
	 * and the longer side in the case of landscape orientation.
	 * @param or orientation of the page
	 * @return the width as specified above
	 */
	public Length width(Orientation or);
	
	/**
	 * Returns the longer side in the case of portrait orientation,
	 * and the shorter side in the case of landscape orientation.
	 * @param or orientation of the page
	 * @return the height as specified above
	 */
	public Length height(Orientation or);
	
	/**
	 * Returns the dimensions object representing this paper format.
	 * @param or orientation of the page. If this is portrait, the shorter
	 *        side will become the return value's width and vice versa
	 * @return the dimensions of the rectangle representing this format
	 */
	public Dimensions dimensions(Orientation or);
	
	/**
	 * Returns the name of the paper format, e.g. "A4" or "US Letter".
	 */
	public String formatName();
	
	/**
	 * Orientation of the paper.
	 */
	public static enum Orientation {
		/** Shorter dimension is horizontal */
		PORTRAIT,
		/** Shorter dimension is vertical */
		LANDSCAPE;
	}
}
