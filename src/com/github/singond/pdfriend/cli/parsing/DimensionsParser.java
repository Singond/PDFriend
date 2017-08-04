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
	public static final Pattern NUMBER = Pattern.compile("\\d*(\\.\\d*)?");
	/**
	 * An integer or decimal number (with dot as decimal separator) at the
	 * beginning of a string
	 */
	private static final Pattern NUMERIC_START = Pattern.compile("^\\d*(\\.\\d*)?");

	private static ExtendedLogger logger = Log.logger(DimensionsParser.class);

	/**
	 * Converts a string to a length unit object.
	 * @param the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed.
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
