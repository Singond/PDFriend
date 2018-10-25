package com.github.singond.pdfriend.io;

import java.io.OutputStream;

/**
 * Represents data output.
 * @author Singon
 */
public interface Output {

	/**
	 * Returns a data stream ready to accept the data.
	 *
	 * @return an output stream
	 * @throws OutputException if an error occurs opening the stream;
	 */
	public OutputStream getOutputStream() throws OutputException;
}
