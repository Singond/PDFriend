package cz.slanyj.pdfriend.document;

import java.awt.geom.AffineTransform;

/**
 * Represents a unit of content in a document page.
 * This is a part of the uniform document interface shared between modules.
 * 
 * @author Singon
 *
 */
public abstract class Content {

	/** The position on the output page. */
	protected final AffineTransform position;
	
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

	/**
	 * Gets the position of this piece of content as a transformation matrix.
	 * @return A defensive copy of the transformation matrix.
	 */
	public AffineTransform getPosition() {
		return new AffineTransform(position);
	}
	
	/**
	 * Transforms this piece of content using the given transformation
	 * (this will usually be only translation or rotation).
	 */
	public abstract Content transform(AffineTransform position);
	
	/**
	 * Invite a ContentVisitor
	 * @param <T> Return type for the visitor.
	 * @param <P> Parameter type for the visitor.
	 * @param <E> Exception type thrown by the visitor.
	 */
	public abstract <T, P, E extends Throwable> T invite(ContentVisitor<T, P, E> visitor, P param) throws E;
	
	/**
	 * A helper for a Content object.
	 * Enables bypassing new instance creation if many transformations
	 * are to be applied to an immutable Content object in sequence.
	 * This is accomplished by accumulating the transformations in
	 * a separate field and then applying them all at once.
	 * 
	 * @author Singon
	 * 
	 */
	public final class Movable {
		/** Matrix accumulating the transformations */
		private final AffineTransform transform = new AffineTransform(position);
		
		public AffineTransform getTransform() {
			return transform;
		}
		
		/**
		 * Gets the transformed Content object.
		 * @return
		 */
		public Content transformed() {
			return Content.this.transform(transform);
		}
	}
}
