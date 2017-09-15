package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.cli.ArgumentParsingException;
import com.github.singond.pdfriend.cli.BookletBindingConverter;

/**
 * A command-line interface for the booklet imposable type {@link Booklet}.
 *
 * @author Singon
 *
 */
class BookletCli implements ImposableCli {

	@Parameter(names="--booklet", description="A simple stack of sheets folded in half")
	private boolean booklet = false;
	
	/** Specifies where the binding is located */
	@Parameter(names="--binding", converter=BookletBindingConverter.class)
	private Booklet.Binding binding = Booklet.Binding.LEFT;
	
	/** In a vertical booklet, print the verso upside down. */
	@Parameter(names="--verso-opposite")
	private boolean flipVerso = false;

	@Override
	public void postParse() throws ArgumentParsingException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return booklet;
	}

	@Override
	public Imposable getImposable() {
//		Imposition.TypeBooklet impl = module.new TypeBooklet();
//		module.setType(impl);
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
