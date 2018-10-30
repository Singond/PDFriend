package com.github.singond.pdfriend.reorder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.ParameterDelegate;

/**
 * Determines which imposable type is to be executed, based on the
 * command-line arguments.
 *
 * @author Singon
 *
 */
class ReorderableResolver implements ParameterDelegate {

	@ParametersDelegate
	private ReverseCli reverse = new ReverseCli();

	@ParametersDelegate
	private CompactCli compact = new CompactCli();

	/**
	 * All options for the reordering type should be here.
	 * If any is omitted from this set, it will be ignored in the command line.
	 */
	private final Set<? extends ReorderableCli<?>> taskTypes =
			new HashSet<>(Arrays.asList(reverse, compact));

	/**
	 * Resolves the type of imposed document from the command line
	 * arguments, creates an instance of an implementing class
	 * and passes necessary parameters to it.
	 * <p>
	 * If more than one type is given in the command line (eg.
	 * {@code --nup 2x4 --booklet}), this method will throw an
	 * {@code IllegalStateException}. To avoid this, clients should check
	 * integrity by calling {@code postParse};
	 *
	 * @throws IllegalStateException when none or more than one imposable
	 *         types is given
	 */
	public Reorderable getReorderingTask() {
		ReorderableCli<?> selected = null;
		for (ReorderableCli<?> i : taskTypes) {
			if (i.isSet()) {
				if (selected == null) {
					selected = i;
				} else {
					throw new IllegalStateException
							("More than one reordering type has been given");
				}
			}
		}
		if (selected == null) {
			throw new IllegalStateException("No reordering type has been given");
		} else {
			return selected.getReorderable();
		}
	}

	/**
	 * Verifies that no more than one reorderable type is given in the
	 * command line and throws an exception otherwise.
	 * @throws ParameterConsistencyException if more than one reorderable type
	 *         appears among the command-line arguments
	 */
	@Override
	public void postParse() throws ParameterConsistencyException {
		int imposablesCount = countReorderables();
		if (imposablesCount > 1) {
			throw new ParameterConsistencyException("Only one type of reordering can be given", null);
		} else if (imposablesCount < 1) {
			throw new ParameterConsistencyException("The type of reordering must be given", null);
		}
	}

	/**
	 * Counts how many reorderable types are given in the command line.
	 */
	private int countReorderables() {
		// TODO Avoid checking all?
		/*
		 * This cast is assumed to be overflow-safe.
		 * If the number of reordering types set in the command line overflows
		 * {@code int}, we're in deep trouble anyway!
		 */
		return (int) taskTypes.stream().filter(i->i.isSet()).count();
	}
}
