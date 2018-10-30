package com.github.singond.pdfriend.reorder;

import java.util.Collection;
import java.util.List;

/**
 * A compacter for arbitrary objects. This takes a sequence of 'sections'
 * with integer length and a number of objects with specified 'size',
 * which is an integer number indicating how many units they take up
 * in the section.
 *
 * It then attempts to order the objects so that when placed one after another
 * into the sections, the number of cases where an object overlaps section
 * boundary is minimized.
 * It is generally not required to find the optimal solution.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
interface Compacter<T> {

	/**
	 * Orders the elements in the given collection in order to compact them
	 * as described in the interface description.
	 *
	 * @param objects the objects to be ordered
	 */
	public List<T> process(Collection<T> objects);
}
