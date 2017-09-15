package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.cli.ArgumentParsingException;

/**
 * A command-line interface for the overlay imposable type {@link Overlay}.
 *
 * @author Singon
 *
 */
class OverlayCli implements ImposableCli<Overlay> {

	@Parameter(names="--overlay", description="Print pages on top of each other")
	private boolean overlay = false;

	@Override
	public void postParse() throws ArgumentParsingException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return overlay;
	}

	@Override
	public Overlay getImposable() {
		// TODO Pass some value into layers argument or remove it
//		Imposition.TypeOverlay impl = module.new TypeOverlay(-1);
//		module.setType(impl);
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
