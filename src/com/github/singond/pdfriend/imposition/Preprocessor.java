package com.github.singond.pdfriend.imposition;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.singond.geometry.plane.RectangleFrame;
import com.github.singond.geometry.plane.Rectangles;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
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
	
	/** Logger instance */
	private static ExtendedLogger logger = Log.logger(Preprocessor.class);
	
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
	 * @param orig the rectangle whose position in the cell is to be obtained
	 * @return the position as a transformation matrix for the coordinate
	 *         system originating in the lower bottom corner of the cell,
	 *         with x-axis pointing right and y-axis pointing up
	 */
	private AffineTransform resolvePositionInCell(final Dimensions orig) {
		final double scale = settings.scale;
		final boolean scaleExplicit = settings.isScaleGiven();
		final double rotation = settings.rotation;
		final Dimensions pageDimensions = settings.pageDimensions;
		final Resizing resize = settings.resizing;
		final List<Alignment> align = settings.alignment;
		
		final LengthUnit unit = Impose.LENGTH_UNIT;
		final RectangleFrame frame = new RectangleFrame
				(cell.width().in(unit), cell.height().in(unit));

		if (logger.isDebugEnabled())
			logger.debug("preprocess_resolvingPositionInCell", orig, cell);
		AffineTransform correction = resize.setSizeInFrame(
				frame, orig, scale, scaleExplicit, pageDimensions);
		frame.setRotation(rotation);
		
		
		// TODO Implement
		throw new UnsupportedOperationException("This method has not been implemented yet");
	}
	
	/**
	 * When only page dimensions are given, calculate the scale.
	 * @param dim target page dimensions
	 * @return scale needed to fit
	 */
	private static double scaleFromDimensions(Dimensions dim, Dimensions orig) {
		LengthUnit u = LengthUnits.METRE;
		double scaleX = dim.width().in(u)/orig.width().in(u);
		double scaleY = dim.height().in(u)/orig.height().in(u);
		return Math.min(scaleX, scaleY);
	}
	
	/**
	 * When only scale is given, calculate page dimensions.
	 * @param scale
	 * @return
	 */
	private static Dimensions dimensionsFromScale(double scale, Dimensions orig) {
		return orig.scaleUp(scale);
	}
	
	/* Resizing */
	
	/**
	 * A reusable container of all the settings available for preprocessing
	 * the input pages.
	 *
	 * @author Singon
	 *
	 */
	public static class Settings {
	
		/**
		 * A uniform scale to be applied to the page.
		 * Setting this value to negative means that scale is not given
		 * and should be determined from the other settings.
		 */
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
		private List<Alignment> alignment = Arrays.asList
				(new CenterAlignment(0), new MiddleAlignment(0));
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
			// Dimensions class is immutable
			copy.pageDimensions = pageDimensions;
			copy.rotation = rotation;
			copy.resizing = resizing;
			copy.alignment = new ArrayList<>(alignment);
			copy.pageDimensions = pageDimensions;
			copy.cellDimensions = cellDimensions;
			return copy;
		}
		
		public void setScale(double scale) {
			if (scale <= 0)
				throw new IllegalArgumentException("Scale must be a positive number");
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
		
		/** Checks whether scale has been explicitly set to a valid value */
		public boolean isScaleGiven() {
			return scale > 0;
		}
	
		/* Alignment */
	}

	/**
	 * Specifies behaviour for page size.
	 * In order to be able to share instances, all implementing classes
	 * are required to be immutable and private to Preprocessor.
	 */
	private static enum Resizing {
		/** Respects pageDimensions and scale. */
		NONE {
			@Override
			AffineTransform setSizeInFrame (
					final RectangleFrame frame,
					final Dimensions orig,
					final double scale,
					final boolean scaleExplicit,
					final Dimensions pageDimensions) {
				AffineTransform correction = null;
				if (scaleExplicit) {
					frame.setSize(frame.new Scale(scale));
					if (pageDimensions != AUTO) {
						/* Rescale to make scaling by {@code scale} also result in correct page size */
						// TODO Check reasoning
						// Magnification needed to make the orig fit the pageDimensions
						double s = scaleFromDimensions(pageDimensions, orig);
						double scaleCorrection = scale/s;
						correction = AffineTransform.getScaleInstance(scaleCorrection, scaleCorrection);
					}
				} else {
					if (pageDimensions == AUTO) {
						frame.setSize(frame.new Scale(1));
					} else {
						double s = scaleFromDimensions(pageDimensions, orig);
						frame.setSize(frame.new Scale(s));
					}
				}
				return correction;
			}
		},
		/**
		 * Ensures that the whole area of the page fits into the cell,
		 * respecting the page's rotation but ignoring its scale and dimensions.
		 * If the rotation is not a multiple of right angle, this resizing
		 * will leave blank areas in the cell, which are not covered by the page.
		 */
		FIT {
			@Override
			AffineTransform setSizeInFrame (
					final RectangleFrame frame,
					final Dimensions orig,
					final double scale,
					final boolean scaleExplicit,
					final Dimensions pageDimensions) {
				frame.setSize(frame.new Fit());
				return null;
			}
		},
		/**
		 * Ensures that the page covers the whole area of the cell,
		 * respecting the page's rotation but ignoring its scale and dimensions.
		 * If the rotation is not a multiple of right angle, this resizing
		 * will result in the page overflowing the cell.
		 */
		FILL {
			@Override
			AffineTransform setSizeInFrame (
					final RectangleFrame frame,
					final Dimensions orig,
					final double scale,
					final boolean scaleExplicit,
					final Dimensions pageDimensions) {
				frame.setSize(frame.new Fill());
				return null;
			}
		};
		
		/**
		 * In the frame given as argument, sets the constraint for page size.
		 * @param frame the frame in which the constraint is to be set
		 * @param orig original dimensions of the page (before pre-processing)
		 * @param scale uniform scale to be applied (as maginfication)
		 * @param scaleExplicit flag indicating that {@code scale} is given explicitly
		 * @param pageDimensions required page dimensions
		 * @return a correction transformation to be applied to the page
		 *         before transforming it with the {@code frame} output.
		 *         This is needed in some types; the other types return null,
		 *         indicating that no correction is necessary.
		 */
		abstract AffineTransform setSizeInFrame (final RectangleFrame frame,
		                                         final Dimensions orig,
		                                         final double scale,
		                                         final boolean scaleExplicit,
		                                         final Dimensions pageDimensions);
	}

	/* Alignment */
	
	/**
	 * Specifies page alignment within the cell.
	 * In order to be able to share instances, all implementing classes
	 * are required to be immutable and private to Preprocessor.
	 */
	private static interface Alignment {
		/** Invite a visitor */
		<P, R> R invite(AlignmentVisitor<P, R> visitor, P param);
	}
	
	private static interface AlignmentVisitor<P, R> {
		// Implement for all subclasses of Alignment
		abstract R visit(LeftAlignment align, P param);
		abstract R visit(CenterAlignment align, P param);
		abstract R visit(RightAlignment align, P param);
		abstract R visit(TopAlignment align, P param);
		abstract R visit(MiddleAlignment align, P param);
		abstract R visit(BottomAlignment align, P param);
	}

	/** A class with a single scalar value */
	private abstract static class SingleValued {
		private final double value;
		
		private SingleValued(double value) {
			this.value = value;
		}
	}

	/** Alignment by distance from the left edge */
	private static class LeftAlignment extends SingleValued implements Alignment {
		private LeftAlignment(double value) {
			super(value);
		}

		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}
	}

	/** Alignment by offset (to right) from the center */
	private static class CenterAlignment extends SingleValued implements Alignment {
		private CenterAlignment(double value) {
			super(value);
		}
		
		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}
	}

	/** Alignment by distance from the right edge */
	private static class RightAlignment extends SingleValued implements Alignment {
		private RightAlignment(double value) {
			super(value);
		}
		
		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}
	}

	/** Alignment by distance from the top edge */
	private static class TopAlignment extends SingleValued implements Alignment {
		private TopAlignment(double value) {
			super(value);
		}
		
		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}
	}

	/** Alignment by offset (upwards) from the center */
	private static class MiddleAlignment extends SingleValued implements Alignment {
		private MiddleAlignment(double value) {
			super(value);
		}
		
		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}
	}

	/** Alignment by distance from the bottom edge */
	private static class BottomAlignment extends SingleValued implements Alignment {
		private BottomAlignment(double value) {
			super(value);
		}
		
		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}
	}
}
