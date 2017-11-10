package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.IntegerDimensionsConverter;
import com.github.singond.pdfriend.geometry.IntegerDimensions;
import com.github.singond.pdfriend.imposition.NUp.GridDimensions;
import com.github.singond.pdfriend.imposition.NUp.GridType;

/**
 * A command-line interface for the n-up imposable type {@link NUp}.
 *
 * @author Singon
 *
 */
class NUpCli implements ImposableCli<NUp> {

	@Parameter(names={"--n-up", "--nup"},
			description="Several pages arranged into a grid on a larget sheet",
			converter=NUpConverter.class)
	private GridDimensions nup = null;
	
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
		if (nup != null) {
			switch (nup.special()) {
				case AUTO:
					task.setAutoGrid();
					break;
				case VALUE:
					task.setRows(nup.value().getFirstDimension());
					task.setCols(nup.value().getSecondDimension());
					break;
				default:
					throw new AssertionError(nup);
			}
		}
		if (copyToFillPage) {
			task.setFillMode(NUp.FillMode.FILL_PAGE);
		}
		return task;
	}
	
	private static class NUpConverter implements IStringConverter<GridDimensions> {
		@Override
		public GridDimensions convert(String arg) {
			if ("auto".equalsIgnoreCase(arg)) {
				return GridDimensions.of(GridType.AUTO);
			} else {
				IntegerDimensions dims = new IntegerDimensionsConverter().convert(arg);
				return GridDimensions.of(dims);
			}
		}
	}
}
