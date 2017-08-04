package com.github.singond.pdfriend.cli.parsing;

/**
 * Represents an object resulting from a successful parsing operation.
 *
 * @author Singon
 * @param <T> the type of the parsing result
 */
class Parsed<T> implements ParsingResult<T> {
	/**
	 * The parsed result represented by this wrapper.
	 * This object must not be null (see the contract of {@code getResult)).
	 */
	private final T result;
	
	/**
	 * Constructs a new wrapper around the given result object.
	 * @param result
	 * @throws NullPointerException if the argument is null
	 */
	Parsed(T result) {
		if (result == null)
			throw new NullPointerException("Null is not accepted as a valid parsing result");
		this.result = result;
	}
	
	@Override
	public boolean parsedSuccessfully() {
		return true;
	}

	/**
	 * Retrieves the result of the parsing operation.
	 * @return the parsed result. This will never be null.
	 */
	@Override
	public T getResult() {
		return result;
	}

	@Override
	public String getErrorMessage() {
		throw new UnsupportedOperationException("There was no error in parsing");
	}

}
