package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import com.github.singond.pdfriend.document.Content.Movable;

/**
 * Represents the whole content in a document page.
 * The content elements are ordered.
 * <p>
 * The view is not live, ie. adding or removing content elements
 * and changes to their position will not be visible in the page.
 * Other changes to the content elements (if allowed by the implementation),
 * like modifying their underlying data, may or may not result in changes
 * in the page, depending on the implementation of {@code Content}.
 * <p>
 * This class is not thread-safe.
 * @author Singon
 *
 */
class ContentsMovable extends Contents {

	private final List<Content.Movable> contents;
	
	/**
	 * Constructs a new instance by shallowly copying the given content.
	 * @param contents all content of the page, wrapped in an object with
	 *        mutable position
	 */
	ContentsMovable(List<Content.Movable> contents) {
		this.contents = new ArrayList<>(contents);
	}
	
	@Override
	public List<Content> get() {
		List<Content> result = new ArrayList<>(contents.size());
		for (Content.Movable cm : contents) {
			result.add(cm.transformed());
		}
		return result;
	}
	
	@Override
	public void transform(AffineTransform transform) {
		for (Content.Movable cm : contents) {
			cm.getTransform().preConcatenate(transform);
		}
	}

	@Override
	List<Movable> getMovable() {
		return contents;
	}

	@Override
	public boolean isEmpty() {
		return contents.isEmpty();
	}
}
