package com.github.singond.pdfriend.reorder;

/**
 * A compacter for arbitrary objects. This takes a sequence of 'slots'
 * with integer length and a number of objects with specified 'size',
 * which is an integer number indicating how many units they take up
 * in the slot length.
 *
 * It then attempts to order the objects so that when placed one after another
 * into the slots, the number of cases where an object overlaps slot boundary
 * is minimized. It is generally not required to find the optimal solution.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
interface Compacter<T> {

	/**
	 * Adds an element to the collection of objects to be compacted.
	 *
	 * @param element the object to be added
	 * @param size the 'size' taken up by the object in slot
	 */
	public void add(T element, int size);
}
