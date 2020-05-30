package com.github.singond.pdfriend.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import picocli.CommandLine.ITypeConverter;

import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.PaperFormat;

public class DimensionsConverter implements IStringConverter<Dimensions>, ITypeConverter<Dimensions> {

	/**
	 * Default length unit to be used if none is found in the parsed string.
	 */
	private static final LengthUnit DFLT_UNIT = LengthUnits.POINT_POSTSCRIPT;
	/**
	 * Default paper orientation.
	 */
	private static final PaperFormat.Orientation DFLT_OR
			= PaperFormat.Orientation.PORTRAIT;
	/**
	 * The parser object.
	 */
	private static final DimensionsParser parser = new DimensionsParser();

	@Override
	public Dimensions convert(String arg) {
		ParsingResult<Dimensions> dims;
		dims = parser.parseRectangleSize(arg, DFLT_UNIT, DFLT_OR);
		if (dims.parsedSuccessfully()) {
			return dims.getResult();
		} else {
			throw new ParameterException(dims.getErrorMessage());
		}
	}
}
