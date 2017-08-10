package com.github.singond.pdfriend.cli.parsing;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.EnumConverter;
import com.github.singond.geometry.plane.Rectangles;
import com.github.singond.pdfriend.geometry.Angle;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnits;

/**
 * A set of command-line parameters which will be parsed into a PageProperties object.
 *
 * @author Singon
 *
 */
public class PagePropertiesParams implements ParameterDelegate {

	/**
	 * Negative value indicates unset parameter.
	 */
	@Parameter(names="--scale",
			description="Size to be applied to the input pages.")
	public double scale = -1;
	
	@Parameter(names="--rotation",
			description="Rotation of the page in counter-clockwise direction",
			converter=AngleConverter.class)
	public Angle rotation = new Angle(0);
	
	/**
	 * TODO Change the name to something more meaningful. 
	 * Warning! The name of this parameter is expected to change.
	 */
	@Parameter(names="--extents",
			description="The target size")
	public TargetSize extents = TargetSize.CIRCUMSCRIBED_RECTANGLE;
	
	@Override
	public void postParse() throws ArgumentParsingException {
		// TODO Auto-generated method stub
	}

	private static enum TargetSize {
		/**
		 * Given a collection of rectangles and a single value of rotation,
		 * returns the smallest rectangle (with sides parallel to coordinate axes)
		 * which can be drawn around the given set of rectangles,
		 * stacked on top of each other with their centers aligned and each
		 * rotated by the given amount of rotation.
		 * It is important to note that the rotation of individual rectangles
		 * is applied before calculating the horizontal and vertical extents
		 * of the figure resulting from combining all the rectangles.
		 */
		CIRCUMSCRIBED_RECTANGLE {
			@Override
			Dimensions combineDimensions(List<Dimensions> dims, double rotation) {
				/** The horizontal extent in points */
				double horizontalExtent = 0;
				/** The vertical extent in points */
				double verticalExtent = 0;
				
				for (Dimensions dim : dims) {
					double w = dim.width().in(LengthUnits.POINT_POSTSCRIPT);
					double h = dim.height().in(LengthUnits.POINT_POSTSCRIPT);
					/** Half of the rectangle's horizontal extent */
					double he = Rectangles.getHorizontalExtent(w, h, rotation);
					/** Half of the rectangle's vertical extent */
					double ve = Rectangles.getVerticalExtent(w, h, rotation);
					
					if (he > horizontalExtent)
						horizontalExtent = he;
					if (ve > verticalExtent)
						verticalExtent = ve;
				}
				return new Dimensions(horizontalExtent, verticalExtent,
				                      LengthUnits.POINT_POSTSCRIPT);
			}
			
		};
		
		/**
		 * Given a set of dimensions, combines them into a single rectangle.
		 * @param dims the list of individual pairs of rectangle dimensions
		 * @param rotation rotation in counter-clockwise direction in radians
		 * @return the dimensions of the rectangle combining all the input
		 */
		abstract Dimensions combineDimensions(List<Dimensions> dims, double rotation);
	}
	
	private static enum Alignment {
		
	}
	
	private static enum Resizing {
		NONE,
		FIT,
		FILL;
	}
}
