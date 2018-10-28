package com.github.singond.pdfriend.reorder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CoinChangeProblem {

	private Combiner combiner;

	@Before
	public void init() {
		combiner = new Combiner();
	}

	private void solvable(int sum, Integer... values) {
		List<Integer> s = combiner.combineToSum(sum, Arrays.asList(values));
		int foundsum = s.stream().mapToInt(Integer::intValue).sum();
		assertEquals("Bad sum of solution returned", sum, foundsum);
		System.out.format("Picking from %s to a total of %d: %s%n",
				Arrays.asList(values), sum, s);
	}

	private void unsolvable(int sum, Integer... values) {
		List<Integer> s = combiner.combineToSum(sum, Arrays.asList(values));
		assertEquals("Found solution where none exists", null, s);
		System.out.format("Picking from %s to a total of %d: no solution%n",
		                  Arrays.asList(values), sum);
	}

	@Test
	public void findingSolution() {
		solvable(5, 1, 2, 3);
		solvable(13, 1, 1, 2, 3, 6, 8);
		solvable(37, 1, 1, 2, 3, 6, 8, 4, 4, 9, 7, 16, 10);
	}

	@Test
	public void findingNoSolution() {
		unsolvable(6, 1, 3);
	}
}
