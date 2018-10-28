package com.github.singond.pdfriend.reorder;

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
//		ListIterator<Integer> it = values.listIterator(values.size() + 1);
//		while (it.hasPrevious()) {
//			int val = it.previous().intValue();
//			// Add the value and solve the rest
//			List<Integer> rem = combine(sum - val, values.subList(0, values.size()));
//			if rem
//		}
		for (int val : values) {
    		// Add the value and solve the rest
			List<Integer> rem = combine(sum - val, values.subList(1, values.size() + 1));
    		if (rem != null) {
    			rem.add(Integer.valueOf(val));
    			return rem;
    		}
		}
		return null;
	}
}
