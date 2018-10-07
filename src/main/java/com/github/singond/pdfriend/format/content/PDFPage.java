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
	/** Short description of the page, used in toString() */
	private final String description;

	public PDFPage(PDDocument doc, PDPage page, AffineTransform position,
	               String description) {
		super(position);
		this.doc = doc;
		this.page = page;
		this.description = description;
	}
	public PDFPage(PDDocument doc, PDPage page, String description) {
		super();
		this.doc = doc;
		this.page = page;
		this.description = description;
	}
	/**
	 * @param pageNumber The index of the desired page (numbered from 0).
	 */
	public PDFPage(PDDocument doc, int pageNumber, String description) {
		super();
		this.doc = doc;
		this.page = doc.getPage(pageNumber);
		this.description = description;
	}

	public PDPage getPage() {
		return page;
	}

	public PDDocument getDoc() {
		return doc;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return a shallow copy of this PDFPage in the position given
	 *         by the {@code transform} argument
	 */
	@Override
	public PDFPage atPosition(AffineTransform newPosition) {
		return new PDFPage(doc, page, newPosition, description);
	}

	@Override
	public <T, P, E extends Throwable> T invite
			(ContentVisitor<T, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}

	@Override
	public String toString() {
		return description;
	}
}
