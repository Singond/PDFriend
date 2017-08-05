package com.github.singond.pdfriend.cli.parsing;

import java.util.regex.Pattern;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.PaperFormat;

class RectangleSizeConverter {
	private static DimensionsParser dimParser = new DimensionsParser();
	private static final Pattern TWO_DIMENSIONS = Pattern.compile("\\d*(\\.\\d*)?.*x\\d*(\\.\\d*)?.*");
	
	private static ExtendedLogger logger = Log.logger(RectangleSizeConverter.class);
	
	/**
	 * <p>
	 * If the size is given as a standard format designation, like "A4",
	 * it is assumed to be in portrait orientation. The task of providing the
	 * correct page rotation is left to the responsibility of the client.
	 * </p>
	 * <p>
	 * If the size is given as two dimensions in the form NxM, then N is
	 * interpreted as width and M as height. A length unit can be specified
	 * for both N and M independently, for example {@code 20cmx0.3m} is
	 * translated as 200 mm (width) by 300 mm (height). If the unit for N
	 * is omitted, it is assumed to be the same as the unit given with M.
	 * If both units are omitted, the default unit given in argument is used.
	 * </p>
	 * @param arg the string to be parsed
	 * @param dfltUnit the default length unit to be used if none is given
	 * @return
	 */
	public static ParsingResult<Dimensions> convert(String arg, LengthUnit dfltUnit) {
		logger.debug("parse_rectangle", arg);
		ParsingResult<Dimensions> result =
				new Unparsable<>("Unknown format definition: " + arg);
		
		ParsingResult<PaperFormat> format = dimParser.parsePaperFormat(arg);
		if (format.parsedSuccessfully()) {
			// Parse standard formats like A-series or US Letter
			result = new Parsed<>(format.getResult().dimensions(PaperFormat.Orientation.PORTRAIT));
		} else if (TWO_DIMENSIONS.matcher(arg).matches()) {
			// Parse the string as two dimensions (width x height)
			String[] parts = arg.split("x", 2);
			assert parts.length == 2 : parts;
			
			/*
			 * Lets assume that if only one unit is given, it is after the
			 * second dimension. If it is given, use it to parse the first
			 * number as well. This enables shorhand notation like 20x30cm
			 * to actually mean "20 cm by 30 cm".
			 */
			ParsingResult<Length> len2 = dimParser.parseLength(parts[1]);
			ParsingResult<Length> len1;
			if (len2.parsedSuccessfully()) {
				// Use the unit from the 2nd part as the default for the 1st
				len1 = dimParser.parseLength(parts[0], len2.getResult().unit());
				if (len1.parsedSuccessfully()) {
					Length length1 = len1.getResult();
					Length length2 = len2.getResult();
					Dimensions dims = new Dimensions(length1, length2);
					logger.debug("parse_rectangle_success", dims);
					result = new Parsed<>(dims);
				}
			}
		}
		return result;
	}

}
