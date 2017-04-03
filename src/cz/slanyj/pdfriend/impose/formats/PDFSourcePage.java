package cz.slanyj.pdfriend.impose.formats;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.impose.SourcePage;
import cz.slanyj.pdfriend.impose.SourcePageVisitor;

/**
 * A container for a PDPage and its parent PDDocument.
 * @author Singon
 *
 */
public class PDFSourcePage implements SourcePage {
	
	/** The page */
	private final PDPage page;
	/** The parent document */
	private final PDDocument doc;
	
	public PDFSourcePage(PDDocument doc, PDPage page) {
		this.doc = doc;
		this.page = page;
	}
	
	public PDFSourcePage(PDDocument doc, int pageNumber) {
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
	public <T, P> T invite(SourcePageVisitor<T, P> visitor, P param) {
		return visitor.visit(this);
	}
}
