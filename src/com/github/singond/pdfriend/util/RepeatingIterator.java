package com.github.singond.pdfriend.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates a collection, returning the same element n times before
 * proceeding to next element.
 * <p>
 * The behaviour of this class is undefined if the underlying collection
 * is modified during iteration.
 *
 * @author Singon
 */
public class RepeatingIterator<T> implements Iterator<T> {

	/** An iterator returning the elements without repeating */
	private final Iterator<T> simpleIter;
	/** How many times to repeat each element before proceeding to next */
	private final int repeatTimes;
	
	/** The last returned element */
	private T currentElement = null;
	/** How many times the current element has been repeated */
	private int currentRepeated = 0;
	
	public RepeatingIterator(Iterable<T> iterable, int repeatTimes) {
		if (iterable == null)
			throw new IllegalArgumentException(
					"The iterable argument must be non-null");
		if (repeatTimes < 1)
			throw new IllegalArgumentException(
					"The number of repetitions must be a positive number");
		
		this.simpleIter = iterable.iterator();
		this.repeatTimes = repeatTimes;
		// Set the repetition count to max to ensure we start by getting
		// an element from the inner iterator
		this.currentRepeated = repeatTimes;
	}
	
	/**
	 * Returns the current element and increases the repeat counter.
	 * @return
	 * @throws NoSuchElementException if the current element has already
	 *         been returned {@code repeatTimes} times
	 */
	private T repeat() {
		if (currentRepeated < repeatTimes) {
			currentRepeated++;
			return currentElement;
		} else {
			throw new NoSuchElementException
					("The number of repetitions for the current element has already been depleted");
		}
	}
	
	/**
	 * Returns true if the current element is still to be repeated,
	 * ie. if the current element has not been called {@code repeatTimes}
	 * yet.
	 * @return true, if a subsequent call to {@link repeat} would suceed
	 */
	private boolean isRepeatable() {
		return currentRepeated < repeatTimes;
	}
	
	@Override
	public boolean hasNext() {
		return isRepeatable() || simpleIter.hasNext();
	}

	@Override
	public T next() {
		if (isRepeatable()) {
			return repeat();
		} else if (simpleIter.hasNext()) {
			T element = simpleIter.next();
			currentElement = element;
			currentRepeated = 1;
			return element;
		} else {
			throw new NoSuchElementException(
					"The last element has already been returned enough times");
		}
	}
}
