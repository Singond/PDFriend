package com.github.singond.pdfriend.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.PaperFormat;
import com.github.singond.pdfriend.imposition.DimensionSettings;

public class DimensionSettingsConverter implements IStringConverter<DimensionSettings> {
	
	/** Default length unit to be used if none is found in the parsed string */
	private static final LengthUnit DFLT_UNIT = LengthUnits.POINT_POSTSCRIPT;
	/** Default paper orientation to be assumed if none is given in the parsed argument */
	private static final PaperFormat.Orientation DFLT_OR = PaperFormat.Orientation.PORTRAIT;
	/** The parser instance to be used */
	private static final DimensionsParser parser = new DimensionsParser();
	
	@Override
	public DimensionSettings convert(String arg) {
		ParsingResult<Dimensions> dims;
		dims = parser.parseRectangleSize(arg, DFLT_UNIT, DFLT_OR);
		if (dims.parsedSuccessfully()) {
			return DimensionSettings.of(dims.getResult());
		} else {
			throw new ParameterException(dims.getErrorMessage());
		}
	}

}
