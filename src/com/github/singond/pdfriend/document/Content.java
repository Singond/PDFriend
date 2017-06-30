package com.github.singond.pdfriend.document;

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
	public final AffineTransform getPosition() {
		return new AffineTransform(position);
	}
	
	/**
	 * Transforms this piece of content using the given transformation.
	 * If the original transformation matrix is T, this method changes
	 * this matrix to T2, such that T2 is the product of {@code trans}
	 * and T. In mathematical formulation:
	 * <pre>[T2] = [trans] x [T]</pre>
	 * @param trans the transformation matrix to be applied on top of
	 *        the current transformation of this Content element
	 */
	public abstract Content transform(AffineTransform trans);
	
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
	 */
	public final class Movable {
		/**
		 * Matrix accumulating the transformations.
		 * This starts with identity transformation and in the end is
		 * used to transform the current position of this Content element.
		 */
		private final AffineTransform transform = new AffineTransform();
		
		/**
		 * Provides direct access to the current position.
		 * Changes to the returned object will be reflected in the Content's
		 * position.
		 * @return The internal transformation matrix.
		 */
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
