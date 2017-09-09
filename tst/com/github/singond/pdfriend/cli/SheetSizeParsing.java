package com.github.singond.pdfriend.cli;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.ArgumentParsingException;
import com.github.singond.pdfriend.cli.SheetSize;

import static org.junit.Assert.*;

import java.util.Arrays;

@RunWith(Enclosed.class)
public class SheetSizeParsing {
	
	private static ExtendedLogger logger = Log.logger(SheetSizeParsing.class);

	@RunWith(Parameterized.class)
	public static class ValidStrings {
	
		/* Parsing */
		@ParametersDelegate
		private SheetSize size = new SheetSize();
		@com.beust.jcommander.Parameter
		private String main;
		private JCommander cmdr;
		
		/* Testing */
		private static final double precision = 1E-12;
		/** Points per millimetre */
		private static final double PPMM = 72/25.4;
		@Parameters(name="{0}")
		public static Iterable<Object[]> data() {
			return Arrays.asList(new Object[][]{
				{"--sheet-size=50x30", 50, 30},
				{"--sheet-size=A4", 210*PPMM, 297*PPMM},
				{"--sheet-size=A4 --portrait", 210*PPMM, 297*PPMM},
				{"--portrait --sheet-size=A4", 210*PPMM, 297*PPMM},
				{"--sheet-size=A4 --landscape", 297*PPMM, 210*PPMM},
				{"", 0, 0},
				{"--gibberish --flag -f -p=12", 0, 0},
				{"--gibberish --sheet-size=letter", 612, 792},
				{"--gibberish --sheet-size=letter --landscape", 792, 612}
			});
		}
		@Parameter(0)
		public String arg;
		@Parameter(1)
		public double width;
		@Parameter(2)
		public double height;
		
		@Before
		public void prepare() {
			cmdr = JCommander.newBuilder()
					.addObject(this)
					.acceptUnknownOptions(true)
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
				if (size.isSet()) {
					w = size.getWidth();
					h = size.getHeight();
					assertEquals("Parsed width is not equal to intended width:",
								 width, w, precision);
					assertEquals("Parsed height is not equal to intended height:",
								 height, h, precision);
					logger.info("Successfully parsed {} => {} pt x {} pt", arg, w, h);
				} else {
					// Sheet size was not parsed
					boolean size = arg.contains("--sheet-size");
					boolean portrait = arg.contains("--portrait");
					boolean landscape = arg.contains("--landscape");
					assertFalse("Failed to parse string "+arg, size || portrait || landscape);
					logger.info("Correctly ignored string: "+arg);
				}
			} catch (ArgumentParsingException e) {
				e.printStackTrace();
				fail("Could not parse the string: " + arg);
			}
		}
	}
	
	@RunWith(Parameterized.class)
	public static class InvalidStrings {
	
		/* Parsing */
		@ParametersDelegate
		private SheetSize size = new SheetSize();
		@com.beust.jcommander.Parameter
		private String main;
		private JCommander cmdr;
		
		@Parameters(name="{0}")
		public static Iterable<Object[]> data() {
			return Arrays.asList(new Object[][]{
				{"--gibberish --sheet-size"},
			});
		}
		@Parameter
		public String arg;
		
		@Before
		public void prepare() {
			cmdr = JCommander.newBuilder()
					.addObject(this)
					.acceptUnknownOptions(true)
					.build();
		}
		
		@Test(expected=ParameterException.class)
		public void unparsable() {
			test(arg);
		}
		
		
		/**
		 * 
		 * @param arg
		 * @param expWidth the expected width in points
		 * @param expHeight the expected height in points
		 * @throws ArgumentParsingException 
		 */
		private void test(String arg) {
			String[] args = arg.split(" ");
			cmdr.parse(args);
		}
	}
}
