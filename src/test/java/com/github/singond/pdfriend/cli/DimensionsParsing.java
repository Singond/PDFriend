package com.github.singond.pdfriend.cli;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.DimensionsParser;
import com.github.singond.pdfriend.cli.ParsingResult;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.PaperFormat;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DimensionsParsing {
	private static LengthUnit dfltUnit = LengthUnits.MILLIMETRE;
	private static PaperFormat.Orientation dfltOr = PaperFormat.Orientation.PORTRAIT;
	private static double precision = 1e-12;
	private static DimensionsParser dimParser = new DimensionsParser();
	private static ExtendedLogger logger = Log.logger(DimensionsParsing.class);

	private static List<TestedDimension> dims = new ArrayList<>();
	static {
		dims.add(new TestedDimension("12x50mm", 12, 50));
		dims.add(new TestedDimension("20cmx0.3m", 200, 300));
		dims.add(new TestedDimension("8x11.5in", 203.2, 292.1));
		dims.add(new TestedDimension("8.5x11in", 215.9, 279.4));
		dims.add(new TestedDimension("A4", 210, 297));
		dims.add(new TestedDimension("Letter", 215.9, 279.4));
	}
	@Parameters
	public static Iterable<? extends Object> data() {
		return dims;
	}
	@Parameter(0)
	public TestedDimension dim;
	
	@Test
	public void conversions() {
		ParsingResult<Dimensions> parsed =
				dimParser.parseRectangleSize(dim.input, dfltUnit, dfltOr);
		if (!parsed.parsedSuccessfully()) {
			logger.info("Parsing failed: "+parsed.getErrorMessage());
		}
		Dimensions d = parsed.getResult();
		logger.info("Parsed dimension: {}", d.toString(LengthUnits.MILLIMETRE));
		assertEquals(d.width().in(dfltUnit), dim.width, precision);
		assertEquals(d.height().in(dfltUnit), dim.height, precision);
		logger.info("Successfully parsed {} => {}", dim.input, d.toString(LengthUnits.MILLIMETRE));
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
	}
}
