package com.github.singond.pdfriend.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import picocli.CommandLine.ITypeConverter;

import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;

public class MarginsConverter implements IStringConverter<Margins>, ITypeConverter<Margins> {

	/** Default length unit to be used if none is specified. */
	private static final LengthUnit DFLT_UNIT = LengthUnits.POINT_POSTSCRIPT;
	/** Dimension parser instance to be used. */
	private static DimensionsParser parser = new DimensionsParser();

	@Override
	public Margins convert(String arg) {
		ParsingResult<Margins> dims;
		dims = parser.parseMargins(arg, DFLT_UNIT);
		if (dims.parsedSuccessfully()) {
			return dims.getResult();
		} else {
			throw new ParameterException(dims.getErrorMessage());
		}
	}
}
