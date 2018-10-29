package com.github.singond.pdfriend.reorder;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;

/**
 * A command-line interface for the n-up imposable type {@link NUp}.
 *
 * @author Singon
 *
 */
class CompactCli implements ReorderableCli<Compact> {

	@Parameter(names="--compact",
			description="Reorders pages to minimize breaks",
			validateWith = PositiveInteger.class)
	// TODO: Enable specifying a list of numbers
	private int slots = -1;

	@Override
	public void postParse() throws ParameterConsistencyException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return slots != -1;
	}

	@Override
	public Compact getReorderable() {
		return new Compact(slots);
	}
}
