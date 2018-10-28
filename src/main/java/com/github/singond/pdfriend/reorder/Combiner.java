package com.github.singond.pdfriend.reorder;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A helper object for combining numbers into a given sum,
 * essentially solving a variation of the Coin Change Problem.
 *
 * @author Singon
 */
class Combiner {

	public List<Integer> combineToSum(int sum, List<Integer> values) {
		values = new ArrayList<>(values);
		Collections.sort(values);
		Collections.reverse(values);
		return combine(sum, values);
	}

	// Requires 'values' to be sorted
	private List<Integer> combine(int sum, List<Integer> values) {
		if (sum == 0) {
			// Solution found: return empty list
			// The list must be mutable because solution will be written to it
			return new ArrayList<>();
		} else if (sum < 0) {
			// No solution for picked values: return null to indicate this
			return null;
		}
		int index = 0;
		for (int val : values) {
			// Add the value and solve the rest without the added value
			List<Integer> rem = combine(sum - val, new GapList<>(values, index));
			if (rem != null) {
				rem.add(Integer.valueOf(val));
				return rem;
			}
			index++;
		}
		return null;
	}

	/**
	 * A view of a list with one element removed.
	 *
	 * @author Singon
	 * @param <T> the type of elements contained in this list
	 */
	private static class GapList<T> extends AbstractList<T> {
		/** Backing list. */
		private final List<T> wholeList;
		/** Index of the missing element in the backing list. */
		private final int missingElement;

		GapList(List<T> list, int missingElement) {
			if (list == null) {
				throw new NullPointerException("The list is null");
			} else if (list.isEmpty()) {
				throw new IllegalArgumentException("The list is empty");
			}
			this.wholeList = list;
			this.missingElement = missingElement;
		}

		@Override
		public T get(int index) {
			if (index >= size()) {
				throw new IndexOutOfBoundsException
						("index: " + index + ", size: " + size());
			} else if (index < missingElement) {
				return wholeList.get(index);
			} else {
				return wholeList.get(index + 1);
			}
		}

		@Override
		public int size() {
			return wholeList.size() - 1;
		}
	}
}
