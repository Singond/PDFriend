package com.github.singond.pdfriend.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.singond.pdfriend.document.Content.Movable;

/**
 * Creates {@code Contents} objects.
 *
 * @author Singon
 */
public final class ContentsFactory {

	private ContentsFactory() {
		throw new UnsupportedOperationException("Non-instantiable class");
	}

	/**
	 * Returns a new instance of Contents containing all content elements
	 * given in the argument. The returned object allows transformations.
	 * @param contents
	 * @return a new instance of Contents
	 */
	public static TransformableContents merge(Collection<Contents> contents) {
		List<Content.Movable> all = new ArrayList<>();
		for (Contents c : contents) {
			all.addAll(makeMovable(c));
		}
		return new ContentsMovable(all);
	}

	private static List<Movable> makeMovable(Contents contents) {
		// Bypass new object creation if possible
		if (contents instanceof ContentsMovable) {
			return ((ContentsMovable) contents).getMovable();
		}

		// Otherwise do it the normal way
    	return contents.get().stream()
    	               .map(c -> c.new Movable())
    	               .collect(Collectors.toList());
    }
}
