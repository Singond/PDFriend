package com.github.singond.pdfriend.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class RepeatingIteratorTest {

	@Test
	public void simple() {
		List<String> list = Arrays.asList(new String[]{"A", "B", "C", "D", "E"});
		RepeatingIterator<String> riter = new RepeatingIterator<>(list, 2);
		while (riter.hasNext()) {
			System.out.print(riter.next());
		}
		System.out.println();
	}
}
