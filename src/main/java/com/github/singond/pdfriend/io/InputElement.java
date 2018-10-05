package com.github.singond.pdfriend.io;

import java.io.InputStream;

/**
 * Represents a single piece of data input.
 * @author Singon
 */
public interface InputElement {

	/** Returns the input as a byte array */
	public InputStream getInputStream() throws Exception;
}
