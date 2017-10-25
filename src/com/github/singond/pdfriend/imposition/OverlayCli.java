package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;

/**
 * A command-line interface for the overlay imposable type {@link Overlay}.
 *
 * @author Singon
 *
 */
class OverlayCli implements ImposableCli<Overlay.Builder> {

	@Parameter(names="--overlay", description="Print pages on top of each other")
	private boolean overlay = false;
	
	@Parameter(names={"--repeat-in-layer"},
	           descriptionKey="overlay-repeatInLayer",
	           description="When a document in a layer has no more pages, repeat this document")
	private boolean repeatInLayer = false;

	@Override
	public void postParse() throws ParameterConsistencyException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return overlay;
	}

	@Override
	public Overlay.Builder getImposable() {
		Overlay.Builder builder = new Overlay.Builder();
		builder.setRepeatInLayer(repeatInLayer);
		return builder;
	}
}
