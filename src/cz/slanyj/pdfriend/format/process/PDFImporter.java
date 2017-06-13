package cz.slanyj.pdfriend.format.process;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.Bundle;
import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.document.ImportException;
import cz.slanyj.pdfriend.document.Importer;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.content.PDFPage;
import cz.slanyj.pdfriend.test.PrintBooklet;

public class PDFImporter implements Importer {

	private final File file;
	
	private static final ExtendedLogger logger = Log.logger(PrintBooklet.class);
	
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
			logger.info("load-file-start", file);
			sourceDoc = PDDocument.load(file);
		} catch (IOException e) {
			logger.error("Error when loading the file", e);
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
