package cz.slanyj.pdfriend.format;

import java.awt.geom.AffineTransform;

/**
 * A wrapper object representing a single input page in an output page,
 * including its source and properties in the new document, like position.
 * @author Singon
 *
 */
public class TargetPage {

	/** The input page (a page from the input docoument). */
	private final SourcePage sourcePage;
	/** The position on the output page. */
	private final AffineTransform position;
	
	public TargetPage(SourcePage page, AffineTransform position) {
		this.sourcePage = page;
		this.position = position;
	}

	public SourcePage getSourcePage() {
		return sourcePage;
	}

	public AffineTransform getPosition() {
		return position;
	}
}
