package com.github.singond.pdfriend.format;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFImporter;
//import com.github.singond.pdfriend.io.FileInput;
import com.github.singond.pdfriend.io.Input;
import com.github.singond.pdfriend.io.InputElement;
import com.github.singond.pdfriend.io.InputException;
//import com.github.singond.pdfriend.io.InputVisitor;

/**
 * Imports input data into virtual documents to be processed
 * by the application.
 * @author Singon
 */
public class ImportManager {
	
	/**
	 * Imports multiple input as a list of virtual documents.
	 * @param input
	 * @return
	 * @throws InputException
	 * @throws ImportException 
	 */
	public final List<VirtualDocument> importAsDocuments(Input input)
			throws InputException, ImportException {
		List<VirtualDocument> docs = new ArrayList<>();
		while (input.hasNext()) {
			docs.add(inputToDocument(input.next()));
		}
		return docs;
	}
	
	/**
	 * Creates a new VirtualDocument from the given input data.
	 * @return a VirtualDocument representing the input
	 */
	private VirtualDocument inputToDocument(InputElement input) throws ImportException {
		// TODO Handle different file formats
		try {
			return new PDFImporter().importDocument(input.getBytes());
		} catch (InputException e) {
			throw new ImportException(e);
		}
	}
}
