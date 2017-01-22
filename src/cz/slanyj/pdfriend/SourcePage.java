package cz.slanyj.pdfriend;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

/**
 * A container for a PDPage and its parent PDDocument.
 * @author Sorondil
 *
 */
public class SourcePage {
	
	/** The page */
	private final PDPage page;
	/** The parent document */
	private final PDDocument doc;
	
	public SourcePage(PDPage page, PDDocument doc, PDFormXObject form) {
		this.page = page;
		this.doc = doc;
	}

	public PDPage getPage() {
		return page;
	}

	public PDDocument getDoc() {
		return doc;
	}
}
