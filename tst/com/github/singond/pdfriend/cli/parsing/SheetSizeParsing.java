package com.github.singond.pdfriend.cli.parsing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;

import static org.junit.Assert.*;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class SheetSizeParsing {

	/* Parsing */
	@ParametersDelegate
	private SheetSize size = new SheetSize();
	private JCommander cmdr;
	
	/* Testing */
	private static final double precision = 1E-12;
	/** Points per millimetre */
	private static final double PPMM = 72/25.4;
	@Parameters
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]{
			{"--sheet-size=50x30", 50, 30},
			{"--sheet-size=A4", 210*PPMM, 297*PPMM},
			{"--sheet-size=A4 --portrait", 210*PPMM, 297*PPMM},
			{"--portrait --sheet-size=A4", 210*PPMM, 297*PPMM},
			{"--sheet-size=A4 --landscape", 297*PPMM, 210*PPMM}
		});
	}
	@Parameter(0)
	public String arg;
	@Parameter(1)
	public double width;
	@Parameter(2)
	public double height;
	
	private static ExtendedLogger logger = Log.logger(SheetSizeParsing.class);
	
	@Before
	public void prepare() {
		cmdr = JCommander.newBuilder()
				.addObject(this)
				.build();
	}
	
	@Test
	public void run() {
		test(arg, width, height);
	}
	
	/**
	 * 
	 * @param arg
	 * @param expWidth the expected width in points
	 * @param expHeight the expected height in points
	 * @throws ArgumentParsingException 
	 */
	private void test(String arg, double expWidth, double expHeight) {
		double w, h;
		String[] args = arg.split(" ");
		try {
			cmdr.parse(args);
			size.postParse();
			w = size.getWidth();
			h = size.getHeight();
			assertEquals("Parsed width is not equal to intended width:",
			             width, w, precision);
			assertEquals("Parsed height is not equal to intended height:",
			             height, h, precision);
			logger.info("Successfully parsed {} => {} pt x {} pt", arg, w, h);
		} catch (ArgumentParsingException e) {
    		e.printStackTrace();
    		fail("Could not parse the string: " + arg);
		}
	}
}
