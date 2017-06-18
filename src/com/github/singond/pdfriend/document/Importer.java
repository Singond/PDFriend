package com.github.singond.pdfriend.document;

public interface Importer {

	/**
	 * Process the import into a new VirtualDocument.
	 * For each page of the input, create a new VirtualPage with the
	 * input page set as its only content in the default position.
	 * @return
	 */
	public VirtualDocument importDocument() throws ImportException;
}
