package com.github.singond.pdfriend.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;

/**
 * Parses a string as length.
 * If there are no units given, PostScript points are assumed.
 *
 * @author Singon
 */
public class LengthConverter implements IStringConverter<Length> {

	private static DimensionsParser dimsParser = new DimensionsParser();
	private static LengthUnit dfltUnit = LengthUnits.POINT_POSTSCRIPT;
	
	@Override
	public Length convert(String arg) {
		ParsingResult<Length> length = dimsParser.parseLength(arg, dfltUnit);
		if (length.parsedSuccessfully()) {
			return length.getResult();
		} else {
			throw new ParameterException(length.getErrorMessage());
		}
	}

}
