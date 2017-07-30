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
	private final AffineTransform position;
	
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
	 * @return a defensive copy of the transformation matrix
	 */
	public final AffineTransform getPosition() {
		return new AffineTransform(position);
	}
	
	/**
	 * Returns a piece of content which is a identical to this instance
	 * transformed using the given transformation, keeping the original
	 * unchanged.
	 * If the original transformation matrix is T, this method returns
	 * content at position given by matrix T2, such that T2 is the product
	 * of {@code trans} and T. In mathematical formulation:
	 * <pre>[T2] = [trans] x [T]</pre>
	 * This implementation works by transforming the position to T2 in the
	 * aforementioned way and calling {@code atPosition(T2)} internally.
	 * @param transform the transformation matrix to be applied on top of
	 *        the current transformation of this Content element
	 * @return an instance representing the transformed piece of content
	 */
	public final Content transform(AffineTransform transform) {
		AffineTransform newPosition = new AffineTransform(position);
		newPosition.preConcatenate(transform);
		return atPosition(newPosition);
	}
	
	/**
	 * Returns a piece of content which is a identical to this instance
	 * moved to the position specified by the given transformation matrix,
	 * keeping the original unchanged.
	 * @param position the transformation matrix to be used as the position
	 *        of the returned Content element
	 * @return the transformed piece of content
	 */
	public abstract Content atPosition(AffineTransform position);
	
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
		 * The current position of the Content element.
		 * This is the transformation matrix representing the position of
		 * this Content element if it were built at this moment using the
		 * <code>transformed</code> method.
		 * The matrix is initialized with the current transformation and
		 * serves to accumulate transformations to the Content element.
		 */
		private final AffineTransform transform = new AffineTransform(position);
		
		/**
		 * Provides direct access to the current position.
		 * Changes to the returned object will be reflected in the Content's
		 * position.
		 * @return the internal transformation matrix
		 */
		public AffineTransform getTransform() {
			return transform;
		}
		
		/**
		 * Gets the transformed Content object.
		 * @return
		 */
		public Content transformed() {
			return Content.this.atPosition(transform);
		}
	}
}
