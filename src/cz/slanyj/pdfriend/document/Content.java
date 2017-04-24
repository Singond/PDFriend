package cz.slanyj.pdfriend.document;

import java.awt.geom.AffineTransform;

/**
 * Represents a unit of content in a document page.
 * This is a part of the uniform document interface shared between modules.
 * @author Singon
 *
 */
public abstract class Content {

	/** The position on the output page. */
	private AffineTransform position;

	public AffineTransform getPosition() {
		return position;
	}

	public void setPosition(AffineTransform position) {
		this.position = position;
	}
	
	
}
