package com.github.singond.pdfriend.util;

import java.util.Collection;

public class Formatting {

	private Formatting() {
		throw new UnsupportedOperationException("Non-instantiable class");
	}

	/**
	 * Produces a short description of the contents of a given collection.
	 * For small collections (whose size does not exceed the given limit),
	 * the output is the collection's own {@code toString} method.
	 * For longer collections, elements after the limit are truncated and
	 * their count is appended.
	 * <p>
	 * Given a list of the capital letters of the English alphabet (A-Z),
	 * the output will look similar to this (the limit is set to {@code 4}):
	 * <pre>
	 * [A, B, C, D... (22 more)]
	 * </pre>
	 *
	 * @param <T> the type of elements in the collection
	 * @param coll the collection whose contents are to be formatted
	 * @param limit the maximum number of elements to show
	 * @return a string builder representing the first {@code limit} elements
	 *         of {@code coll} and the number of remaining elements
	 */
	public static <T> StringBuilder listDigest(Collection<T> coll, int limit) {
		StringBuilder sb = new StringBuilder();
		if (coll.size() <= limit) {
			sb.append(coll.toString());
		} else {
			sb.append("[");
			int index = 0;
			for (T element : coll) {
				if (index++ < limit) {
					sb.append(element).append(", ");
				}
			}
			sb.setLength(sb.length() - 2);
			sb.append("... (" + (coll.size() - limit) + " more)]");
		}
		return sb;
	}
}
