package com.github.singond.pdfriend.imposition;

import org.junit.Before;
import org.junit.Test;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.ArgumentParsingException;
import com.github.singond.pdfriend.imposition.PreprocessorSettingsCli;
import com.github.singond.pdfriend.imposition.Preprocessor;

import static org.junit.Assert.*;

public class PageOptionsParsing {
	
	private static ExtendedLogger logger = Log.logger(PageOptionsParsing.class);

	@ParametersDelegate
	private PreprocessorSettingsCli size = new PreprocessorSettingsCli();
	@com.beust.jcommander.Parameter
	private String main;
	private JCommander cmdr;
	
	@Before
	public void prepare() {
		cmdr = JCommander.newBuilder()
				.addObject(this)
				.acceptUnknownOptions(false)
				.build();
	}
	
	@Test
	public void test1() {
		testParseability("--scale 2");
	}
	
	@Test
	public void test2() {
		testParseability("--scale=2");
	}
	
	@Test
	public void test3() {
		testParseability("--align 0,0 --rotation=down");
	}
	
	@Test
	public void test4() {
		testParseability("--align -1,0 --resize=fit");
	}
	
	@Test
	public void test5() {
		testParseability("--rotate=90deg --align -1,0 --resize=fit");
	}
	
	@Test
	public void test6() {
		testParseability("--rotate=100gon --align -1,0 --resize=fit");
	}
	
	@Test
	public void test7() {
		testParseability("--rotate=left --align -1,0 --resize=fit");
	}
	
	@Test
	public void test8() {
		testParseability("--rotate=right --align -1,-1 --resize=fill --size=A4");
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
