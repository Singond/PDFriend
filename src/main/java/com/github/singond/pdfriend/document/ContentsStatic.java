package com.github.singond.pdfriend.document;

import java.util.List;

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
class ContentsStatic implements Contents {

	private final List<Content> contents;

	ContentsStatic(List<Content> contents) {
		this.contents = contents;
	}

	@Override
	public List<Content> get() {
		return contents;
	}

	@Override
	public boolean isEmpty() {
		return contents.isEmpty();
	}
}
