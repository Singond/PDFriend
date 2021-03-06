package com.github.singond.pdfriend.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.geometry.Angle;
import com.github.singond.pdfriend.geometry.AngularUnit;
import com.github.singond.pdfriend.geometry.AngularUnits;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;
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
	 * Groups the parsed length and unit in which it was given into one object.
	 * The {@code unit} serves only as a reference to the unit which was used
	 * when parsing the length – it is not bound to the {@code length} anymore
	 * in any way.
	 */
	private static final class LengthWithUnit {
		private final Length length;
		private final LengthUnit unit;
		private LengthWithUnit(Length length, LengthUnit unit) {
			this.length = length;
			this.unit = unit;
		}
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
		ParsingResult<LengthWithUnit> l = parseLengthAndUnit(arg, defaultUnit);
		if (l.parsedSuccessfully()) {
			return new Parsed<Length>(l.getResult().length);
		} else {
			return new Unparsable<Length>(l.getErrorMessage());
		}
	}
	
	/**
	 * Converts a string to a length object and remembers which unit was
	 * used in parsing the value.
	 * 
	 * This method allows to specify a default length unit, which will be
	 * used to interpret the string if no other unit is found therein.
	 * @param arg the string to be parsed
	 * @param defaultUnit the default length unit to be used if none is given.
	 *        If this argument is null and there is no unit given in {@code arg},
	 *        this method will return an {@code Unparsable} object.
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	private ParsingResult<LengthWithUnit> parseLengthAndUnit(
			String arg, LengthUnit defaultUnit) {
		logger.debug("parse_length", arg);
		Matcher matcher = NUMERIC_START.matcher(arg);
		if (matcher.find()) {
			String numericPart = matcher.group();
			try {
				double value = Double.parseDouble(numericPart);
				String rest = arg.substring(matcher.end());
				if (rest.length() == 0) {
					if (defaultUnit == null)
						return new Unparsable<LengthWithUnit>(
								"There is no unit specified in the string, nor any default unit set: "
								+ arg);
					else {
						Length length = new Length(value, defaultUnit);
						LengthWithUnit wrapper = new LengthWithUnit(length, defaultUnit);
						return new Parsed<LengthWithUnit>(wrapper);
					}
				} else {
					ParsingResult<LengthUnit> parsedUnit = parseLengthUnit(rest);
					if (parsedUnit.parsedSuccessfully()) {
						Length length = new Length(value, parsedUnit.getResult());
						LengthWithUnit wrapper = new LengthWithUnit(
								length, parsedUnit.getResult());
						return new Parsed<LengthWithUnit>(wrapper);
					} else {
						// Bad unit
						return new Unparsable<LengthWithUnit>(parsedUnit.getErrorMessage());
					}
				}
			} catch (NumberFormatException e) {
				return new Unparsable<LengthWithUnit>("Unknown number format: " + numericPart);
			}
		} else {
			return new Unparsable<LengthWithUnit>(
					"The string does not start with a number: " + arg);
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
	 * It is assumed that if only one unit is given, it is after the
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
		ParsingResult<LengthWithUnit> len2u = parseLengthAndUnit(parts[1], dfltUnit);
		ParsingResult<Length> len1;
		if (len2u.parsedSuccessfully()) {
			// Use the unit from the 2nd part as the default for the 1st
			len1 = parseLength(parts[0], len2u.getResult().unit);
			if (len1.parsedSuccessfully()) {
				Length length1 = len1.getResult();
				Length length2 = len2u.getResult().length;
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
			return new Parsed<>(format.getResult().dimensions(dfltOrientation));
		
		// Parse the string as two dimensions (width x height)
		if (TWO_DIMENSIONS.matcher(arg).matches()) {
			ParsingResult<Dimensions> dims = parseTwoDimensions(arg, dfltUnit);
			if (dims.parsedSuccessfully())
				return dims;
		}
		
		// Don't know how to parse this
		return new Unparsable<>("Unknown format definition: " + arg);
	}
	
	/**
	 * Converts a string to an angular unit object.
	 * @param the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<AngularUnit> parseAngularUnit(String arg) {
		for (AngularUnit u : AngularUnits.values()) {
			if (u.symbol().equals(arg)) {
				return new Parsed<AngularUnit>(u);
			}
		}
		return new Unparsable<AngularUnit>("Unknown angular unit symbol: " + arg);
	}
	
	/**
	 * Converts a string to an angle object.
	 * 
	 * This variant allows to specify a default angular unit, which will be
	 * used to interpret the string if no other unit is found therein.
	 * @param arg the string to be parsed
	 * @param defaultUnit the default angular unit to be used if none is given.
	 *        If this argument is null and there is no unit given in {@code arg},
	 *        this method will return an {@code Unparsable} object.
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<Angle> parseAngle(String arg, AngularUnit defaultUnit) {
		logger.debug("parse_angle", arg);
		Matcher matcher = NUMERIC_START.matcher(arg);
		if (matcher.find()) {
			String numericPart = matcher.group();
			try {
				double value = Double.parseDouble(numericPart);
				String rest = arg.substring(matcher.end());
				if (rest.length() == 0) {
					if (defaultUnit == null)
						return new Unparsable<Angle>(
								"There is no unit specified in the string, nor any default unit set: "
								+ arg);
					else {
						return new Parsed<Angle>(new Angle(value, defaultUnit));
					}
				} else {
					ParsingResult<AngularUnit> parsedUnit = parseAngularUnit(rest);
					if (parsedUnit.parsedSuccessfully()) {
						Angle length = new Angle(value, parsedUnit.getResult());
						return new Parsed<Angle>(length);
					} else {
						// Bad unit
						return new Unparsable<Angle>(parsedUnit.getErrorMessage());
					}
				}
			} catch (NumberFormatException e) {
				return new Unparsable<Angle>("Unknown number format: " + numericPart);
			}
		} else {
			return new Unparsable<Angle>("The string does not start with a number: " + arg);
		}
	}
	
	/**
	 * Converts a string to an angle object.
	 * 
	 * If no unit is given in {@code arg}, this method will return
	 * an {@code Unparsable} object.
	 * @param arg the string to be parsed
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<Length> parseAngle(String arg) {
		return parseLength(arg, null);
	}
	
	/**
	 * Converts a string to the four widths of page margins.
	 * 
	 * This method interprets the string as either A; A,B; or A,B,C,D.
	 * It is assumed that if only one unit is given, it is after the
	 * last dimension. If it is given, it is used to parse the preceding
	 * numbers as well. This enables shorhand notation like 20,30mm
	 * to actually mean "left and right margin 20 mm wide and top and
	 * bottom margins 30 mm wide".
	 * <p>
	 * The argument can take one of the following forms:
	 * <li>A single length like {@code A}: All four margins have the specified
	 *     width, ie. left = right = top = bottom = A.
	 * <li>Two lengths like {@code A,B}: These are taken to mean the horizontal
	 *     and vertical margins, respectively. This means left = right = A,
	 *     bottom = top = B.
	 * <li>Four lengths like {@code A,B,C,D}. Each margin can have its own
	 *     width. The numbers are interpreted as left, right, bottom and top
	 *     margin, respectively: left = A, right = B, bottom = C, top = D.
	 * 
	 * @param arg the string to be parsed
	 * @param dfltUnit the default length unit to be used if none is given
	 * @return a {@code Parsed} object wrapping the parsed result, or
	 *         an {@code Unparsable} instance if the string cannot be parsed
	 */
	ParsingResult<Margins> parseMargins(String arg, LengthUnit dfltUnit) {
		String[] parts = arg.split(",", 4);
		
		if (parts.length != 1 && parts.length != 2 && parts.length != 4) {
			return new Unparsable<>
					("Please provide one, two or four values separated by commas");
		}
		
		/*
		 * Lets assume that if only one unit is given, it is after the
		 * last dimension. If it is given, use it to parse the preceding
		 * numbers as well. This enables shorhand notation like 20,30mm
		 * to actually mean "left and right margin 20 mm wide and top and
		 * bottom margins 30 mm wide". (See method docs.)
		 */
		ParsingResult<LengthWithUnit> lenLast =
				parseLengthAndUnit(parts[parts.length-1], dfltUnit);
		if (lenLast.parsedSuccessfully()) {
			List<Length> lengths = new ArrayList<>(parts.length);
			for (int i = 0; i < parts.length-1; i++) {
				ParsingResult<Length> len;
				// Use the unit from the last part as the default for the previous
				len = parseLength(parts[i], lenLast.getResult().unit);
				if (len.parsedSuccessfully()) {
					Length length = len.getResult();
					lengths.add(length);
				} else {
					return new Unparsable<>
							("Unknown length format: " + parts[i] + " in " + arg);
				}
			}
			lengths.add(lenLast.getResult().length);
			
			Margins margins;
			switch (parts.length) {
				case 1:
					margins = new Margins(lengths.get(0), lengths.get(0),
					                      lengths.get(0), lengths.get(0));
					break;
				case 2:
					margins = new Margins(lengths.get(0), lengths.get(0),
					                      lengths.get(1), lengths.get(1));
					break;
				case 4:
					margins = new Margins(lengths.get(0), lengths.get(1),
    				                      lengths.get(2), lengths.get(3));
					break;
				default:
					return new Unparsable<>
							("Wrong number of elements in margins definition: " + arg);
			}
			logger.debug("parse_margins_success", margins);
			return new Parsed<>(margins);
		}
		return new Unparsable<>("Unknown format of margins: " + arg);
	}
}
