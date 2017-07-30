package com.github.singond.pdfriend.geometry;

import java.util.Collection;

import com.github.singond.pdfriend.document.VirtualPage;

/**
 * Defines conditions for determining the output dimensions of a page.
 *
 * @author Singon
 *
 */
public abstract class PageSize {

	/**
	 * Invites a PageSize.Visitor.
	 * @param <R> return type of the visitor
	 */
	protected abstract <R, P> R invite(Visitor<? extends R, P> visitor, P param);
	
	/**
	 * A Visitor interface for PageSize objects.
	 * @param <R> return type of the visitor
	 * @param <P> parameter type for the visitor
	 */
	public static interface Visitor<R, P> {
		public R visit(Scale size, P param);
		public R visit(FitToLargest size, P param);
	}
	
	/**
	 * Scales each page up by a constant uniform scale.
	 * Each instance of this class represents a constant number,
	 * which is to be interpreted as magnification to be applied to the page.
	 * Absolute values smaller than 1 represent minification.
	 */
	public static class Scale extends PageSize {
		private final double scale;
		
		/**
		 * Constructs a new Scale object representing the given scale.
		 * @param value the value of uniform scale to be applied to any page
		 */
		public Scale(double value) {
			this.scale = value;
		}
		
		/**
		 * Gets the value of this scale expressed as magnification.
		 * @return the scale to be applied to arbitrary page
		 */
		public double scalePage() {
			return scale;
		}
		
		@Override
		protected <R, P> R invite(Visitor<? extends R, P> visitor, P param) {
			return visitor.visit(this, param);
		}
	}
	
	/**
	 * Scales pages up to fit the "largest" page while keeping the aspect ratio.
	 * 
	 * More precisely, given a collection of pages, scales each page up so that
	 * it fits tightly the smallest rectangle which can be circumscribed to the
	 * unscaled pages, stacked on top of each other with their centers aligned.
	 * This resizing keeps the aspect ratio.
	 */
	public static class FitToLargest extends PageSize {
		/**
		 * Scales a page up so that it fits the rectangle circumscribed
		 * to the collection of pages.
		 * @param page the page whose scale is to be determined
		 * @param pages a collection of pages to define the circumscribed rectangle
		 * @return the magnification to be applied to {@code page}
		 */
		public double scalePage(VirtualPage page, Collection<VirtualPage> pages) {
			/** The largest width in the collection of pages */
			double largestWidth = -1;
			/** The largest height in the collection of pages */
			double largestHeight = -1;
			
			for (VirtualPage p : pages) {
				if (p.getWidth() > largestWidth)
					largestWidth = p.getWidth();
				if (p.getHeight() > largestHeight)
					largestHeight = p.getHeight();
			}
			return Math.min(largestWidth/page.getWidth(), largestHeight/page.getHeight());
		}

		@Override
		protected <R, P> R invite(Visitor<? extends R, P> visitor, P param) {
			return visitor.visit(this, param);
		}
	}
}
