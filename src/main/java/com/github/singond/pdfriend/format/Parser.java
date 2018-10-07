package com.github.singond.pdfriend.format;

import java.io.InputStream;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.io.InputElement;

public interface Parser {

	/**
	 * Process the given binary data into a new VirtualDocument.
	 * For each page of the input, create a new VirtualPage with the
	 * input page set as its only content in the default position.
	 *
	 * @param in the stream of data to import
	 * @return a virtual document parsed from {@code in}
	 */
	public VirtualDocument parseDocument(InputStream in)
			throws ParsingException;

	/**
	 * Process the given input into a new VirtualDocument.
	 * For each page of the input, create a new VirtualPage with the
	 * input page set as its only content in the default position.
	 *
	 * @param in an input element containing the data to import
	 * @return a virtual document parsed from {@code in}
	 */
	public VirtualDocument parseDocument(InputElement in)
			throws ParsingException;
}
