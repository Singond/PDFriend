package com.github.singond.pdfriend.reorder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * An implementation of {@code Compacter} in which all slots are of equal size,
 * and which attempts to keep close to the original order, where possible.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
class OptimizingFixedSlotCompacter<T> implements Compacter<T> {

	private final int slotSize;

	private final List<T> placed;

	private final List<List<Element>> slots;

	/**
	 * A buffer of unplaced objects, sorted by their size.
	 */
	private final NumberedQueue<Element> unplaced;

	private int elementCounter = 0;

	private Combiner combiner;

	/** Indicates that this object has already been used. */
	private boolean used;

	OptimizingFixedSlotCompacter(int slotSize) {
		this.slotSize = slotSize;
		placed = new ArrayList<>();
		slots = new ArrayList<>();
		unplaced = new NumberedQueue<>();
		combiner = new Combiner();
	}

	@Override
	public List<T> process(Collection<T> objects, ToIntFunction<T> size) {
		if (used) {
			throw new IllegalStateException
					("This compacter cannot be used more than once");
		} else if (objects == null) {
			throw new NullPointerException("The collection of objects is null");
		} else if (size == null) {
			throw new NullPointerException("The size function is null");
		} else if (objects.isEmpty()) {
			return Collections.emptyList();
		}
		processAll(objects, size);
		used = true;
		return placed;
	}

	private void processAll(Collection<T> objects, ToIntFunction<T> size) {
		for (T t : objects) {
			add(t, size.applyAsInt(t));
		}

		// Try placing unplaced pages into slots.
		// Once no more pages can be placed, add one slot size to the current
		// and repeat. Finish if the increased slot size exceeds number
		// of unplaced elements.
		int slotsToFill = 1;
		int remaining = -1;

		do {
			remaining = unplaced.size();
			if (remaining <= 0) {
				return;         // Finished
			}
			boolean flushed = tryFlush(slotsToFill);
			if (!flushed) slotsToFill++;
		} while (slotsToFill * slotSize <= remaining);

		// Flush the rest
		List<Element> toBePlaced = new ArrayList<>(unplaced.size());
		for (Element element : unplaced.getAllAscending()) {
			toBePlaced.add(element);
		}
		Collections.sort(toBePlaced);
		Collections.sort(slots, new SlotComparator());
		slots.add(toBePlaced);

		for (List<Element> s : slots) {
			for (Element e : s) {
				placed.add(e.value());
			}
		}
	}

	private boolean add(T element, int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Size must not be negative");
		}
		if (element == null) {
			throw new NullPointerException("The element to be added is null");
		}
		return unplaced.add(new Element(element), size);
	}

	/**
	 * Attempts to place the unplaced objects now into n slots.
	 * If any combination of unplaced objects can be made that has size
	 * equal to the size of n slots, place it now.
	 *
	 * @param n the number of slots to fill
	 */
	private boolean tryFlush(int n) {
		List<Integer> sizes = unplaced.numbers();
		List<Integer> sizeComb = combiner.combineToSum(n * slotSize, sizes);
		if (sizeComb != null) {
			List<Element> toBePlaced = new ArrayList<>(sizeComb.size());
			for (int length : sizeComb) {
				toBePlaced.add(unplaced.nextInQueue(length));
			}
			Collections.sort(toBePlaced);
			slots.add(toBePlaced);
			return true;
		} else return false;
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

		public int ordinal() {
			return order;
		}

		@Override
		public int compareTo(Element o) {
			return Integer.compare(order, o.order);
		}
	}

	private class SlotComparator implements Comparator<List<Element>> {

		@Override
		public int compare(List<Element> o1, List<Element> o2) {
			int s1 = o1.stream().mapToInt(i -> i.ordinal()).sum();
			int s2 = o2.stream().mapToInt(i -> i.ordinal()).sum();
			return Integer.compare(s1/o1.size(), s2/o2.size());
		}
	}
}
