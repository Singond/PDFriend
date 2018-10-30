package com.github.singond.pdfriend.reorder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * An implementation of {@code Compacter} in which all sections are of equal size.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
class GreedyFixedSectionCompacter<T> implements Compacter<T> {

	private final int sectionSize;

	private final ToIntFunction<T> sizeFunction;

	private final List<T> placed;

	/**
	 * A buffer of unplaced objects, sorted by their size.
	 */
	private final NumberedQueue<Element> unplaced;

	private int elementCounter = 0;

	private CoinChangeSolver combiner;

	/** Indicates that this object has already been used. */
	private boolean used;

	public GreedyFixedSectionCompacter(int sectionSize,
			ToIntFunction<T> sizeFunction) {
		if (sectionSize < 1) {
			throw new IllegalArgumentException
					("Section size must be a positive number");
		} else if (sizeFunction == null) {
			throw new NullPointerException("The size function is null");
		}
		this.sectionSize = sectionSize;
		this.sizeFunction = sizeFunction;
		placed = new ArrayList<>();
		unplaced = new NumberedQueue<>();
		combiner = new CoinChangeSolver();
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
			// combination of objects to fill a section.
			// If any such combination exists, it will be placed now.
			if (!added) {
				while (tryFlush(1));
				// (Do nothing, everything happened in the test clause)
			}
		}

		// Try placing unplaced pages into sections.
		// Once no more pages can be placed, add one section size to the current
		// and repeat. Finish if the increased section size exceeds number
		// of unplaced elements.
		int sectionsToFill = 1;
		int remaining = -1;

		do {
			remaining = unplaced.size();
			if (remaining <= 0) {
				return;         // Finished
			}
			boolean flushed = tryFlush(sectionsToFill);
			if (!flushed) sectionsToFill++;
		} while (sectionsToFill * sectionSize <= remaining);

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

		// If the size is an integer number of sections, place it now,
		// else store it away for later placement
		if (size % sectionSize == 0) {
			placed.add(element);
			return true;
		} else {
			unplaced.add(new Element(element), size);
			return false;
		}
	}

	/**
	 * Attempts to place the unplaced objects now into n sections.
	 * If any combination of unplaced objects can be made that has size
	 * equal to the size of n sections, place it now.
	 */
	private boolean tryFlush(int sects) {
		List<Integer> sizes = unplaced.numbers();
		List<Integer> sizeComb = combiner.combineToSum(sects * sectionSize, sizes);
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
