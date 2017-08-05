package com.github.singond.pdfriend.cli.parsing;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnits;

@Parameters(separators="=")
public class SheetSize implements ParameterDelegate {
	
	/** The width and height in points */
	private double[] widthHeight;
	
	@Parameter(names="--sheet-size", description="The size of the output sheets",
	           converter=DimensionsConverter.class)
	private Dimensions dimensions;
	
	@Parameter(names="--portrait",
	           description="Forces the sheet format to be in portrait orientation")
	private boolean portrait = false;
	
	@Parameter(names="--landscape",
	           description="Forces the sheet format to be in landscape orientation")
	private boolean landscape = false;
	
	@Override
	public void postParse() throws ArgumentParsingException {
		widthHeight = resolveDimensions();
	}
	
	/**
	 * Sets the width and height based on orientation
	 * @return the dimensions as an array: {width, height}
	 * @throws ArgumentParsingException if both --portrait and --landscape
	 *         options are set to true
	 */
	private double[] resolveDimensions() throws ArgumentParsingException {
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
	 * Returns the parsed width in PostScript points.
	 * @throws ArgumentParsingException if both --portrait and --landscape
	 *         options are set to true
	 */
	public double getWidth() throws ArgumentParsingException {
		if (widthHeight == null) {
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
		if (widthHeight == null) {
			double[] dims = resolveDimensions();
			widthHeight = dims;
			return dims[1];
		} else {
			return widthHeight[1];
		}
	}
}
