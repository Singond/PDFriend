package com.github.singond.pdfriend.cli.parsing;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnits;

@Parameters(separators="=")
public class SheetSize implements ParameterDelegate {
	
	/** Marks that sheet size has been set */
	private boolean isSet = false;
	/** The width and height in points */
	private double[] widthHeight;
	/** Logger instance */
	private static ExtendedLogger logger = Log.logger(SheetSize.class);
	
	@Parameter(names="--sheet-size", description="The size of the output sheets",
	           converter=DimensionsConverter.class)
	private Dimensions dimensions;
	
	@Parameter(names="--portrait",
	           description="Forces the sheet format to be in portrait orientation")
	private boolean portrait = false;
	
	@Parameter(names="--landscape",
	           description="Forces the sheet format to be in landscape orientation")
	private boolean landscape = false;
	
	/**
	 * Resolves the separate parameters into a consistent state of this object.
	 */
	@Override
	public void postParse() throws ArgumentParsingException {
		if (dimensions == null) {
			if (portrait || landscape) {
				// These switches have no meaning now, inform the user
				StringBuilder switches = new StringBuilder();
				if (portrait) switches.append("--portrait").append(" ");
				if (landscape) switches.append("--landscape");
				logger.warn("parse_orientation_ignored", switches.toString().trim());
			}
			// Nothing has been specified, sheet size remains unset
		} else {
			isSet = true;
			widthHeight = resolveDimensions();
		}
	}
	
	/**
	 * Resolves the width and height based on orientation.
	 * This method assumes that {@code dimensions} is not null.
	 * @return the dimensions as an array: {width, height}
	 * @throws ArgumentParsingException if both --portrait and --landscape
	 *         options are set to true
	 */
	private double[] resolveDimensions() throws ArgumentParsingException {
		assert (dimensions != null) : dimensions;
		if (portrait && landscape) {
			throw new ArgumentParsingException(
					"Cannot provide both portrait and landscape options",
					"parse_orientation_both");
		} else {
    		Dimensions dims = dimensions;
    		double w = dims.width().in(LengthUnits.POINT_POSTSCRIPT);
    		double h = dims.height().in(LengthUnits.POINT_POSTSCRIPT);
    		boolean widthShorter = w < h;
    		if (!portrait && !landscape            // No info, must leave it as it is
    			|| portrait && widthShorter        // Desirable, leave it as it is
    			|| landscape && !widthShorter) {   // Desirable, leave it as it is
    			return new double[]{w, h};
    		} else {
    			// Need to swap h and w in output
    			return new double[]{h, w};
    		}
		}
	}
	
	/**
	 * Returns whether the sheet size has been set in the arguments.
	 * @return {@code true} if a valid dimension has been parsed
	 */
	public boolean isSet() {
		return isSet;
	}
	
	/**
	 * Returns the parsed width in PostScript points.
	 * @throws ArgumentParsingException if both --portrait and --landscape
	 *         options are set to true
	 */
	public double getWidth() throws ArgumentParsingException {
		if (!isSet) {
			throw new IllegalStateException("The sheet size has not been set");
		} else if (widthHeight == null) {
			double[] dims = resolveDimensions();
			widthHeight = dims;
			return dims[0];
		} else {
			return widthHeight[0];
		}
	}
	
	/**
	 * Returns the parsed width in PostScript points.
	 * @throws ArgumentParsingException if both --portrait and --landscape
	 *         options are set to true
	 */
	public double getHeight() throws ArgumentParsingException {
		if (!isSet) {
			throw new IllegalStateException("The sheet size has not been set");
		} else if (widthHeight == null) {
			double[] dims = resolveDimensions();
			widthHeight = dims;
			return dims[1];
		} else {
			return widthHeight[1];
		}
	}
}
