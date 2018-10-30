package com.github.singond.pdfriend.reorder;

import java.util.Collection;
import java.util.List;

/**
 * An extension of {@code Compacter} which limits the number of times
 * the {@link #process} method may be called to one.
 *
 * @author Singon
 * @param <T> the type of the objects being compacted
 */
public interface SingleUseCompacter<T> extends Compacter<T> {

	/**
	 * {@inheritDoc}
	 * This method may only be called once. Subsequent invocations will raise
	 * an {@code IllegalStateException}.
	 *
	 * @param objects the objects to be ordered
	 * @throws IllegalStateException when invoked more than once
	 */
	@Override
	public List<T> process(Collection<T> objects);

	/**
	 * Returns the number of objects spread over more than one section.
	 *
	 * @return the number of objects spread over more than one section
	 */
	public int splitObjects();

	/**
	 * Returns the number of objects which are spread over more sections
	 * than the minimum number of sections able to contain them.
	 *
	 * @return the number of objects spread over more sections than necessary
	 */
	public int suboptimSplitObjects();
}
