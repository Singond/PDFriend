package com.github.singond.pdfriend.format;

import java.io.InputStream;

import com.github.singond.pdfriend.document.VirtualDocument;

public interface Parser {

	/**
	 * Process the given binary data into a new VirtualDocument.
	 * For each page of the input, create a new VirtualPage with the
	 * input page set as its only content in the default position.
	 *
	 * @param in the stream of data to import
	 * @return a new VirtualDocument
	 */
	public VirtualDocument parseDocument(InputStream in) throws ParsingException;
}
