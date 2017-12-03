package com.github.singond.pdfriend.reorder;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;

/**
 * A command-line interface for the booklet imposable type {@link Booklet}.
 *
 * @author Singon
 *
 */
@Parameters(separators="=")
class ReverseCli implements ReorderableCli<Reverse> {

	@Parameter(names="--reverse", description="A simple stack of sheets folded in half")
	private boolean reverse = false;

	@Override
	public void postParse() throws ParameterConsistencyException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return reverse;
	}

	@Override
	public Reverse getReorderable() {
		return new Reverse();
	}
}
