package com.github.singond.pdfriend.format;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.io.FileInput;
import com.github.singond.pdfriend.io.Input;
import com.github.singond.pdfriend.io.InputException;
import com.github.singond.pdfriend.io.InputVisitor;

/**
 * Imports input data into virtual documents to be processed
 * by the application.
 * @author Singon
 */
public class ImportManager implements InputVisitor<VirtualDocument, Void> {

	public VirtualDocument importAsDocument(Input input) throws InputException {
		return input.invite(this, null);
	}

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
}
