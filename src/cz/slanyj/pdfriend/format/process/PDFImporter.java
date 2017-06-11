package cz.slanyj.pdfriend.format.process;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.document.ImportException;
import cz.slanyj.pdfriend.document.Importer;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.content.PDFPage;

public class PDFImporter implements Importer {

	private final File file;
	
	public PDFImporter(File file) {
		this.file = file;
	}
	
	/**
	 * {@inheritDoc}
	 * Imports the file given in constructor, if it is a PDF file.
	 */
	@Override
	public VirtualDocument importDocument() throws ImportException {
		PDDocument sourceDoc = null;
		try {
			sourceDoc = PDDocument.load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		VirtualDocument.Builder result = new VirtualDocument.Builder();
		int length = sourceDoc.getNumberOfPages();
		for (int i=0; i<length; i++) {
			PDPage sourcePage = sourceDoc.getPage(i);
			VirtualPage.Builder page = new VirtualPage.Builder();
			page.setWidth(sourcePage.getMediaBox().getWidth());
			page.setHeight(sourcePage.getMediaBox().getHeight());
			page.addContent(new PDFPage(sourceDoc, sourcePage));
			result.addPage(page.build());
		}
		return result.build();
	}
}
