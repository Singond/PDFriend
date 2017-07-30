package com.github.singond.pdfriend.format;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFParser;
//import com.github.singond.pdfriend.io.FileInput;
import com.github.singond.pdfriend.io.Input;
import com.github.singond.pdfriend.io.InputElement;
import com.github.singond.pdfriend.io.InputException;

/**
 * Imports input data into virtual documents to be processed
 * by the application.
 * @author Singon
 */
public class ParsingManager {
	
	/**
	 * Imports multiple input as a list of virtual documents.
	 * @param input
	 * @return
	 * @throws InputException
	 * @throws ParsingException 
	 */
	public final List<VirtualDocument> parseToDocuments(Input input)
			throws InputException, ParsingException {
		List<VirtualDocument> docs = new ArrayList<>();
		while (input.hasNext()) {
			docs.add(parseToDocument(input.next()));
		}
		return docs;
	}
	
	/**
	 * Creates a new VirtualDocument from the given input data.
	 * @return a VirtualDocument representing the input
	 */
	private VirtualDocument parseToDocument(InputElement input) throws ParsingException {
		// TODO Handle different file formats
		try {
			return new PDFParser().parseDocument(input.getBytes());
		} catch (InputException e) {
			throw new ParsingException(e);
		}
	}
}