package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.cli.ParameterDelegate;

/**
 * A command-line interface for {@link RenderingSettings}.
 *
 * Provides configuration settings for the imposition module which affect
 * the way in which the output document is rendered.
 * This includes the way even-numbered pages are rotated in double-sided
 * documents.
 *
 * @author Singon
 */
@Parameters(resourceBundle="Help", separators="=")
class RenderingSettingsCli implements ParameterDelegate {
	
	/** Duplex printing with flip along vertical edge. */
	@Parameter(names = "--duplex",
			description = "Keep even-numbered pages upright. This is the default",
			descriptionKey = "param-duplex")
	private boolean duplex = false;

	/** Duplex printing with flip along horizontal edge. */
	@Parameter(names = "--tumble",
	           description = "Rotate even-numbered pages upside down",
	           descriptionKey = "param-tumble")
	private boolean tumble = false;

	@Override
	public void postParse() throws ParameterConsistencyException {
		if (duplex && tumble) {
			throw new ParameterConsistencyException(
					"Cannot set both the \"duplex\" and \"tumble\" options");
		}
	}
	
	public RenderingSettings getRenderingSettings() {
		RenderingSettings.Builder sb = new RenderingSettings.Builder();
		if (tumble) {
			sb.setFlipDirection(FlipDirection.AROUND_X);
		} else {
			sb.setFlipDirection(FlipDirection.AROUND_Y);
		}
		return sb.build();
	}
}
