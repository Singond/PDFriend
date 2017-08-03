package com.github.singond.pdfriend.cli.parsing;

/**
 * An optional result of parsing a string input.
 * 
 * @author Singon
 * @param <T> the type of the parsing result
 */
interface ParsingResult<T> {
	/**
	 * Signifies that the parsing operation resulted in a successful
	 * construction of a result object.
	 * @return true if parsing was successful
	 */
	public boolean parsedSuccessfully();
	
	/**
	 * Returns the parsed result as an object of the type T.
	 * This method should never return null.
	 * @return the parsed result
	 */
	public T getResult();
}
