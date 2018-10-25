package com.github.singond.pdfriend.imposition;

import org.junit.Before;
import org.junit.Test;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.MarginsConverter;
import com.github.singond.pdfriend.geometry.Margins;

@Parameters(separators="=")
public class MarginsParsing {
	
	private static ExtendedLogger logger = Log.logger(MarginsParsing.class);

	@com.beust.jcommander.Parameter(names="--margins",
			converter=MarginsConverter.class)
	private Margins margins;
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
		testParseability("--margins=1.1cm,5mm");
	}
	
	/**
	 * 
	 * @param arg
	 * @param expWidth the expected width in points
	 * @param expHeight the expected height in points
	 * @throws ParameterConsistencyException
	 */
	private void testParseability(String arg) {
		logger.info("Parsing: "+arg);
		String[] args = arg.split(" ");
		cmdr.parse(args);
		logger.info("Parsed margins: " + margins);
	}
}
