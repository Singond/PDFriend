package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.IntegerDimensionsConverter;
import com.github.singond.pdfriend.geometry.IntegerDimensions;

/**
 * A command-line interface for the n-up imposable type {@link NUp}.
 *
 * @author Singon
 *
 */
class NUpCli implements ImposableCli<NUp> {

	@Parameter(names={"--n-up", "--nup"},
			description="Several pages arranged into a grid on a larget sheet",
			converter=IntegerDimensionsConverter.class)
	private IntegerDimensions nup = null;

	@Override
	public void postParse() throws ParameterConsistencyException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return nup != null;
	}

	@Override
	public NUp getImposable() {
		NUp task = new NUp();
		task.setRows(nup.getFirstDimension());
		task.setCols(nup.getSecondDimension());
		return task;
	}
}
