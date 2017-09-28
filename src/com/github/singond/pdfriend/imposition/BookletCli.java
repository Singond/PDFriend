package com.github.singond.pdfriend.imposition;

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
class BookletCli implements ImposableCli<Booklet> {

	@Parameter(names="--booklet", description="A simple stack of sheets folded in half")
	private boolean booklet = false;
	
	/** Specifies where the binding is located */
	@Parameter(names="--binding", converter=EdgeConverter.class)
	private Edge binding = Edge.LEFT;
	
	/** In a vertical booklet, print the verso upside down. */
	@Parameter(names="--verso-opposite")
	private boolean flipVerso = false;

	@Override
	public void postParse() throws ParameterConsistencyException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return booklet;
	}

	@Override
	public Booklet getImposable() {
		Booklet booklet = new Booklet();
		booklet.setBinding(binding);
		booklet.setVersoOpposite(flipVerso);
		return booklet;
	}
}
