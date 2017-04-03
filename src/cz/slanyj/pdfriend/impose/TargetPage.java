package cz.slanyj.pdfriend.impose;

import java.awt.geom.AffineTransform;

import cz.slanyj.pdfriend.impose.formats.PDFSourcePage;

/**
 * A wrapper object representing a single input page in an output page,
 * including its source and properties in the new document, like position.
 * @author Singon
 *
 */
public class TargetPage {

	/** The input page (a page from the input docoument). */
	private final PDFSourcePage sourcePage;
	/** The position on the output page. */
	private final AffineTransform position;
	
	public TargetPage(PDFSourcePage page, AffineTransform position) {
		this.sourcePage = page;
		this.position = position;
	}

	public PDFSourcePage getSourcePage() {
		return sourcePage;
	}

	public AffineTransform getPosition() {
		return position;
	}
}
