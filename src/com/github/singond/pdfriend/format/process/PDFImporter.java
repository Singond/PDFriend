package com.github.singond.pdfriend.format.process;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ImportException;
import com.github.singond.pdfriend.format.Importer;
import com.github.singond.pdfriend.format.content.PDFPage;

public class PDFImporter implements Importer {

//	private final File file;
	
	private static ExtendedLogger logger = Log.logger(PDFImporter.class);
	
	public PDFImporter() {
//		this.file = file;
	}
	
	/**
	 * {@inheritDoc}
	 * Imports the file given in constructor, if it is a PDF file.
	 */
	@Override
	public VirtualDocument importDocument(byte[] bytes) throws ImportException {
		PDDocument sourceDoc = null;
		try {
//			logger.info("load-file-start", file);
			sourceDoc = PDDocument.load(bytes);
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
