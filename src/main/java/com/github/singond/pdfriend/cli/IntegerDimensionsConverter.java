package com.github.singond.pdfriend.cli;

import java.util.regex.Pattern;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.github.singond.pdfriend.geometry.IntegerDimensions;

public class IntegerDimensionsConverter implements IStringConverter<IntegerDimensions> {
	private static final Pattern INT_DIMENSIONS = Pattern.compile("\\d*x\\d*");
	
	@Override
	public IntegerDimensions convert(String arg) {
		if (!INT_DIMENSIONS.matcher(arg).matches()) {
			throw new ParameterException("Wrong format of dimensions: "+arg);
		}
		String[] dims = arg.split("x", 2);
		try {
			int first = Integer.parseInt(dims[0]);
			int second = Integer.parseInt(dims[1]);
			return new IntegerDimensions(first, second);
		} catch (NumberFormatException e) {
			throw new ParameterException("Wrong number format", e);
		}
	}
}
