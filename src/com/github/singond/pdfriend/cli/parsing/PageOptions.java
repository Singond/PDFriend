package com.github.singond.pdfriend.cli.parsing;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.imposition.Preprocessor;

@Parameters(resourceBundle = "Help", separators="=")
public class PageOptions implements ParameterDelegate {
	
	private static final TwoNumbers DEFAULT_ALIGNMENT = new TwoNumbers(0,0);
	private static final Dimensions AUTO = Dimensions.dummy();

	@Parameter(names={"-s", "--scale"},
	           description="The scale of the content",
	           descriptionKey="param-scale")
	private double scale = -1;
	
	@Parameter(names={"-r", "--rotation"},
	           description="Rotation of the pages in radians in counter-clockwise direction",
	           descriptionKey="param-rotation")
	private double rotation;
	
	@Parameter(names={"--resize"},
	           description="Page size behaviour after scaling and rotating",
	           descriptionKey="param-resizing")
	private ResizingBehaviour resize = ResizingBehaviour.NONE;
	
	@Parameter(names={"--align", "--alignment"},
	           description="Alignment of the scaled, rotated and resized page",
	           descriptionKey="param-alignment",
	           converter=TwoNumbers.Converter.class)
	private TwoNumbers align = DEFAULT_ALIGNMENT;
	
	@Override
	public void postParse() throws ArgumentParsingException {
		// TODO Auto-generated method stub
		
	}
	
	public Preprocessor.Settings getPreprocessorSettings() {
		Preprocessor.Settings settings = new Preprocessor.Settings();
		if (scale > 0) {
			settings.setScale(scale);
		}
		if (rotation != 0) {
			settings.setRotation(rotation);
		}
		settings.setResizing(resize.value);
		if (align != DEFAULT_ALIGNMENT) {
			settings.setHorizontalAndVerticalAlignment
				(align.getFirst(), align.getSecond());
		}
		return settings;
	}
	
	public static enum ResizingBehaviour {
		NONE(Preprocessor.Resizing.NONE),
		FIT(Preprocessor.Resizing.FIT),
		FILL(Preprocessor.Resizing.FILL);
		
		private final Preprocessor.Resizing value;
		
		private ResizingBehaviour(Preprocessor.Resizing value) {
			this.value = value;
		}
	}
}
