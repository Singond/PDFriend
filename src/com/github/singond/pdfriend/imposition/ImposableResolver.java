package com.github.singond.pdfriend.imposition;

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
class ImposableResolver implements ParameterDelegate {

	@ParametersDelegate
	private BookletCli booklet = new BookletCli();
	
	@ParametersDelegate
	private NUpCli nup = new NUpCli();
	
	@ParametersDelegate
	private OverlayCli overlay = new OverlayCli();
	
	/**
	 * All options for the imposable type should be here.
	 * If any is omitted from this set, it will be ignored in the command line.
	 */
	private final Set<? extends ImposableCli<?>> imposables =
			new HashSet<>(Arrays.asList(booklet, nup, overlay));
	
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
	public Imposable getImpositionTask() {
		ImposableCli<?> selected = null;
		for (ImposableCli<?> i : imposables) {
			if (i.isSet()) {
				if (selected == null) {
					selected = i;
				} else {
					throw new IllegalStateException
							("More than one imposition type has been given");
				}
			}
		}
		if (selected == null) {
			throw new IllegalStateException("No imposition type has been given");
		} else {
			return selected.getImposable();
		}
	}

	/**
	 * Verifies that no more than one imposable type is given in the
	 * command line and throws an exception otherwise.
	 * @throws ParameterConsistencyException if more than one imposable type
	 *         appears among the command-line arguments
	 */
	@Override
	public void postParse() throws ParameterConsistencyException {
		int imposablesCount = countImposables();
		if (imposablesCount > 1) {
			throw new ParameterConsistencyException("Only one type of imposition can be given", null);
		} else if (imposablesCount < 1) {
			throw new ParameterConsistencyException("The type of imposition must be given", null);
		}
	}
	
	/**
	 * Counts how many imposable types are given in the command line.
	 */
	private int countImposables() {
		// TODO Avoid checking all?
		/*
		 * This cast is assumed to be overflow-safe.
		 * If the number of imposable types set in the command line overflows
		 * {@code int}, we're in deep trouble anyway!
		 */
		return (int) imposables.stream().filter(i->i.isSet()).count();
	}
}
