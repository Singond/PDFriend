package com.github.singond.pdfriend.reorder;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class CompacterTest {

	private void fixedNoRemainder(int sectSize, Compacter<Integer> compacter,
			Integer... objectSizes) {
		List<Integer> sizes = Arrays.asList(objectSizes);
		int totalLength = sizes.stream().mapToInt(Integer::intValue).sum();
		int sects = (totalLength + sectSize - 1)/sectSize;
		List<Integer> compacted = compacter.process(sizes);
		SortedSet<Integer> compactedSums = new TreeSet<>(partialSums(compacted));
		for (int sect = 1; sect <= sects; sect++) {
			int sectPartialSum = sect * sectSize;
			boolean noOverflow = compactedSums.contains(sectPartialSum);
			if (!noOverflow) {
				String msg = String.format(
						"Compacting %s into %d sections failed due to"
						+ " overflow at section %d: %s",
						sizes, sectSize, sect, compacted);
				System.out.println(msg);
				fail(msg);
			}
		}
		System.out.format("Compacted %s into sections of size %d: %s%n",
		                  sizes, sectSize, compacted);
	}

	@Test
	public void fixedSectNoRemainder() {
//		fixedNoRemainder(4, greedyFixedSect(4),
//				2, 2, 1, 3, 1, 1, 2, 2, 3, 3);
		fixedNoRemainder(4, optimizingFixedSect(4),
				2, 2, 1, 3, 1, 1, 2, 2, 3, 3);
	}

	private static List<Integer> partialSums(List<Integer> sequence) {
		List<Integer> result = new ArrayList<>(sequence.size());
		int partialSum = 0;
		for (int term : sequence) {
			partialSum += term;
			result.add(partialSum);
		}
		return result;
	}

	private static Compacter<Integer> greedyFixedSect(int sectSize) {
		return new GreedyFixedSectionCompacter<>(sectSize, e -> e.intValue());
	}

	private static Compacter<Integer> optimizingFixedSect(int sectSize) {
		return new OptimizingFixedSectionCompacter<>(sectSize, e -> e.intValue());
	}
}
