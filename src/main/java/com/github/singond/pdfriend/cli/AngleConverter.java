package com.github.singond.pdfriend.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import picocli.CommandLine.ITypeConverter;

import com.github.singond.pdfriend.geometry.Angle;
import com.github.singond.pdfriend.geometry.AngularUnit;
import com.github.singond.pdfriend.geometry.AngularUnits;

/**
 * Parses a string as an angle.
 * If there are no units given, degrees are assumed.
 *
 * @author Singon
 */
public class AngleConverter implements IStringConverter<Angle>, ITypeConverter<Angle> {

	private static DimensionsParser dimsParser = new DimensionsParser();
	private static AngularUnit dfltUnit = AngularUnits.DEGREE;

	@Override
	public Angle convert(String arg) {
		ParsingResult<Angle> angle = dimsParser.parseAngle(arg, dfltUnit);
		if (angle.parsedSuccessfully()) {
			return angle.getResult();
		} else {
			throw new ParameterException(angle.getErrorMessage());
		}
	}

}
