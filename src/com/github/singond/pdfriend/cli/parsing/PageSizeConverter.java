package com.github.singond.pdfriend.cli.parsing;

import java.util.regex.Pattern;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.github.singond.pdfriend.geometry.PageSize;

public class PageSizeConverter implements IStringConverter<PageSize> {
	private static final Pattern NUMBER = Pattern.compile("\\d*(.\\d*)?");
	
	@Override
	public PageSize convert(String arg) {
		if (NUMBER.matcher(arg).matches()) {
			return new PageSize.Scale(Double.parseDouble(arg));
		} else if (arg.equalsIgnoreCase("fit-largest")) {
			return new PageSize.FitToLargest();
		} else {
			throw new ParameterException("Wrong format of size: "+arg);
		}
	}

}
