package com.github.singond.pdfriend.format.process;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.Parser;
import com.github.singond.pdfriend.format.content.PDFPage;

public class PDFParser implements Parser {

	private static ExtendedLogger logger = Log.logger(PDFParser.class);
	
	public PDFParser() {}
	
	/**
	 * {@inheritDoc}
	 * Imports the file given in constructor, if it is a PDF file.
	 */
	@Override
	public VirtualDocument parseDocument(byte[] bytes) throws ParsingException {
		PDDocument sourceDoc = null;
		try {
			logger.info("parse_pdf");
			sourceDoc = PDDocument.load(bytes);
		} catch (IOException e) {
			logger.error("Error when parsing the file", e);
		}
		VirtualDocument.Builder result = new VirtualDocument.Builder();
		for (PDPage sourcePage : sourceDoc.getPages()) {
			VirtualPage.Builder page = new VirtualPage.Builder();
			double pageWidth, pageHeight;
			PDRectangle box = PDFSettings.getBox(sourcePage);
			/*
			 * If the rotation is multiple of 180, the page is either
			 * upright or upside down, ie. the width is width and
			 * height is height. If the rotation is a multiple of 180 plus
			 * 90, the page is rotated either left or right by 90 degrees,
			 * meaning the declared width and height must be swapped.
			 */
			if (sourcePage.getRotation() % 180 == 0) {
				pageWidth = box.getWidth();
				pageHeight = box.getHeight();
			} else if (sourcePage.getRotation() % 180 == 90) {
				pageWidth = box.getHeight();
				pageHeight = box.getWidth();
			} else {
				throw new AssertionError("PDF Page rotation is not a multiple of 90: "
						+ sourcePage.getRotation());
			}
			page.setWidth(pageWidth);
			page.setHeight(pageHeight);
			page.addContent(new PDFPage(sourceDoc, sourcePage));
			result.addPage(page.build());
		}
		return result.build();
	}
}
