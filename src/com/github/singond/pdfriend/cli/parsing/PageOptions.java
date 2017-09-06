package com.github.singond.pdfriend.cli.parsing;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.imposition.Preprocessor;

@Parameters(resourceBundle = "Help")
public class PageOptions implements ParameterDelegate {

	@Parameter(names={"-s", "--scale"},
	           description="The scale of the content",
	           descriptionKey="param-scale")
	private double scale;
	
	@Parameter(names={"-r", "--rotation"},
	           description="Rotation of the pages in radians in counter-clockwise direction",
	           descriptionKey="param-rotation")
	private double rotation;
	
	@Parameter(names={"--resize"},
	           description="Page size behaviour after scaling and rotating",
	           descriptionKey="param-resizing")
	private ResizingBehaviour resize = ResizingBehaviour.NONE;

	@Override
	public void postParse() throws ArgumentParsingException {
		// TODO Auto-generated method stub
		
	}
	
	public Preprocessor.Settings getPreprocessorSettings() {
		Preprocessor.Settings settings = new Preprocessor.Settings();
		settings.setScale(scale);
		settings.setRotation(rotation);
		
		return settings;
	}
	
	private static enum ResizingBehaviour {
		NONE(Preprocessor.Resizing.NONE),
		FIT(Preprocessor.Resizing.FIT),
		FILL(Preprocessor.Resizing.FILL);
		
		private final Preprocessor.Resizing value;
		
		private ResizingBehaviour(Preprocessor.Resizing value) {
			this.value = value;
		}
	}
}
