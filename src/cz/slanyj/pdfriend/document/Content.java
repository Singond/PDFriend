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
	
	/**
	 * Creates a new Content in the given position.
	 * @param position
	 */
	public Content(AffineTransform position) {
		this.position = position;
	}
	
	/**
	 * Creates a new Content in the default position
	 * (where position is the identity matrix).
	 */
	public Content() {
		this(new AffineTransform());
	}

	public AffineTransform getPosition() {
		return position;
	}

	public void setPosition(AffineTransform position) {
		this.position = position;
	}
	
	/**
	 * Invite a ContentVisitor
	 * @param T Return type for the visitor.
	 * @param P Parameter type for the visitor.
	 */
	public abstract <T, P> T invite(ContentVisitor<T, P> visitor, P param);
}
