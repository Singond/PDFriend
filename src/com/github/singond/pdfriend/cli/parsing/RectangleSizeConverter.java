package com.github.singond.pdfriend.cli.parsing;

import java.util.regex.Pattern;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;

class RectangleSizeConverter {
	private static DimensionsParser dimParser = new DimensionsParser();
	private static final Pattern TWO_DIMENSIONS = Pattern.compile("\\d*(\\.\\d*)?.*x\\d*(\\.\\d*)?.*");
	
	private static ExtendedLogger logger = Log.logger(RectangleSizeConverter.class);
	
	public static ParsingResult<Dimensions> convert(String arg) {
		logger.debug("parse_rectangle", arg);
		ParsingResult<Dimensions> result =
				new Unparsable<>("Unknown format definition: " + arg);
		
		// TODO Parse standard formats like A-series
		if (TWO_DIMENSIONS.matcher(arg).matches()) {
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
