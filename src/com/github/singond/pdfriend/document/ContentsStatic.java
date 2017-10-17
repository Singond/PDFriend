package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.stream.Collectors;

import com.github.singond.pdfriend.document.Content.Movable;

/**
 * Represents the whole content in a document page.
 * The content elements are ordered.
 * <p>
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
class ContentsStatic extends Contents {

	private final List<Content> contents;
	
	ContentsStatic(List<Content> contents) {
		this.contents = contents;
	}
	
	@Override
	public List<Content> get() {
		return contents;
	}

	@Override
	public void transform(AffineTransform transform) {
		throw new UnsupportedOperationException
				("This object does not support transforming content");
	}

	@Override
	List<Movable> getMovable() {
		return contents.stream()
		               .map(c -> c.new Movable())
		               .collect(Collectors.toList());
	}

	@Override
	public boolean isEmpty() {
		return contents.isEmpty();
	}
}
