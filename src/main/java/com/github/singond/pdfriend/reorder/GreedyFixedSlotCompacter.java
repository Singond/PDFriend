package com.github.singond.pdfriend.reorder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * An implementation of {@code Compacter} in which all slots are of equal size.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
class GreedyFixedSlotCompacter<T> implements Compacter<T> {

	private final int slotSize;

	private final ToIntFunction<T> sizeFunction;

	private final List<T> placed;

	/**
	 * A buffer of unplaced objects, sorted by their size.
	 */
	private final NumberedQueue<Element> unplaced;

	private int elementCounter = 0;

	private Combiner combiner;

	/** Indicates that this object has already been used. */
	private boolean used;

	GreedyFixedSlotCompacter(int slotSize, ToIntFunction<T> sizeFunction) {
		if (slotSize < 1) {
			throw new IllegalArgumentException
					("Slot size must be a positive number");
		} else if (sizeFunction == null) {
			throw new NullPointerException("The size function is null");
		}
		this.slotSize = slotSize;
		this.sizeFunction = sizeFunction;
		placed = new ArrayList<>();
		unplaced = new NumberedQueue<>();
		combiner = new Combiner();
	}

	@Override
	public List<T> process(Collection<T> objects) {
		if (used) {
			throw new IllegalStateException
					("This compacter cannot be used more than once");
		} else if (objects == null) {
			throw new NullPointerException("The collection of objects is null");
		} else if (objects.isEmpty()) {
			return Collections.emptyList();
		}
		processAll(objects);
		used = true;
		return placed;
	}

	private void processAll(Collection<T> objects) {
		for (T t : objects) {
			boolean added = add(t, sizeFunction.applyAsInt(t));
			// If the object was not added to result, it was stored among
			// other unplaced objects. This may have enabled a valid
			// combination of objects to fill a slot.
			// If any such combination exists, it will be placed now.
			if (!added) {
				while (tryFlush(1));
				// (Do nothing, everything happened in the test clause)
			}
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
		for (Element e : toBePlaced) {
			placed.add(e.value());
		}
	}

	private boolean add(T element, int size) {
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
			return true;
		} else {
			unplaced.add(new Element(element), size);
			return false;
		}
	}

	/**
	 * Attempts to place the unplaced objects now into n slots.
	 * If any combination of unplaced objects can be made that has size
	 * equal to the size of n slots, place it now.
	 */
	private boolean tryFlush(int slots) {
		List<Integer> sizes = unplaced.numbers();
		List<Integer> sizeComb = combiner.combineToSum(slots * slotSize, sizes);
		if (sizeComb != null) {
			List<Element> toBePlaced = new ArrayList<>(sizeComb.size());
			for (int length : sizeComb) {
				toBePlaced.add(unplaced.nextInQueue(length));
			}
			Collections.sort(toBePlaced);
			for (Element e : toBePlaced) {
				placed.add(e.value());
			}
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

		@Override
		public int compareTo(Element o) {
			return Integer.compare(order, o.order);
		}
	}
}
