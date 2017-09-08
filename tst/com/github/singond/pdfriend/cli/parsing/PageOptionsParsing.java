package com.github.singond.pdfriend.cli.parsing;

import org.junit.Before;
import org.junit.Test;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.imposition.Preprocessor;

import static org.junit.Assert.*;

public class PageOptionsParsing {
	
	private static ExtendedLogger logger = Log.logger(PageOptionsParsing.class);

	@ParametersDelegate
	private PageOptions size = new PageOptions();
	@com.beust.jcommander.Parameter
	private String main;
	private JCommander cmdr;
	
	@Before
	public void prepare() {
		cmdr = JCommander.newBuilder()
				.addObject(this)
				.acceptUnknownOptions(true)
				.build();
	}
	
	@Test
	public void test1() {
		testParseability("--scale 2");
	}
	
	/**
	 * 
	 * @param arg
	 * @param expWidth the expected width in points
	 * @param expHeight the expected height in points
	 * @throws ArgumentParsingException
	 */
	private void testParseability(String arg) {
		logger.info("Parsing: "+arg);
		String[] args = arg.split(" ");
		try {
			cmdr.parse(args);
			size.postParse();
		} catch (ArgumentParsingException e) {
			e.printStackTrace();
			fail("Could not parse the string: " + arg);
		}
		Preprocessor.Settings settings = size.getPreprocessorSettings();
		logger.info("Parsed settings: " + settings);
	}
}
