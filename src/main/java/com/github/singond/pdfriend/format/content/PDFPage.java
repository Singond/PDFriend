package com.github.singond.pdfriend.format.content;

import java.awt.geom.AffineTransform;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.github.singond.pdfriend.document.AbstractContent;
import com.github.singond.pdfriend.document.Content;
import com.github.singond.pdfriend.document.ContentVisitor;

/**
 * Content of a page formed by a page of a PDF document.
 * @author Singon
 *
 */
public class PDFPage extends AbstractContent implements Content {

	/** The page */
	private final PDPage page;
	/** The parent document */
	private final PDDocument doc;
	/** The number of the page in the document, starting from 1 */
	private final int pageNumber;

	public PDFPage(PDDocument doc, PDPage page, AffineTransform position) {
		super(position);
		this.doc = doc;
		this.page = page;
		this.pageNumber = doc.getPages().indexOf(page);
	}
	public PDFPage(PDDocument doc, PDPage page) {
		super();
		this.doc = doc;
		this.page = page;
		this.pageNumber = doc.getPages().indexOf(page);
	}
	/**
	 * @param pageNumber The index of the desired page (numbered from 0).
	 */
	public PDFPage(PDDocument doc, int pageNumber) {
		super();
		this.doc = doc;
		this.page = doc.getPage(pageNumber);
		this.pageNumber = pageNumber;
	}

	public PDPage getPage() {
		return page;
	}

	public PDDocument getDoc() {
		return doc;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return a shallow copy of this PDFPage in the position given
	 *         by the {@code transform} argument
	 */
	@Override
	public PDFPage atPosition(AffineTransform newPosition) {
		return new PDFPage(doc, page, newPosition);
	}

	@Override
	public <T, P, E extends Throwable> T invite(ContentVisitor<T, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}

	@Override
	public String toString() {
		// TODO Include file name if available
		return "PDF page " + pageNumber;
	}
}
