package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.book.model.MultiPage;
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
	private Alignment alignment = new HorizontalVerticalAlignment(
			new Center(0), new Middle(0));
	/**
	 * Required dimensions.
	 * This property can be set to override the preferred dimensions
	 * as calculated from the other settings.
	 */
	private Dimensions requiredDims = null;
	
	// TODO Make something useful of these
	private static interface Resizing {}
	
	private static interface Alignment {
		void accept(MultiPage.Pagelet pagelet);
	}
	
	private static interface HorizontalAlign {
		void accept(MultiPage.Pagelet pagelet);
	}
	private static interface VerticalAlign {
		void accept(MultiPage.Pagelet pagelet);
	}
	
	/**
	 * A basic alignment consisting of two separate values for the horizontal
	 * and vertical alignment.
	 */
	private static class HorizontalVerticalAlignment implements Alignment {
		private final HorizontalAlign horizontalAlign;
		private final VerticalAlign verticalAlign;
		
		private HorizontalVerticalAlignment(HorizontalAlign horizontalAlign,
		                                   VerticalAlign verticalAlign) {
			if (horizontalAlign == null)
				throw new NullPointerException("Horizontal alignment cannot be null");
			if (verticalAlign == null)
				throw new NullPointerException("Vertical alignment cannot be null");
			this.horizontalAlign = horizontalAlign;
			this.verticalAlign = verticalAlign;
		}
		
		@Override
		public void accept(MultiPage.Pagelet pagelet) {
			horizontalAlign.accept(pagelet);
			verticalAlign.accept(pagelet);
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
	private static class Left extends SingleValued implements HorizontalAlign {
		private Left(double value) {
			super(value);
		}
		
		@Override
		public void accept(MultiPage.Pagelet pagelet) {
			// TODO Implement!
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}
	
	/** Alignment by offset (to right) from the center */
	private static class Center extends SingleValued implements HorizontalAlign {
		private Center(double value) {
			super(value);
		}
		
		@Override
		public void accept(MultiPage.Pagelet pagelet) {
			// TODO Implement!
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}
	
	/** Alignment by distance from the right edge */
	private static class Right extends SingleValued implements HorizontalAlign {
		private Right(double value) {
			super(value);
		}
		
		@Override
		public void accept(MultiPage.Pagelet pagelet) {
			// TODO Implement!
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}
	
	/** Alignment by distance from the top edge */
	private static class Top extends SingleValued implements VerticalAlign {
		private Top(double value) {
			super(value);
		}
		
		@Override
		public void accept(MultiPage.Pagelet pagelet) {
			// TODO Implement!
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}
	
	/** Alignment by offset (upwards) from the center */
	private static class Middle extends SingleValued implements VerticalAlign {
		private Middle(double value) {
			super(value);
		}
		
		@Override
		public void accept(MultiPage.Pagelet pagelet) {
			// TODO Implement!
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}
	
	/** Alignment by distance from the bottom edge */
	private static class Bottom extends SingleValued implements VerticalAlign {
		private Bottom(double value) {
			super(value);
		}
		
		@Override
		public void accept(MultiPage.Pagelet pagelet) {
			// TODO Implement!
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}
}
