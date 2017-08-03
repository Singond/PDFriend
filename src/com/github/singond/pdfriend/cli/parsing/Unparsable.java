package com.github.singond.pdfriend.cli.parsing;

/**
 * Represents that the input string could not be parsed successfully.
 * Attempts to retrieve the result will cause an UnsupportedOperationException
 * to be thrown.
 *
 * @author Singon
 * @param <T> the type of the parsing result
 */
final class Unparsable<T> implements ParsingResult<T> {
	/** Constructs a new instance  */
	Unparsable() {}
	
	@Override
	public boolean parsedSuccessfully() {
		return false;
	}

	@Override
	public T getResult() {
		throw new UnsupportedOperationException(
				"The string could not be parsed by the given parser");
	}
}
