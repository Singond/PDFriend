package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import picocli.CommandLine.Option;

import com.github.singond.pdfriend.cli.DimensionsConverter;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.ParameterDelegate;
import com.github.singond.pdfriend.cli.RotationConverter;
import com.github.singond.pdfriend.cli.TwoNumbers;
import com.github.singond.pdfriend.geometry.Angle;
import com.github.singond.pdfriend.geometry.AngularUnits;
import com.github.singond.pdfriend.geometry.Dimensions;

/**
 * A command-line interface for some of the preprocessor settings.
 * This includes only those preprocessor settings which are handled in the
 * same manner regardless of the imposition type.
 *
 * @author Singon
 *
 */
@Parameters(resourceBundle="Help", separators="=")
class PreprocessorSettingsCli implements ParameterDelegate {

	private static final Angle DEFAULT_ROTATION = new Angle(0);
	private static final TwoNumbers DEFAULT_ALIGNMENT = new TwoNumbers(0,0);

	@Parameter(names={"-s", "--scale"},
	           description="The scale of the content",
	           descriptionKey="param-scale")
	@Option(names = {"-s", "--scale"})
	private double scale = -1;

	@Parameter(names={"-r", "--rotate", "--rotation"},
	           description="Rotation of the pages in radians in counter-clockwise direction",
	           descriptionKey="param-rotation",
	           converter=RotationConverter.class)
	@Option(names = {"-r", "--rotate"},
	        converter = RotationConverter.class)
	private Angle rotation = DEFAULT_ROTATION;

	@Parameter(names={"--resize"},
	           description="Page size behaviour after scaling and rotating",
	           descriptionKey="param-resizing")
	@Option(names = {"--resize"})
	private ResizingBehaviour resize = ResizingBehaviour.AUTO;

	@Parameter(names={"--align", "--alignment"},
	           description="Alignment of the scaled, rotated and resized page",
	           descriptionKey="param-alignment",
	           converter=TwoNumbers.Converter.class)
	@Option(names = {"--align"})
	private TwoNumbers align = DEFAULT_ALIGNMENT;

	@Parameter(names={"--size"},
	           description="Size of the imposed pages before resizing",
	           descriptionKey="param-inputPageSize",
	           converter=DimensionsConverter.class)
	@Option(names = {"--size"})
	private Dimensions pageSize = null;

	@Override
	public void postParse() throws ParameterConsistencyException {
		// Do nothing
	}

	public boolean isSet() {
		return scale > 0
		       || rotation != DEFAULT_ROTATION
		       || resize != ResizingBehaviour.NONE
		       || align != DEFAULT_ALIGNMENT
		       || pageSize != null;
	}

	/**
	 * Combines the command-line arguments into a new
	 * {@code Preprocessor.Settings} object.
	 * @return a new instance of preprocessor settings
	 */
	public Preprocessor.Settings getPreprocessorSettings() {
		Preprocessor.Settings settings = new Preprocessor.Settings();
		if (scale > 0) {
			settings.setScale(scale);
		}
		if (rotation != null && rotation != DEFAULT_ROTATION) {
			settings.setRotation(rotation.in(AngularUnits.RADIAN));
		}
		settings.setResizing(resize.value);
		if (align != null && align != DEFAULT_ALIGNMENT) {
			settings.setHorizontalAndVerticalAlignment
				(align.getFirst(), align.getSecond());
		}
		if (pageSize != null) {
			settings.setPageDimensions(pageSize);
		}
		return settings;
	}

	public static enum ResizingBehaviour {
		NONE(Preprocessor.Resizing.NONE),
		FIT(Preprocessor.Resizing.FIT),
		FILL(Preprocessor.Resizing.FILL),
		AUTO(Preprocessor.Resizing.AUTO);

		private final Preprocessor.Resizing value;

		private ResizingBehaviour(Preprocessor.Resizing value) {
			this.value = value;
		}
	}
}
