package com.github.singond.pdfriend.format;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.io.FileInput;
import com.github.singond.pdfriend.io.InputElement;
import com.github.singond.pdfriend.io.InputException;
import com.github.singond.pdfriend.io.InputVisitor;

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
	public final VirtualDocument importAsDocument(InputElement input) throws InputException {
		return input.invite(importProvider, null);
	}
	
	/**
	 * Imports multiple input as a list of virtual documents.
	 * @param inputs
	 * @return
	 * @throws InputException
	 */
	public final List<VirtualDocument> importAsDocuments(List<InputElement> inputs)
			throws InputException {
		List<VirtualDocument> docs = new ArrayList<>(inputs.size());
		for (InputElement i : inputs) {
			VirtualDocument doc = i.invite(importProvider, null);
			docs.add(doc);
		}
		return docs;
	}
	
	/**
	 * Helper object to handle the file import
	 */
	private final InputVisitor<VirtualDocument, Void> importProvider
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
	};
}
