package com.github.singond.pdfriend.format.process;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.Parser;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.content.PDFPage;
import com.github.singond.pdfriend.io.InputElement;

public class PDFParser implements Parser, AutoCloseable {

	private static ExtendedLogger logger = Log.logger(PDFParser.class);

	/** A list of open PDDocuments which need to be closed */
	private final List<PDDocument> openDocs = new ArrayList<>();

	public PDFParser() {}

	/**
	 * {@inheritDoc}
	 * Imports the file given in constructor, if it is a PDF file.
	 */
	@SuppressWarnings("resource") // Resources are closed later in close() method
	@Override
	public VirtualDocument parseDocument(InputStream in) throws ParsingException {
		PDDocument sourceDoc = null;
		try {
			if (logger.isDebugEnabled()) logger.debug("parse_pdf");
			sourceDoc = PDDocument.load(in);
			openDocs.add(sourceDoc);
			VirtualDocument result = parseDocument(sourceDoc, "untitled").build();
			return result;
		} catch (IOException e) {
			logger.error("Error when parsing the file", e);
			throw new ParsingException("Error when parsing the PDF file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * Imports the file given in constructor, if it is a PDF file.
	 */
	@SuppressWarnings("resource") // Resources are closed later in close() method
	@Override
	public VirtualDocument parseDocument(InputElement in)
			throws ParsingException {
		PDDocument sourceDoc = null;
		try {
			if (logger.isDebugEnabled()) logger.debug("parse_pdf");
			sourceDoc = PDDocument.load(in.getInputStream());
			openDocs.add(sourceDoc);
			String name = in.getName();
			VirtualDocument.Builder result = parseDocument(sourceDoc, name);
			result.setName(name);
			return result.build();
		} catch (IOException e) {
			logger.error("Error when parsing the file", e);
			throw new ParsingException("Error when parsing the PDF file", e);
		}
	}

	/**
	 * Converts the given PDF document into a virtual document.
	 */
	private VirtualDocument.Builder parseDocument(PDDocument sourceDoc,
			String docDescription) throws ParsingException {
		VirtualDocument.Builder result = new VirtualDocument.Builder();
		result.setName(docDescription);
		int pageNo = 1;
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
				throw new RuntimeException("PDF Page rotation is not a multiple of 90: "
						+ sourcePage.getRotation());
			}
			page.setWidth(pageWidth);
			page.setHeight(pageHeight);
			page.addContent(new PDFPage(sourceDoc, sourcePage,
			                docDescription + "-" + pageNo++));
			result.addPage(page);
		}
		return result;
	}

	/**
	 * Closes the PDF documents created when parsing the input.
	 * <p>
	 * <strong>Warning:</strong>
	 * This causes the PDF content in {@code VirtualDocument} instances
	 * produced by this {@code PDFParser} to be closed, too, thus making
	 * the virtual documents (and all documents derived from them) unusable
	 * in output.
	 * @throws IOException when at least one of the backing documents fails
	 *         to close. The exception accompanying the first failure is
	 *         passed to the thrown exception as its cause.
	 */
	@Override
	public void close() throws IOException {
		List<IOException> exceptions = new ArrayList<>();
		for (PDDocument openDoc : openDocs) {
			try {
				logger.debug("parse_pdf_close", openDoc);
				openDoc.close();
			} catch (IOException e) {
				exceptions.add(e);
				logger.error("PDF document could not be closed: " + openDoc, e);
			}
		}
		if (!exceptions.isEmpty()) {
			int size = exceptions.size();
			throw new IOException
					(size + " documents failed to close; the first doc failed with ", exceptions.get(0));
		}
	}
}
