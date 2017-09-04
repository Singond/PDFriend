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
import com.github.singond.pdfriend.document.Contents;
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
		Dimensions result;
		logger.verbose("preprocess_cellSize_resolve", documents, settings);
		if (settings.cellDimensions == AUTO) {
			logger.verbose("preprocess_cellSize_resolvePref");
			final double rotation = settings.rotation;
			double halfHorizontalExtent;
			double halfVerticalExtent;
			if (settings.pageDimensions == AUTO) {
				// Circumscribe the cell to the pages scaled by {@code settings.scale}
				// and rotated by {@code settings.rotation}
				logger.verbose("preprocess_cellSize_fromPageScale", settings.scale, rotation);
				// TODO: Merge to one loop?
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
				// Scale it
				if (settings.isScaleGiven()) {
					halfHorizontalExtent *= settings.scale;
					halfVerticalExtent *= settings.scale;
				}
			} else {
				// Page dimensions are given explicitly: circumscribe the cell
				// to a page of these dimensions rotated by {@code settings.rotation}
				logger.verbose("preprocess_cellSize_fromPageDimensions", settings.pageDimensions, rotation);
				halfHorizontalExtent = Rectangles.getHorizontalExtent(
						settings.pageDimensions.width().in(Impose.LENGTH_UNIT),
						settings.pageDimensions.height().in(Impose.LENGTH_UNIT),
						rotation);
				halfVerticalExtent = Rectangles.getVerticalExtent(
						settings.pageDimensions.width().in(Impose.LENGTH_UNIT),
						settings.pageDimensions.height().in(Impose.LENGTH_UNIT),
						rotation);
			}
			result = new Dimensions(2 * halfHorizontalExtent, 2 * halfVerticalExtent,
			                        Impose.LENGTH_UNIT);
		} else {
			// Cell dimensions are given explicitly; return the value
			logger.verbose("preprocess_cellSize_explicit", settings.cellDimensions);
			result = settings.cellDimensions;
		}
		logger.verbose("preprocess_cellSize_result", result);
		return result;
	}
	
	/**
	 * Returns the cell dimensions resolved for the list of documents
	 * and settings specified during initialization.
	 */
	public Dimensions getResolvedCellDimensions() {
		return cell;
	}
	
	/**
	 * Performs the pre-processing of a given page and returns the result.
	 * <p>
	 * This method does not perform data consistency check, ie. whether the
	 * given page was present in the initialization data of this
	 * {@code Preprocessor} instance, ie. it does not include calling
	 * {@code hasPage()}.
	 * @param page the page which is to be preprocessed
	 * @return a new page with the dimensions of the cell, whose content is
	 *         the same as that of the input page, but moved, scaled and
	 *         rotated according to the preprocess settings given during
	 *         the initialization of this {@code Preprocessor} instance
	 */
	public VirtualPage process(VirtualPage page) {
		Dimensions pageDims = new Dimensions
				(page.getWidth(), page.getHeight(), Impose.LENGTH_UNIT);
		AffineTransform position = getResolvedPositionInCell(pageDims);
		Contents contents = page.getContents();
		contents.transform(position);
		if (logger.isDebugEnabled())
			logger.debug("preprocess_page_position", page, position);

		VirtualPage.Builder result = new VirtualPage.Builder();
		Dimensions d = getResolvedCellDimensions();
		result.setWidth(d.width().in(Impose.LENGTH_UNIT));
		result.setHeight(d.height().in(Impose.LENGTH_UNIT));
		result.addContent(contents);
		return result.build();
	}
	
	/**
	 * Returns the position of a rectangle of the given dimensions
	 * inside the cell; looking it up in cached values and calculating
	 * it if necessary.
	 * @param dims the rectangle whose position in the cell is to be obtained
	 * @return the position as a transformation matrix for the coordinate
	 *         system originating in the lower bottom corner of the cell,
	 *         with x-axis pointing right and y-axis pointing up
	 */
	private AffineTransform getResolvedPositionInCell(Dimensions dims) {
		// TODO Cache frequently used values of Dimensions and their results
		return resolvePositionInCell(dims);
	}
	
	/**
	 * Resolves the position of a rectangle of the given dimensions
	 * inside the cell.
	 * This method always performs the full calculation. If the result
	 * has already been computed for given dimensions (specified in the
	 * argument), using {@link #getResolvedPositionInCell} is preferable
	 * because it caches the computed values.
	 * @param orig the rectangle whose position in the cell is to be obtained
	 * @return the position as a transformation matrix for the coordinate
	 *         system originating in the lower bottom corner of the cell,
	 *         with x-axis pointing right and y-axis pointing up
	 */
	private AffineTransform resolvePositionInCell(final Dimensions orig) {
		final double declaredScale = settings.scale;
		final boolean scaleExplicit = settings.isScaleGiven();
		final double rotation = settings.rotation;
		final Dimensions pageDimensions = settings.pageDimensions;
		final Resizing resize = settings.resizing;
		final List<Alignment> align = settings.alignment;
		final LengthUnit unit = Impose.LENGTH_UNIT;
		
		if (logger.isDebugEnabled())
			logger.debug("preprocess_pageSize_cell", orig, cell);
		
		/*
		 * To position the page box we are using a RectangleFrame object.
		 * Its "frame" represents the cell, whose dimensions are now known.
		 * This frame is used to position the page box according to some
		 * constraints which will be specified now.
		 */
		final RectangleFrame frame = new RectangleFrame
				(cell.width().in(unit), cell.height().in(unit));
		
		/*
		 * The easiest constraint to resolve is the rotation, because it
		 * is not dependent on anything else. We can set it now.
		 */
		if (rotation != 0) {
			frame.setRotation(rotation);
		}
		
		/*
		 * Another constraint which needs to be set is the size of the page.
		 * However, this applies only for some values of {@code resizing},
		 * because some other values might set the output size of the page
		 * in a completely unrelated manner. The following is the default
		 * behaviour:
		 * <p>
		 * For the purpose of this method, define a "page box" as that part
		 * of the page, whose dimensions when rendered will be equal to the
		 * {@code pageDimensions}, and which will be considered when placing
		 * the page into the cell.
		 * In most cases the "page box" will coincide with the actual border
		 * of the page, but see below.
		 * <p>
		 * Resolving the scale is not trivial, because it can be set by both
		 * {@code scale} and {@code pageDimensions}. If only one of the two
		 * values is given, the other value can easily be calculated from the
		 * first and the original page dimensions, but if both values are set,
		 * they pose a conflict: Which one to prefer when setting the size?
		 * <p>
		 * The decision is that the page box will honor the value declared
		 * in {@code pageDimensions}, but the contents of the page will be
		 * drawn with the scale declared in {@code scale}. Unless we're really
		 * lucky and the {@code scale} and {@code pageDimensions} actually
		 * resolve to the same page size (ie. original size of the page scaled
		 * by {@code scale} equals {@code pageDimensions}), this means that
		 * some parts of the page content will overflow the page box
		 * (if original page size scaled by {@code scale} is larger than
		 * {@code pageDimensions}), or some part of the page box will not be
		 * covered by the page contents and will remain blank (in the opposite
		 * case).
		 * <p>
		 * However, some resizing options will bypass this behvaiour, because
		 * they, by definition, resize the pages when applied.
		 * Note that this does not mean that those options disregard the size
		 * settings completely: The values of both {@code scale} and
		 * {@code pageDimensions} are reflected in the size of the cell.
		 * <p>
		 * First, rule out those resizing options which operate solely with
		 * the size of the cell. For the rest, determine the scale at which
		 * the contents of the page will be drawn:
		 */
		if (resize == Resizing.FIT) {
			// Fit the pages into the cell, no scale needed
			frame.setSize(frame.new Fit());
		} else if (resize == Resizing.FILL) {
			// Fill the cell with the page, no scale needed
			frame.setSize(frame.new Fill());
		} else {
			// No resizing is applied, respect the settings in full
			double scale;
			if (scaleExplicit) {
				// Honor the given value
				scale = declaredScale;
				if (logger.isDebugEnabled())
					logger.debug("preprocess_pageScale_explicit", scale);
			} else if (pageDimensions != AUTO) {
				// No scale given, calculate it from output page size
				scale = scaleFromDimensions(pageDimensions, orig);
				if (logger.isDebugEnabled())
					logger.debug("preprocess_pageScale_fromPage", pageDimensions, scale);
			} else {
				// Nothing to determine scale from, use default
				scale = 1;
				if (logger.isDebugEnabled())
					logger.debug("preprocess_pageScale_default", scale);
			}
			// Use the determined scale in the frame
			frame.setSize(frame.new Scale(scale));
		}
		
		/*
		 * Up to now, the size of the page box is still unknown.
		 * By default, this will be equal to the original page dimensions.
		 * 
		 * However, if both {@code pageDimensions} and {@code scale} have
		 * been set to an explicit size, we need to take some measures to
		 * obtain the correct result:
		 * 
		 * The RectangleFrame (representing the "cell") simply takes
		 * a rectangle, and positions it into itself, completely agnostic
		 * of the contents of the rectangle.
		 * That "rectangle" is the page box.
		 * The positioning performed by RectangleFrame will also set
		 * the scale of the contents to an exact value, which will most
		 * probably not be the desired value specified in {@code scale}.
		 * In order to make the page box, after scaling it by {@code scale},
		 * equal in size to the desired {@code pageDimensions},
		 * we need to change the size of the page box passed to the frame.
		 * If, for example, the {@code pageDimensions} are two times
		 * the original dimensions of the page ({@code orig}), but the
		 * value of {@code scale} is three, we need to use a page box
		 * which is 2/3 of the original page dimensions and pass it to
		 * the rectangle frame along with the scale of 3.
		 * Then scaling the page box of 2/3 of original dimensions will
		 * result in output page dimensions of (2/3) * 3 = 2 times the
		 * original dimensions, which is the desired result.
		 */
		Dimensions pageBox;
		boolean pageBoxOffset = false;  // Page box coincides with page border
		if (pageDimensions != AUTO && scaleExplicit) {
			// Magnification needed to make the input page fit the pageDimensions
			double s = scaleFromDimensions(pageDimensions, orig);
			double correction = s/declaredScale;
			pageBox = orig.scaleUp(correction);
			pageBoxOffset = true;
			if (logger.isDebugEnabled())
				logger.debug("preprocess_pageSize_fromPageAndScale", declaredScale,
				             pageDimensions, s, correction);
		} else {
			pageBox = orig;
			if (logger.isDebugEnabled())
				logger.debug("preprocess_pageSize_fromPage", declaredScale);
		}
		
		/*
		 * By now, the size and rotation of the page are known as well as
		 * the page box size.
		 * The constraint remaining to be set in RectangleFrame is alignment.
		 */
		Aligner aligner;
		switch (resize) {
			case FIT:
			case NONE:
				// Interpret alignment as from the inside of the cell
				aligner = new InnerAligner(frame);
				break;
			case FILL:
			default:
				throw new UnsupportedOperationException("Not implemented yet");
		}
		for (Alignment a : align) {
			a.invite(aligner, null);
		}
		aligner.prepareRectangleFrame();    // The return value is the frame, which we already have
		
		// Now let RectangleFrame do its job
		AffineTransform result = frame.positionRectangle
				(pageBox.width().in(unit), pageBox.height().in(unit));
		
		/*
		 * If the page box does not coincide with the page border,
		 * (ie. if scale correction has been applied), shift the page
		 * to align the page box over the center of the page.
		 */
		if (pageBoxOffset) {
			double horizontalShift =
					(pageBox.width().in(unit) - orig.width().in(unit)) / 2;
			double verticalShift =
					(pageBox.height().in(unit) - orig.height().in(unit)) / 2;
			result.concatenate(AffineTransform.getTranslateInstance
					(horizontalShift, verticalShift));
		}
		
		/* Phew, done */
		return result;
	}
	
	/**
	 * Calculates the scale corresponding to given output page dimensions.
	 * @param dim output page dimensions
	 * @param orig input page dimensions
	 * @return the scale necessary to make {@code orig} fit {@code dim}
	 */
	private static double scaleFromDimensions(Dimensions dim, Dimensions orig) {
		LengthUnit u = LengthUnits.METRE;
		double scaleX = dim.width().in(u)/orig.width().in(u);
		double scaleY = dim.height().in(u)/orig.height().in(u);
		return Math.min(scaleX, scaleY);
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("cell dimensions: ").append(cell).append(", ")
				.append("settings: ").append(settings)
				.toString();
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
		private Resizing resizing = Resizing.NONE;
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
			this.cellDimensions = dimensions;
		}
		
		/** Checks whether scale has been explicitly set to a valid value */
		public boolean isScaleGiven() {
			return scale > 0;
		}
	
		@Override
		public String toString() {
			return new StringBuilder()
					.append("scale: ").append(scale).append(", ")
					.append("rotation: ").append(rotation).append(" rad, ")
					.append("resizing: ").append(resizing).append(", ")
					.append("alignment: ").append(alignment).append(", ")
					.append("page dimensions: ").append((pageDimensions==AUTO)
					                                    ? "AUTO" : pageDimensions).append(", ")
					.append("cell dimensions: ").append((cellDimensions==AUTO)
					                                    ? "AUTO" : pageDimensions)
					.toString();
		}
	}

	/**
	 * Specifies behaviour for page size.
	 * In order to be able to share instances, all implementing classes
	 * are required to be immutable and private to Preprocessor.
	 */
	private static enum Resizing {
		/** Respects pageDimensions and scale. */
		NONE,
		/**
		 * Ensures that the whole area of the page fits into the cell,
		 * respecting the page's rotation but ignoring its scale and dimensions.
		 * If the rotation is not a multiple of right angle, this resizing
		 * will leave blank areas in the cell, which are not covered by the page.
		 */
		FIT,
		/**
		 * Ensures that the page covers the whole area of the cell,
		 * respecting the page's rotation but ignoring its scale and dimensions.
		 * If the rotation is not a multiple of right angle, this resizing
		 * will result in the page overflowing the cell.
		 */
		FILL;
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
	
	private static interface Aligner extends AlignmentVisitor<Void, Void> {
		RectangleFrame prepareRectangleFrame();
	}
	
	private static class InnerAligner implements Aligner {
		private final RectangleFrame frame;
		private final RectangleFrame.InnerAlignment alignment;
		
		InnerAligner(RectangleFrame frame) {
			this.frame = frame;
			this.alignment = frame.new InnerAlignment();
		}

		@Override
		public Void visit(LeftAlignment align, Void param) {
			alignment.setHorizontalAlignment(alignment.new Left(align.value));
			return null;
		}

		@Override
		public Void visit(CenterAlignment align, Void param) {
			alignment.setHorizontalAlignment(alignment.new Center(align.value));
			return null;
		}

		@Override
		public Void visit(RightAlignment align, Void param) {
			alignment.setHorizontalAlignment(alignment.new Right(align.value));
			return null;
		}

		@Override
		public Void visit(TopAlignment align, Void param) {
			alignment.setVerticalAlignment(alignment.new Top(align.value));
			return null;
		}

		@Override
		public Void visit(MiddleAlignment align, Void param) {
			alignment.setVerticalAlignment(alignment.new Middle(align.value));
			return null;
		}

		@Override
		public Void visit(BottomAlignment align, Void param) {
			alignment.setVerticalAlignment(alignment.new Bottom(align.value));
			return null;
		}

		@Override
		public RectangleFrame prepareRectangleFrame() {
			frame.setAlignment(alignment);
			return frame;
		}
		
		@Override
		public String toString() {
			return "Inner aligner for frame" + frame;
		}
	}

	/** A class with a single scalar value */
	private abstract static class SingleValued {
		final double value;
		
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
		
		@Override
		public String toString() {return "Left " + value;}
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
		
		@Override
		public String toString() {return "Center " + value;}
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
		
		@Override
		public String toString() {return "Right " + value;}
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
		
		@Override
		public String toString() {return "Top " + value;}
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
		
		@Override
		public String toString() {return "Middle " + value;}
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
		
		@Override
		public String toString() {return "Bottom " + value;}
	}
}
