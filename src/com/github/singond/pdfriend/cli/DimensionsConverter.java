package com.github.singond.pdfriend.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.PaperFormat;

public class DimensionsConverter implements IStringConverter<Dimensions> {
	
	/** Default length unit to be used if none is found in the parsed string */
	private static LengthUnit dfltUnit = LengthUnits.POINT_POSTSCRIPT;
	/** Default paper orientation to be assumed if none is given in the parsed argument */
	private static PaperFormat.Orientation dfltOr = PaperFormat.Orientation.PORTRAIT;
	/** The parser instance to be used */
	private static DimensionsParser PARSER = new DimensionsParser();
	
	@Override
	public Dimensions convert(String arg) {
		ParsingResult<Dimensions> dims;
		dims = PARSER.parseRectangleSize(arg, dfltUnit, dfltOr);
		if (dims.parsedSuccessfully()) {
			return dims.getResult();
		} else {
			throw new ParameterException(dims.getErrorMessage());
		}
	}

}
