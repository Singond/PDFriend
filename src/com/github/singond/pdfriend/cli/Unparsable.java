package com.github.singond.pdfriend.cli;

/**
 * Represents that the input string could not be parsed successfully.
 * Attempts to retrieve the result will cause an UnsupportedOperationException
 * to be thrown.
 *
 * @author Singon
 * @param <T> the type of the parsing result
 */
final class Unparsable<T> implements ParsingResult<T> {
	/** An error message to be returned in {@code getMessage} */
	private final String message;
	
	/**
	 * Constructs a new instance
	 * @param msg message explaining why the conversion failed
	 */
	Unparsable(String msg) {
		this.message = msg;
	}
	
	@Override
	public boolean parsedSuccessfully() {
		return false;
	}

	@Override
	public T getResult() {
		throw new UnsupportedOperationException(
				"The string could not be parsed by the given parser");
	}

	/**
	 * Returns a message explaining why the conversion failed.
	 */
	@Override
	public String getErrorMessage() {
		return message;
	}
}
