package com.github.singond.pdfriend.imposition;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import com.github.singond.geometry.plane.Rectangles;
import com.github.singond.pdfriend.book.model.MultiPage;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.modules.Impose;

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
	 * The settings used when processing pages.
	 * This is a mutable object and therefore should stay private!
	 */
	private final Settings settings;
	/**
	 * The list of VirtualDocuments to be processed by this preprocessor.
	 * These documents are used in determining the cell dimensions before
	 * the page processing begins. However, there is no guarantee that the
	 * pages passed to the preprocessor in the processing phase are part
	 * of these documents. This responsibility is left to the client code.
	 */
	private final List<VirtualDocument> documents;
	/**
	 * The resolved dimensions of the cell.
	 */
	private final Dimensions cell;
	
	/** Specifies that the dimensions are not given and should be calculated */
	private static final Dimensions AUTO = Dimensions.dummy();
	
	Preprocessor(List<VirtualDocument> documents, Settings settings) {
		// Storing documents and settings might not be necessary if the cell
		// dimension is determined now.
		this.documents = new ArrayList<>(documents);
		this.settings = settings.copy();
		this.cell = resolveCellDimensions(this.documents, this.settings);
	}
	
	/**
	 * Checks whether this preprocessor was initialized with this page
	 * in mind, ie. if this page is contained in the documents passed
	 * to this {@code Preprocessor} during its initialization.
	 * @param page the page to be tested
	 * @return true if the page was present in the initialization documents
	 */
	public boolean hasPage(VirtualPage page) {
		return documents.stream()
				.anyMatch(doc -> doc.getPages().contains(page));
	}
	
	/**
	 * Resolves the dimensions of the cell for the given collection of pages
	 * and settings.
	 * @param documents
	 * @param settings
	 * @return
	 * @throws NoSuchElementException if the document list contains no pages
	 */
	private static final Dimensions resolveCellDimensions(
			List<VirtualDocument> documents, Settings settings) {
		if (settings.cellDimensions == AUTO) {
			// Calculate preferred cell dimensions
			final double rotation = settings.rotation;
			double halfHorizontalExtent;
			double halfVerticalExtent;
			if (settings.pageDimensions == AUTO) {
				// Circumscribe the cell to the pages scaled by {@code settings.scale}
				// and rotated by {@code settings.rotation}
				try {
					halfHorizontalExtent = documents.stream()
							.flatMap(doc -> doc.getPages().stream())
							.mapToDouble(page -> Rectangles.getHorizontalExtent(
									page.getWidth(), page.getHeight(), rotation))
							.max().getAsDouble();
					halfVerticalExtent = documents.stream()
							.flatMap(doc -> doc.getPages().stream())
							.mapToDouble(page -> Rectangles.getVerticalExtent(
									page.getWidth(), page.getHeight(), rotation))
							.max().getAsDouble();
				} catch (NoSuchElementException e) {
					throw new NoSuchElementException(
							"The documents are empty (they contain no pages): " + documents);
				}
			} else {
				// Page dimensions are given explicitly: circumscribe the cell
				// to a page of these dimensions rotated by {@code settings.rotation}
				halfHorizontalExtent = Rectangles.getHorizontalExtent(
						settings.pageDimensions.width().in(Impose.LENGTH_UNIT),
						settings.pageDimensions.height().in(Impose.LENGTH_UNIT),
						rotation);
				halfVerticalExtent = Rectangles.getVerticalExtent(
						settings.pageDimensions.width().in(Impose.LENGTH_UNIT),
						settings.pageDimensions.height().in(Impose.LENGTH_UNIT),
						rotation);
			}
			return new Dimensions(2 * halfHorizontalExtent, 2 * halfVerticalExtent,
			                      Impose.LENGTH_UNIT);
		} else {
			// Cell dimensions are given explicitly; return the value
			return settings.cellDimensions;
		}
	}
	
	/**
	 * Returns the cell dimensions resolved for the list of documents
	 * and settings specified during initialization.
	 */
	public Dimensions getResolvedCellDimensions() {
		return cell;
	}
	
	/**
	 * Performs the pre-processing of a given page to resolve its position
	 * inside the cell.
	 * <p>
	 * This method does not perform data consistency check, ie. whether the
	 * given page was present in the initialization data of this
	 * {@code Preprocessor} instance, ie. it does not include calling
	 * {@code hasPage()}.
	 * @param page the page whose position in the cell is to be obtained
	 * @return the position as a transformation matrix for the coordinate
	 *         system originating in the lower bottom corner of the cell,
	 *         with x-axis pointing right and y-axis pointing up
	 */
	public AffineTransform getResolvedPositionInCell(VirtualPage page) {
		// TODO Implement using resolvePositionInCell()
		// TODO Cache frequently used values of Dimensions and their results
		throw new UnsupportedOperationException("This method has not been implemented yet");
	}
	
	/**
	 * Resolves the position of a rectangle of the given dimensions
	 * inside the cell.
	 * @param rect the rectangle whose position in the cell is to be obtained
	 * @return the position as a transformation matrix for the coordinate
	 *         system originating in the lower bottom corner of the cell,
	 *         with x-axis pointing right and y-axis pointing up
	 */
	private AffineTransform resolvePositionInCell(Dimensions rect) {
		// TODO Implement
		throw new UnsupportedOperationException("This method has not been implemented yet");
	}
	
	/**
	 * A reusable container of all the settings available for preprocessing
	 * the input pages.
	 *
	 * @author Singon
	 *
	 */
	public static class Settings {
	
		/** A uniform scale to be applied to the page. */
		private double scale;
		/**
		 * Required dimensions of the input page on the output sheet.
		 * If set, this value will cause the image of each input page
		 * to be rendered at the given dimensions in the output.
		 * <p>
		 * The special value {@code AUTO} gives no explicit size for the pages,
		 * it is an instruction to render the image of each page in the
		 * preferred dimensions as calculated from the other settings.
		 */
		private Dimensions pageDimensions = AUTO;
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
		private Dimensions cellDimensions = AUTO;
		
		public Settings() {}
		
		/**
		 * Returns a new {@code Preprocessor.Settings} object initialized
		 * to the current state of this instance.
		 * @return a new copy of this instance
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
			if (dimensions == null)
				throw new IllegalArgumentException("Page dimensions cannot be null");
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
			if (dimensions == null)
				throw new IllegalArgumentException("Cell dimensions cannot be null");
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
