package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;
import java.util.Collection;

/**
 * Represents the whole content in a document page.
 * This variant does not permit content transformation and throws
 * {@code UnsupportedOperationException} when attempting to do so.
 * Because this class is merely a wrapper around the collection of content
 * elements passed in the arguments, it is expected to perform better when
 * compared to {@code ContentsMovable}. The {@code get} method in particular
 * may have faster execution, because there is no copying and wrapping the
 * content elements involved.
 * <p>
 * This class is not thread-safe.
 * @author Singon
 *
 */
class ContentsStatic implements Contents {

	private final Collection<Content> contents;
	
	ContentsStatic(Collection<Content> contents) {
		this.contents = contents;
	}
	
	@Override
	public Collection<Content> get() {
		return contents;
	}

	@Override
	public void transform(AffineTransform transform) {
		throw new UnsupportedOperationException
				("This object does not support transforming content");
	}

}
