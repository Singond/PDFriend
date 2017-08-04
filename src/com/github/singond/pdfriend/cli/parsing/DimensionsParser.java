package com.github.singond.pdfriend.cli.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;

class DimensionsParser {
	/** An integer or decimal number with dot as decimal separator */
	public static final Pattern NUMBER = Pattern.compile("\\d*(.\\d*)?");
	/**
	 * An integer or decimal number (with dot as decimal separator) at the
	 * beginning of a string
	 */
	private static final Pattern NUMERIC_START = Pattern.compile("^\\d*(.\\d*)?");

	/** Instance to be returned if the length unit cannot be parsed */
	private static final Unparsable<LengthUnit> BAD_LENGTH_UNIT = new Unparsable<LengthUnit>();
	/** Instance to be returned if the length cannot be parsed */
	private static final Unparsable<Length> BAD_LENGTH = new Unparsable<Length>();
	
	private static ExtendedLogger logger = Log.logger(DimensionsParser.class);

	/**
	 * Converts a string to a length unit object.
	 * @param the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed.
	 */
	ParsingResult<LengthUnit> parseLengthUnit(String arg) {
		for (LengthUnit u : LengthUnits.values()) {
			if (u.getSymbol().equals(arg)) {
				return new Parsed<LengthUnit>(u);
			}
		}
		return BAD_LENGTH_UNIT;
	}

	/**
	 * Converts a string to a length object.
	 * This variant allows to specify a default length unit, which will be
	 * used to interpret the string if no other unit is found therein. 
	 * @param arg the string to be parsed
	 * @param defaultUnit the default length unit to be used if none is given.
	 *        If this argument is null and there is no unit given in {@code arg},
	 *        this method will return an {@code Unparsable} object.
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed.
	 */
	ParsingResult<Length> parseLength(String arg, LengthUnit defaultUnit) {
		logger.debug("parse-length", arg);
		Matcher matcher = NUMERIC_START.matcher(arg);
		if (matcher.find()) {
			try {
				double value = Double.parseDouble(matcher.group());
				String rest = arg.substring(matcher.end());
				if (rest.length() == 0) {
					if (defaultUnit == null)
						return BAD_LENGTH;
//						throw new ArgumentParsingException
//								("There is no unit specified in the string, nor any default unit set");
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
    					return BAD_LENGTH;
    				}
				}
			} catch (NumberFormatException e) {
				// The string
				return BAD_LENGTH;
			}
		} else {
			// The string does not start with a number
			return BAD_LENGTH;
		}
	}
	
	/**
	 * Converts a string to a length object. 
	 * If no unit is given in {@code arg}, this method will return
	 * an {@code Unparsable} object.
	 * @param arg the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed.
	 */
	ParsingResult<Length> parseLength(String arg) {
		return parseLength(arg, null);
	}
}
