package com.github.singond.pdfriend.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a single piece of data input.
 * @author Singon
 */
public interface InputElement {

	/**
	 * Returns the input as a data stream.
	 *
	 * @return the input
	 * @throws Exception
	 */
	public InputStream getInputStream() throws IOException;

	public String getName();
}
