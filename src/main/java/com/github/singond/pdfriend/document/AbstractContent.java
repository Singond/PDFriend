package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;

/**
 * Represents a unit of content in a document page.
 * This is a part of the uniform document interface shared between modules.
 *
 * @author Singon
 *
 */
public abstract class AbstractContent implements Content {

	/** The position on the output page. */
	private final AffineTransform position;

	/**
	 * Creates a new Content in the given position.
	 *
	 * @param position
	 */
	public AbstractContent(AffineTransform position) {
		this.position = position;
	}

	/**
	 * Creates a new Content in the default position
	 * (where position is the identity matrix).
	 */
	public AbstractContent() {
		this(new AffineTransform());
	}

	@Override
	public final AffineTransform getPosition() {
		return new AffineTransform(position);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation works by transforming the position to T2 in the
	 * aforementioned way and calling {@code atPosition(T2)} internally.
	 *
	 * @param transform {@inheritDoc}
	 * @return          {@inheritDoc}
	 */
	@Override
	public final AbstractContent transform(AffineTransform transform) {
		AffineTransform newPosition = new AffineTransform(position);
		newPosition.preConcatenate(transform);
		return atPosition(newPosition);
	}

	@Override
	public abstract AbstractContent atPosition(AffineTransform position);

	@Override
	public abstract <T, P, E extends Throwable> T invite
			(ContentVisitor<T, P, E> visitor, P param) throws E;
}
