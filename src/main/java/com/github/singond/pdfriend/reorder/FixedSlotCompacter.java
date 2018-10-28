package com.github.singond.pdfriend.reorder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * An implementation of {@code Compacter} in which all slots are of equal size.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
class FixedSlotCompacter<T> implements Compacter<T> {

	private final int slotSize;

	private final List<T> placed;

	/**
	 * A buffer of unplaced objects, sorted by their size.
	 */
	private final NumberedQueue<Element> unplaced;

	private int elementCounter = 0;

	private Combiner combiner;

	FixedSlotCompacter(int slotSize) {
		this.slotSize = slotSize;
		placed = new ArrayList<>();
		unplaced = new NumberedQueue<>();
		combiner = new Combiner();
	}

	@Override
	public List<T> process(Collection<T> objects, ToIntFunction<T> size) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	private void add(T element, int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Size must not be negative");
		}
		if (element == null) {
			throw new NullPointerException("The element to be added is null");
		}

		// If the size is an integer number of slots, place it now,
		// else store it away for later placement
		if (size % slotSize == 0) {
			placed.add(element);
		} else {
			unplaced.add(new Element(element), size);
		}
	}

	/**
	 * Attempts to place the unplaced objects now into n slots.
	 * If any combination of unplaced objects can be made that has size
	 * equal to the size of n slots, place it now.
	 */
	private void tryFlush(int slots) {
		List<Integer> sizes = unplaced.numbers();
		List<Integer> sizeComb = combiner.combineToSum(slots * slotSize, sizes);
		if (sizeComb != null) {
			for (int length : sizeComb) {
				placed.add(unplaced.nextInQueue(length).value());
			}
		}
	}

	/**
	 * A wrapper for the contained objects which allows for ordering them
	 * in their order of appearance.
	 */
	private class Element implements Comparable<Element>{
		private final T value;
		private final int order;

		public Element(T value) {
			this.value = value;
			this.order = elementCounter++;
		}

		public T value() {
			return value;
		}

		@Override
		public int compareTo(Element o) {
			return Integer.compare(order, o.order);
		}
	}
}
