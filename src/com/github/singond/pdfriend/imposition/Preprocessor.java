package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.geometry.Dimensions;

/**
 * Pre-processes pages of input document prior to imposition.
 * 
 * This procedure includes tasks such as rotating and resizing pages
 * or unifying the pages' dimensions.
 * An instance of this class aggregates various preprocessing options
 * and resolves them into specific properties which can be used in
 * imposition.
 * Because the class is mutable, the options can be set one by one.
 * <p>
 * This class is not thread-safe.
 *
 * @author Singon
 *
 */
public class Preprocessor {

	/** A uniform scale to be applied to the page */
	private double scale;
	/** Rotation of the page */
	private double rotation;
	/**
	 * Behaviour for page size.
	 * This is applied after scaling each page by {@code scale}.
	 */
	private Resizing resizing;
	/** Page alignment within the rectangle given by resolved dimensions */
	private Alignment alignment;
	/**
	 * Required dimensions.
	 * This property can be set to override the preferred dimensions
	 * as calculated from the other settings.
	 */
	private Dimensions requiredDims = null;
	
	// TODO Make something useful of these
	public static interface Resizing {}
	public static interface Alignment {}
	private static interface HorizontalAlign {}
	private static interface VerticalAlign {}
	
	public static class HorizontalVerticalAlignment implements Alignment {
		private final HorizontalAlign horizontalAlign;
		private final VerticalAlign verticalAlign;
		
		public HorizontalVerticalAlignment(HorizontalAlign horizontalAlign,
		                                   VerticalAlign verticalAlign) {
			super();
			this.horizontalAlign = horizontalAlign;
			this.verticalAlign = verticalAlign;
		}
	}
	
	/** A class with a single scalar value */
	private abstract static class SingleValued {
		private final double value;
		
		private SingleValued(double value) {
			this.value = value;
		}
	}
	
	/** Alignment by distance from the left edge */
	public static class Left extends SingleValued implements HorizontalAlign {
		public Left(double value) {
			super(value);
		}
	}
	
	/** Alignment by offset (to right) from the center */
	public static class Center extends SingleValued implements HorizontalAlign {
		public Center(double value) {
			super(value);
		}
	}
	
	/** Alignment by distance from the right edge */
	public static class Right extends SingleValued implements HorizontalAlign {
		public Right(double value) {
			super(value);
		}
	}
	
	/** Alignment by distance from the top edge */
	public static class Top extends SingleValued implements VerticalAlign {
		public Top(double value) {
			super(value);
		}
	}
	
	/** Alignment by offset (upwards) from the center */
	public static class Middle extends SingleValued implements VerticalAlign {
		public Middle(double value) {
			super(value);
		}
	}
	
	/** Alignment by distance from the bottom edge */
	public static class Bottom extends SingleValued implements VerticalAlign {
		public Bottom(double value) {
			super(value);
		}
	}
}
