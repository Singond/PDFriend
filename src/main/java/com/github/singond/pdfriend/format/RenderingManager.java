package com.github.singond.pdfriend.format;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.io.Output;
import com.github.singond.pdfriend.io.OutputException;

/**
 * Renders virtual documents into file format.
 * @author Singon
 */
public class RenderingManager {
	
	/**
	 * Renders multiple documents as a list of virtual documents.
	 * @param output
	 * @return
	 * @throws OutputException
	 * @throws RenderingException
	 */
	public final void renderDocuments(List<VirtualDocument> docs, Output output)
			throws OutputException, RenderingException {
		// TODO Handle different file formats?
		PDFRenderer renderer = new PDFRenderer();
		for (VirtualDocument doc : docs) {
			output.acceptBytes(renderer.renderBinary(doc));
		}
	}
}
