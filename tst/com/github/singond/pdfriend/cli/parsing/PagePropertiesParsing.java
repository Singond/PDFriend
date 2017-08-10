package com.github.singond.pdfriend.cli.parsing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;

@RunWith(Parameterized.class)
public class PagePropertiesParsing {
	
	private JCommander cmdr;
	private static ExtendedLogger logger = Log.logger(PagePropertiesParsing.class);
	
	@ParametersDelegate
	public PagePropertiesParams ppp;
	
	@Before
	public void prepare() {
		cmdr = JCommander.newBuilder()
				.addObject(this)
				.acceptUnknownOptions(true)
				.build();
	}
	
	@Test
	public void enumConversion() {
		String arg = "--scale 2";
		String[] args = arg.split(" ");
		cmdr.parse(args);
	}
}
