package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.IStringConverter;
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

	/**
	 * FIXME Relies on the fact that IntegerDimensions is non-final.
	 */
	private static final IntegerDimensions AUTO = new IntegerDimensions(0, 0) {};
	
	/**
	 * Use null to represent AUTO. Use this internally and do not expose
	 * this smelly piece.
	 * TODO It stinks anyway. Any nice way to avoid the null?
	 * Maybe just store the string and parse it manually when constructing
	 * the output object in {@link #getImposable()}.
	 */
	@Parameter(names={"--n-up", "--nup"},
			description="Several pages arranged into a grid on a larget sheet",
			converter=NUpConverter.class)
	private IntegerDimensions nup = null;
	
	@Parameter(names={"--nup-repeat"},
			descriptionKey="nup-copyToFill",
			description="Fill each cell in a page with a copy of the same input page")
	private boolean copyToFillPage = false;

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
		if (nup == AUTO) {
			task.setAutoGrid();
		} else if (nup != null) {
			task.setRows(nup.getFirstDimension());
			task.setCols(nup.getSecondDimension());
		}
		if (copyToFillPage) {
			task.setFillMode(NUp.FillMode.FILL_PAGE);
		}
		return task;
	}
	
	private static class NUpConverter implements IStringConverter<IntegerDimensions> {
		@Override
		public IntegerDimensions convert(String arg) {
			if ("auto".equalsIgnoreCase(arg)) {
				return AUTO;
			} else {
				return new IntegerDimensionsConverter().convert(arg);
			}
		}
	}
}
