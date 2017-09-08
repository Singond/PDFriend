package com.github.singond.pdfriend.cli.parsing;

import java.util.regex.Pattern;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class TwoNumbers {
	private final double first;
	private final double second;
	
    public TwoNumbers(double first, double second) {
		this.first = first;
		this.second = second;
	}

	public double getFirst() {
		return first;
	}

	public double getSecond() {
		return second;
	}

	public static class Converter implements IStringConverter<TwoNumbers> {
    	private static final Pattern TWO_NUMBERS = Pattern.compile("-?\\d*,-?\\d*");
    	
    	@Override
    	public TwoNumbers convert(String arg) {
    		if (!TWO_NUMBERS.matcher(arg).matches()) {
    			throw new ParameterException("Wrong format of number pair: "+arg);
    		}
    		String[] dims = arg.split(",", 2);
    		try {
    			double first = Double.parseDouble(dims[0]);
    			double second = Double.parseDouble(dims[1]);
    			return new TwoNumbers(first, second);
    		} catch (NumberFormatException e) {
    			throw new ParameterException("Wrong number format", e);
    		}
    	}
    }
}
