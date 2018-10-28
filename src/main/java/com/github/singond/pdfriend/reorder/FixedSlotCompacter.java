package com.github.singond.pdfriend.reorder;

import java.util.ArrayList;
import java.util.List;

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
	private final NumberedQueue<List<Element>> unplaced;

	private int elementCounter = 0;

	FixedSlotCompacter(int slotSize) {
		this.slotSize = slotSize;
		placed = new ArrayList<>();
		unplaced = new NumberedQueue<>();
	}

	@Override
	public void add(T element, int size) {

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

		@Override
		public int compareTo(Element o) {
			return Integer.compare(order, o.order);
		}
	}
}
