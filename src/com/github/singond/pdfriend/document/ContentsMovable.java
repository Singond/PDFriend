package com.github.singond.pdfriend.document;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.singond.pdfriend.document.Content.Movable;

/**
 * Represents the whole content in a document page.
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

	private final Set<Content.Movable> contents;
	
	/**
	 * Constructs a new instance by shallowly copying the given content.
	 * @param contents all content of the page, wrapped in an object with
	 *        mutable position
	 */
	ContentsMovable(Collection<Content.Movable> contents) {
		this.contents = new HashSet<>(contents);
	}
	
	@Override
	public Collection<Content> get() {
		Collection<Content> result = new ArrayList<>(contents.size());
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
	Collection<Movable> getMovable() {
		return contents;
	}
}
