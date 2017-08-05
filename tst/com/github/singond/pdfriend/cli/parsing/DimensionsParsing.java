package com.github.singond.pdfriend.cli.parsing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.PaperFormat;

import static org.junit.Assert.*;

public class DimensionsParsing {
	private static LengthUnit dfltUnit = LengthUnits.MILLIMETRE;
	private static PaperFormat.Orientation dfltOr = PaperFormat.Orientation.PORTRAIT;
	private static double precision = 1e-12;

	private static List<TestedDimension> dims = new ArrayList<>();
	static {
		dims.add(new TestedDimension("12x50mm", 12, 50));
		dims.add(new TestedDimension("20cmx0.3m", 200, 300));
		dims.add(new TestedDimension("8x11.5in", 203.2, 292.1));
		dims.add(new TestedDimension("A4", 210, 297));
	}
	
	private static ExtendedLogger logger = Log.logger(DimensionsParsing.class);
	
	@Test
	public void conversions() {
		DimensionsParser dimParser = new DimensionsParser();
		for (TestedDimension dim : dims) {
			dim.test(dimParser);
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
		
		void test(DimensionsParser parser) {
			ParsingResult<Dimensions> parsed =
					parser.parseRectangleSize(input, dfltUnit, dfltOr);
			if (!parsed.parsedSuccessfully()) {
				logger.info("Parsing failed: "+parsed.getErrorMessage());
			}
			Dimensions d = parsed.getResult();
			logger.info("Parsed dimension: {}", d.toString(LengthUnits.MILLIMETRE));
			assertEquals(d.width().in(dfltUnit), width, precision);
			assertEquals(d.height().in(dfltUnit), height, precision);
			logger.info("Successfully parsed {} => {}", input, d.toString(LengthUnits.MILLIMETRE));
		}
	}
}
