package com.github.singond.pdfriend.reorder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;

public class CoinChangeProblem {

	private Combiner combiner;

	@Before
	public void init() {
		combiner = new Combiner();
	}

	private void combine(int sum, Integer... values) {
		List<Integer> s = combiner.combineToSum(sum, Arrays.asList(values));
		int foundsum = s.stream().mapToInt(Integer::intValue).sum();
		assertEquals("Bad sum of solution returned", sum, foundsum);
		System.out.format("Picking from %s to a total of %d: %s", values, sum, s);
	}

	public void findingSolution() {
		combine(5, 1, 2, 3);
	}
}
