package com.github.singond.pdfriend.reorder;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class CompacterTest {

	private void fixedNoRemainder(int slotSize, Integer... objectSizes) {
		List<Integer> sizes = Arrays.asList(objectSizes);
		int totalLength = sizes.stream().mapToInt(Integer::intValue).sum();
		int slots = (totalLength + slotSize - 1)/slotSize;
		FixedSlotCompacter<Integer> fsc = new FixedSlotCompacter<>(slotSize);
		List<Integer> compacted = fsc.process(sizes, (e) -> e.intValue());
		SortedSet<Integer> compactedSums = new TreeSet<>(partialSums(compacted));
		for (int slot = 1; slot <= slots; slot++) {
			int slotPartialSum = slot * slotSize;
			boolean noOverflow = compactedSums.contains(slotPartialSum);
			if (noOverflow) {
				System.out.format("Compacted %s into slots of size %d: %s%n",
				                  sizes, slotSize, compacted);
			} else {
				fail(String.format("Overflow at slot %d", slot));
			}
		}
	}

	@Test
	public void fixedSlotNoRemainder() {
		fixedNoRemainder(2, 2, 2, 2, 3, 1, 1, 3);
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
}
