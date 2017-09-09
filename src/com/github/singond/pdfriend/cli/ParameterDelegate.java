package com.github.singond.pdfriend.cli;

public interface ParameterDelegate {

	/**
	 * Code to be run after JCommander is finished parsing the arguments.
	 * @throws ArgumentParsingException
	 */
	public void postParse() throws ArgumentParsingException;
}
