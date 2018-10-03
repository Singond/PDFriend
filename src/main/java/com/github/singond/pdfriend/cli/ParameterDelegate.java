package com.github.singond.pdfriend.cli;

public interface ParameterDelegate {

	/**
	 * Code to be run after JCommander is finished parsing the arguments.
	 * @throws ParameterConsistencyException
	 */
	public void postParse() throws ParameterConsistencyException;
}
