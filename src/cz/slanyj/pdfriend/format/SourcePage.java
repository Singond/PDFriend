package cz.slanyj.pdfriend.format;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * A container for a PDPage and its parent PDDocument.
 * @author Singon
 *
 */
public class SourcePage {
	
	/** The page */
	private final PDPage page;
	/** The parent document */
	private final PDDocument doc;
	
	public SourcePage(PDDocument doc, PDPage page) {
		this.doc = doc;
		this.page = page;
	}
	
	public SourcePage(PDDocument doc, int pageNumber) {
		this.doc = doc;
		this.page = doc.getPage(pageNumber);
	}

	public PDPage getPage() {
		return page;
	}

	public PDDocument getDoc() {
		return doc;
	}
}
