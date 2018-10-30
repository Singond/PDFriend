package com.github.singond.pdfriend.reorder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class NumberedQueueTest {

	private NumberedQueue<String> nq;

	@Before
	public void init() {
		nq = new NumberedQueue<>();
	}

	@Test
	public void numbersTest() {
		nq.add("A", 1);
		nq.add("F", 6);
		nq.add("F", 6);
		nq.add("B", 2);
		List<Integer> numbers = nq.numbers();
		Collections.sort(numbers);
		List<Integer> expected = Arrays.asList(1, 2, 6, 6);
		assertEquals("Wrong numbers returned", expected, numbers);
	}
}
