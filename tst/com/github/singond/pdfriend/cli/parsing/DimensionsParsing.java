package com.github.singond.pdfriend.cli.parsing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;

import static org.junit.Assert.*;

public class DimensionsParsing {
	private static LengthUnit dfltUnit = LengthUnits.MILLIMETRE;
	private static double precision = 1e-12;

	private static List<TestedDimension> dims = new ArrayList<>();
	static {
		dims.add(new TestedDimension("12x50mm", 12, 50));
		dims.add(new TestedDimension("20cmx0.3m", 200, 300));
		dims.add(new TestedDimension("8x11.5in", 203.2, 292.1));
	}
	
	@Test
	public void conversions() {
		for (TestedDimension dim : dims) {
			dim.test();
		}
	}
	
	private static class TestedDimension {
		/** The parsed string */
		final String input;
		/** Actual value in mm */
		final double width;
		/** Actual value in mm */
		final double height;
		
		public TestedDimension(String input, double width, double height) {
			this.input = input;
			this.width = width;
			this.height = height;
		}
		
		void test() {
			ParsingResult<Dimensions> parsed = RectangleSizeConverter.convert(input);
			assertTrue(parsed.parsedSuccessfully());
			Dimensions d = parsed.getResult();
			System.out.println("Parsed dimension: " + d.toString(LengthUnits.MILLIMETRE));
			assertEquals(d.width().in(dfltUnit), width, precision);
			assertEquals(d.height().in(dfltUnit), height, precision);
			System.out.println("Successfully parsed " + input + " => " + d.toString(LengthUnits.MILLIMETRE));
		}
	}
}
