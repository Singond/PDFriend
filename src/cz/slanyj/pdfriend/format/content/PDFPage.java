package cz.slanyj.pdfriend.format.content;

import java.awt.geom.AffineTransform;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.ContentVisitor;

/**
 * Content of a page formed by a page of a PDF document.
 * @author Singon
 *
 */
public class PDFPage extends Content {
	
	/** The page */
	private final PDPage page;
	/** The parent document */
	private final PDDocument doc;
	
	public PDFPage(PDDocument doc, PDPage page, AffineTransform position) {
		super(position);
		this.doc = doc;
		this.page = page;
	}
	public PDFPage(PDDocument doc, PDPage page) {
		super();
		this.doc = doc;
		this.page = page;
	}
	/**
	 * @param pageNumber The index of the desired page (numbered from 0).
	 */
	public PDFPage(PDDocument doc, int pageNumber) {
		super();
		this.doc = doc;
		this.page = doc.getPage(pageNumber);
	}

	public PDPage getPage() {
		return page;
	}

	public PDDocument getDoc() {
		return doc;
	}
	
	@Override
	public <T, P, E extends Throwable> T invite(ContentVisitor<T, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}
}
