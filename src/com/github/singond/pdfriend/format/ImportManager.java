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
	 * Imports a single piece of input as a single virtual document.
	 * @param input
	 * @return
	 * @throws InputException
	 */
//	public final VirtualDocument importAsDocument(InputElement input) throws InputException {
//		return input.invite(importProvider, null);
//	}
	
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
//		for (InputElement i : input) {
//			VirtualDocument doc = i.invite(importProvider, null);
//			docs.add(doc);
//		}
		return docs;
	}
	
	/**
	 * Helper object to handle the file import
	 * @throws ImportException 
	 */
	/*private final InputVisitor<VirtualDocument, Void> importProvider
			= new InputVisitor<VirtualDocument, Void>() {
		
		@Override
		public VirtualDocument visit(FileInput input, Void param) throws InputException {
			// TODO Handle different file formats
			try {
				return new PDFImporter(input.getFile().toFile()).importDocument();
			} catch (ImportException e) {
				// TODO Auto-generated catch block
				throw new InputException("Error when reading file "
						+ input.getFile().toAbsolutePath(), input, e);
			}
		}
	};*/
	
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
