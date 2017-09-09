package com.github.singond.pdfriend.cli.parsing;

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
import com.github.singond.pdfriend.geometry.Angle;
import com.github.singond.pdfriend.geometry.AngularUnit;
import com.github.singond.pdfriend.geometry.AngularUnits;

import static java.lang.Math.PI;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class AnglesParsing {
	private static AngularUnit dfltUnit = AngularUnits.DEGREE;
	private static double precision = 1e-12;
	private static DimensionsParser dimParser = new DimensionsParser();
	private static ExtendedLogger logger = Log.logger(AnglesParsing.class);
	private static final double DEG_PER_RAD = 180/PI;

	private static List<TestedAngle> dims = new ArrayList<>();
	static {
		dims.add(new TestedAngle("360deg", 360));
		dims.add(new TestedAngle("1rad", 1*DEG_PER_RAD));
	}
	@Parameters
	public static Iterable<? extends Object> data() {
		return dims;
	}
	@Parameter(0)
	public TestedAngle angle;
	
	@Test
	public void conversions() {
		ParsingResult<Angle> parsed =
				dimParser.parseAngle(angle.input, dfltUnit);
		if (!parsed.parsedSuccessfully()) {
			logger.info("Parsing failed: "+parsed.getErrorMessage());
		}
		Angle a = parsed.getResult();
		logger.info("Parsed angle: {}", a);
		assertEquals(a.in(dfltUnit), angle.value, precision);
		logger.info("Successfully parsed {} => {}", angle.input, a);
	}
	
	private static class TestedAngle {
		/** The parsed string */
		final String input;
		/** Actual value in radians */
		final double value;
		
		public TestedAngle(String input, double actual) {
			this.input = input;
			this.value = actual;
		}
	}
}
