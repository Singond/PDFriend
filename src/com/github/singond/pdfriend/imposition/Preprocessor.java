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
 * This class introduces a notion of a so-called "cell", which is a rectangle
 * of specific dimensions which forms a reference in which the position
 * of a page is expressed.
 * This cell is the same (or equal in terms of properties) for all pages
 * processed by a single pre-processor instance.
 * <p>
 * The page pre-processing involves first determining the dimensions of the
 * cell to be used and then calculating the position of each page given with
 * respect to this cell.
 * <p>
 * This class is not thread-safe.
 *
 * @author Singon
 *
 */
public class Preprocessor {
	
	/**
	 * A reusable container of all the settings available for page preprocessing.
	 *
	 * @author Singon
	 *
	 */
	public static class Settings {
	
		/** A uniform scale to be applied to the page. */
		private double scale;
		/**
		 * Required dimensions of every page.
		 * This property can be set to override the preferred dimensions
		 * as calculated from the other settings.
		 */
		private Dimensions pageDimensions = null;
		/** Rotation of the page in radians in the direction from x-axis to y-axis. */
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
		 * Required dimensions of the circumscribed rectangle (the cell).
		 * This property can be set to override the preferred dimensions
		 * as calculated from the other settings.
		 */
		private Dimensions cellDimensions = null;
		
		public Settings() {}
		
		/**
		 * Returns a new {@code Preprocessor.Settings} object initialized
		 * to the current state of this instance.
		 * @return a copy of this instance
		 */
		public Settings copy() {
			Settings copy = new Settings();
			copy.scale = scale;
			// Dimensions class is considered immutable (will be truly once it's fixed)
			copy.pageDimensions = pageDimensions;
			copy.rotation = rotation;
			copy.resizing = resizing;
			copy.alignment = alignment;
			copy.pageDimensions = pageDimensions;
			copy.cellDimensions = cellDimensions;
			return copy;
		}
		
		public void setScale(double scale) {
			if (scale <= 0)
				throw new IllegalArgumentException("scale must be a positive number");
			this.scale = scale;
		}
	
		/**
		 * Sets the dimensions of every page.
		 * This overrides the preferred dimensions which would otherwise be
		 * calculated from the initial dimensions, scale and cell dimensions.
		 * @param dimensions the required cell dimensions
		 */
		public void setPageDimensions(Dimensions dimensions) {
			this.pageDimensions = dimensions;
		}
	
		public void setRotation(double rotation) {
			this.rotation = rotation;
		}
	
		/**
		 * Sets the dimensions of the circumscribed rectangle (the cell).
		 * This overrides the preferred dimensions which would otherwise be
		 * calculated from either the initial page dimensions and scale,
		 * or the required page dimensions.
		 * @param dimensions the required cell dimensions
		 */
		public void setCellDimensions(Dimensions dimensions) {
			this.pageDimensions = dimensions;
		}
	
		// TODO Make something useful of these
		/**
		 * Specifies behaviour for page size.
		 * In order to be able to share instances, all implementing classes
		 * are required to be immutable and private to Preprocessor.
		 */
		private static interface Resizing {}
		
		/**
		 * Specifies page alignment within the cell.
		 * In order to be able to share instances, all implementing classes
		 * are required to be immutable and private to Preprocessor.
		 */
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
}
