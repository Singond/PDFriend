package com.github.singond.pdfriend.util;

import java.util.List;

public class Formatting {

	private Formatting() {
		throw new UnsupportedOperationException("Non-instantiable class");
	}

	/**
	 * Produces a short description of the contents of a given list.
	 * For short lists (whose length does not exceed the given limit),
	 * the output is the list's own {@code toString} method.
	 * For longer lists, elements after the limit are truncated and their
	 * count is appended.
	 * <p>
	 * Given a list of the capital letters of the English alphabet (A-Z),
	 * the output will look similar to this (the limit is set to {@code 4}):
	 * <pre>
	 * [A, B, C, D... (22 more)]
	 * </pre>
	 *
	 * @param list the list whose contents are to be formatted
	 * @param limit the maximum number of elements to show
	 * @return a string builder representing the first {@code limit} elements
	 *         of {@code list} and the number of remaining elements
	 */
	public static StringBuilder listDigest(List<?> list, int limit) {
		StringBuilder sb = new StringBuilder();
		if (list.size() <= limit) {
			sb.append(list.toString());
		} else {
			sb.append(list.subList(0, limit).toString());
			sb.setLength(sb.length() - 1);
			sb.append("... (" + (list.size() - limit) + " more)]");
		}
		return sb;
	}
}
