package com.github.singond.pdfriend.reorder;

import java.util.ArrayList;
import java.util.List;

/**
 * An compacter for arbitrary objects. This takes a sequence of 'slots'
 * with integer length and a number of objects with specified 'size',
 * which is an integer number indicating how many units they take up
 * in the slot length.
 *
 * It then attempts to order the objects so that when placed one after another
 * into the slots, the number of cases where an object overlaps slot boundary
 * is minimized. It is not required to find the optimal solution.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
class FixedSlotCompacter<T> {

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
