package com.github.singond.pdfriend.io;

import java.io.OutputStream;

/**
 * Represents a single piece of data output.
 * @author Singon
 */
interface OutputElement {

	/**
	 * Returns a data stream ready to receive data to be output.
	 *
	 * @return an output data stream
	 * @throws OutputException if an error occurs obtaining the stream
	 */
	public OutputStream getOutputStream() throws OutputException;
}
