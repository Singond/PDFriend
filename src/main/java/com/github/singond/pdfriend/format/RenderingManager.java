package com.github.singond.pdfriend.format;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.io.Output;

/**
 * Renders virtual documents into file format.
 *
 * @author Singon
 */
public class RenderingManager {

	/**
	 * Renders multiple documents as a list of virtual documents.
	 *
	 * @param docs the documents to be rendered
	 * @param output the output to write the rendered document into
	 * @throws RenderingException if an error occurs during rendering
	 */
	public final void renderDocuments(List<VirtualDocument> docs, Output output)
			throws RenderingException {
		// TODO Handle different file formats?
		PDFRenderer renderer = new PDFRenderer();
		for (VirtualDocument doc : docs) {
			renderer.render(doc, output);
		}
	}
}
