package com.github.singond.pdfriend.cli.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.PaperFormat;
import com.github.singond.pdfriend.geometry.PaperFormats;

class DimensionsParser {
	/** An integer or decimal number with dot as decimal separator */
	public static final Pattern NUMBER = Pattern.compile("\\d*(\\.\\d*)?");
	/**
	 * An integer or decimal number (with dot as decimal separator) at the
	 * beginning of a string
	 */
	private static final Pattern NUMERIC_START = Pattern.compile("^\\d*(\\.\\d*)?");
	/** Two length dimensions separated by 'x' */
	private static final Pattern TWO_DIMENSIONS = Pattern.compile("\\d*(\\.\\d*)?.*x\\d*(\\.\\d*)?.*");
	/**
	 * A lookup of paper formats.
	 * All the formats names are stored in lower-case here to provide
	 * case independent matching.
	 */
	private static final Map<String, PaperFormat> formats = new HashMap<>();

	private static ExtendedLogger logger = Log.logger(DimensionsParser.class);

	static {
		for (PaperFormat format : PaperFormats.values()) {
			formats.put(format.formatName().toLowerCase(), format);
		}
		formats.put("letter", PaperFormats.LETTER);
		formats.put("legal", PaperFormats.LEGAL);
		formats.put("ledger", PaperFormats.LEDGER);
		formats.put("tabloid", PaperFormats.LEDGER); // Tabloid and Ledger are the same size, only rotated
	}
	
	/**
	 * Converts a string to a length unit object.
	 * @param the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<LengthUnit> parseLengthUnit(String arg) {
		for (LengthUnit u : LengthUnits.values()) {
			if (u.symbol().equals(arg)) {
				return new Parsed<LengthUnit>(u);
			}
		}
		return new Unparsable<LengthUnit>("Unknown unit symbol: " + arg);
	}

	/**
	 * Converts a string to a length object.
	 * 
	 * This variant allows to specify a default length unit, which will be
	 * used to interpret the string if no other unit is found therein. 
	 * @param arg the string to be parsed
	 * @param defaultUnit the default length unit to be used if none is given.
	 *        If this argument is null and there is no unit given in {@code arg},
	 *        this method will return an {@code Unparsable} object.
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<Length> parseLength(String arg, LengthUnit defaultUnit) {
		logger.debug("parse_length", arg);
		Matcher matcher = NUMERIC_START.matcher(arg);
		if (matcher.find()) {
			String numericPart = matcher.group();
			try {
				double value = Double.parseDouble(numericPart);
				String rest = arg.substring(matcher.end());
				if (rest.length() == 0) {
					if (defaultUnit == null)
						return new Unparsable<Length>(
								"There is no unit specified in the string, nor any default unit set: "
								+ arg);
					else {
						return new Parsed<Length>(new Length(value, defaultUnit));
					}
				} else {
					ParsingResult<LengthUnit> parsedUnit = parseLengthUnit(rest);
					if (parsedUnit.parsedSuccessfully()) {
						Length length = new Length(value, parsedUnit.getResult());
						return new Parsed<Length>(length);
					} else {
						// Bad unit
						return new Unparsable<Length>(parsedUnit.getErrorMessage());
					}
				}
			} catch (NumberFormatException e) {
				return new Unparsable<Length>("Unknown number format: " + numericPart);
			}
		} else {
			return new Unparsable<Length>("The string does not start with a number: " + arg);
		}
	}
	
	/**
	 * Converts a string to a length object.
	 * 
	 * If no unit is given in {@code arg}, this method will return
	 * an {@code Unparsable} object.
	 * @param arg the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<Length> parseLength(String arg) {
		return parseLength(arg, null);
	}
	
	/**
	 * Converts a string to a paper format object.
	 * 
	 * @param arg the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<PaperFormat> parsePaperFormat(String arg) {
		String argCanonical = arg.toLowerCase();
		if (formats.containsKey(argCanonical))
			return new Parsed<>(formats.get(argCanonical));
		else
			return new Unparsable<>("Unknown format name: " + arg);
	}
	
	/**
	 * Converts a string to a rectangle size.
	 * 
	 * This method interprets the string as WIDTHxHEIGHT.
	 * It is assumes that if only one unit is given, it is after the
	 * second dimension. If it is given, it is used to parse the first
	 * number as well. This enables shorhand notation like 20x30cm
	 * to actually mean "20 cm (width) by 30 cm (height)".
	 * 
	 * @param arg the string to be parsed
	 * @param dfltUnit the default length unit to be used if none is given
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed.
	 *         In the case of successful parsing, the returned Dimensions
	 *         object's width and height correspond to the WIDTH and HEIGHT
	 *         as interpreted here, respectively.
	 */
	ParsingResult<Dimensions> parseTwoDimensions(String arg, LengthUnit dfltUnit) {
		String[] parts = arg.split("x", 2);
		if (parts.length != 2) {
			return new Unparsable<>("The string cannot be split into two parts at 'x'");
		}
		
		ParsingResult<Dimensions> result =
				new Unparsable<>("Unknown format definition: " + arg);
		/*
		 * Lets assume that if only one unit is given, it is after the
		 * second dimension. If it is given, use it to parse the first
		 * number as well. This enables shorhand notation like 20x30cm
		 * to actually mean "20 cm by 30 cm". (See method docs.)
		 */
		ParsingResult<Length> len2 = parseLength(parts[1]);
		ParsingResult<Length> len1;
		if (len2.parsedSuccessfully()) {
			// Use the unit from the 2nd part as the default for the 1st
			len1 = parseLength(parts[0], len2.getResult().unit());
			if (len1.parsedSuccessfully()) {
				Length length1 = len1.getResult();
				Length length2 = len2.getResult();
				Dimensions dims = new Dimensions(length1, length2);
				logger.debug("parse_rectangle_success", dims);
				result = new Parsed<>(dims);
			}
		}
		return result;
	}
	
	/**
	 * <p>
	 * If the size is given as a standard format designation, like "A4",
	 * it is assumed to be in the default orientation given in argument.
	 * The task of providing the correct page rotation is left to the
	 * responsibility of the client code.
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
	 * @param dfltOrientation default orientation (PORTRAIT or LANDSCAPE)
	 *        to be used if it cannot be determined from the string
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<Dimensions> parseRectangleSize(String arg, LengthUnit dfltUnit,
	                                             PaperFormat.Orientation dfltOrientation) {
		logger.debug("parse_rectangle", arg);
		
		// Parse standard formats like A-series or US Letter
		ParsingResult<PaperFormat> format = parsePaperFormat(arg);
		if (format.parsedSuccessfully())
			return new Parsed<>(format.getResult().dimensions(PaperFormat.Orientation.PORTRAIT));
		
		// Parse the string as two dimensions (width x height)
		if (TWO_DIMENSIONS.matcher(arg).matches()) {
			ParsingResult<Dimensions> dims = parseTwoDimensions(arg, dfltUnit);
			if (dims.parsedSuccessfully())
				return dims;
		}
		
		// Don't know how to parse this
		return new Unparsable<>("Unknown format definition: " + arg);
	}
}
