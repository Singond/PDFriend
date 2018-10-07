package com.github.singond.pdfriend.imposition;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.github.singond.geometry.plane.RectangleFrame;
import com.github.singond.geometry.plane.Rectangles;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.TransformableContents;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;

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
public final class Preprocessor {

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
	 * The resolved dimensions of the cell (including margins).
	 */
	private final Dimensions cell;
	/**
	 * The resolved dimensions of the contents of the cell without margins.
	 */
	private final Dimensions cellContent;
	/**
	 * The cache of resolved page positions.
	 */
	private final Map<Dimensions, AffineTransform> positionsCache;
	/**
	 * The working length unit.
	 */
	private static final LengthUnit UNIT = Imposition.LENGTH_UNIT;

	/** Logger instance */
	private static ExtendedLogger logger = Log.logger(Preprocessor.class);

	Preprocessor(List<VirtualDocument> documents, Settings settings) {
		// Storing documents and settings might not be necessary if the cell
		// dimension is determined now.
		this.documents = new ArrayList<>(documents);
		this.settings = settings.copy();
		CellProperties cp = resolveCellDimensions(this.documents, this.settings);
		this.cell = cp.cell;
		this.cellContent = cp.cellContent;
		this.positionsCache = new HashMap<>();
	}

	Preprocessor(VirtualDocument document, Settings settings) {
		this(Arrays.asList(document), settings);
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
	 * Returns the cell dimensions resolved for the list of documents
	 * and settings specified during initialization.
	 */
	public Dimensions getResolvedCellDimensions() {
		return cell;
	}

	/**
	 * Resolves the dimensions of the cell for the given collection of pages
	 * and settings.
	 * @param documents
	 * @param settings
	 * @return an object containing the resolved cell dimensions including margins
	 *         and the cell content dimensions (ie. without margins)
	 * @throws NoSuchElementException if the document list contains no pages
	 */
	private static final CellProperties resolveCellDimensions(
			List<VirtualDocument> documents, Settings settings) {
		Dimensions cell;
		Dimensions content;

		logger.verbose("preprocess_cellSize_resolve", documents, settings);
		if (settings.cellDimensions == DimensionSettings.AUTO) {
			logger.verbose("preprocess_cellSize_resolvePref");
			final double rotation = settings.rotation;
			double halfHorizontalExtent;
			double halfVerticalExtent;
			if (settings.pageDimensions == DimensionSettings.AUTO) {
				// Circumscribe the cell to the pages scaled by {@code settings.scale}
				// and rotated by {@code settings.rotation}
				logger.verbose("preprocess_cellSize_fromPageScale", settings.scale, rotation);
				// TODO: Merge to one loop?
				try {
					halfHorizontalExtent = documents.stream()
							.flatMap(doc -> doc.getPages().stream())
							.mapToDouble(page -> Rectangles.getHalfHorizontalExtent(
									page.getWidth(), page.getHeight(), rotation))
							.max().getAsDouble();
					halfVerticalExtent = documents.stream()
							.flatMap(doc -> doc.getPages().stream())
							.mapToDouble(page -> Rectangles.getHalfVerticalExtent(
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
				assert settings.pageDimensions.isValue();
				Dimensions pageDimensions = settings.pageDimensions.value();
				// Page dimensions are given explicitly: circumscribe the cell
				// to a page of these dimensions rotated by {@code settings.rotation}
				logger.verbose("preprocess_cellSize_fromPageDimensions", pageDimensions, rotation);
				halfHorizontalExtent = Rectangles.getHalfHorizontalExtent(
						pageDimensions.width().in(UNIT),
						pageDimensions.height().in(UNIT),
						rotation);
				halfVerticalExtent = Rectangles.getHalfVerticalExtent(
						pageDimensions.width().in(UNIT),
						pageDimensions.height().in(UNIT),
						rotation);
			}

			Length contentWidth = new Length(2 * halfHorizontalExtent,
			                                 UNIT);
			Length contentHeight = new Length(2 * halfVerticalExtent,
			                                  UNIT);
			Margins m = settings.cellMargins;
			content = new Dimensions(contentWidth, contentHeight);
			cell = new Dimensions(Length.sum(contentWidth, m.left(), m.right()),
			                      Length.sum(contentHeight, m.top(), m.bottom()));
		} else {
			assert settings.cellDimensions.isValue();
			// Cell dimensions are given explicitly; return the value
			logger.verbose("preprocess_cellSize_explicit", settings.cellDimensions);
			cell = settings.cellDimensions.value();
			// Shrink content area according to margins
			Margins m = settings.cellMargins;
			Length horizontalMargins = Length.sum(m.left(), m.right());
			Length verticalMargins = Length.sum(m.top(), m.bottom());
			content = new Dimensions(
					Length.subtract(cell.width(), horizontalMargins),
					Length.subtract(cell.height(), verticalMargins));
		}

		logger.verbose("preprocess_cellSize_result", cell, content);
		return new CellProperties(cell, content);
	}

	/**
	 * Groups the cell size with and without margins.
	 */
	private static class CellProperties {
		/** Cell size including margins */
		private final Dimensions cell;
		/** Cell size without margins */
		private final Dimensions cellContent;

		private CellProperties(Dimensions cell, Dimensions cellContent) {
			this.cell = cell;
			this.cellContent = cellContent;
		}
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
				(page.getWidth(), page.getHeight(), UNIT);
		AffineTransform position = getResolvedPositionInCell(pageDims);
		TransformableContents contents = page.getContents();
		contents.transform(position);
		if (logger.isDebugEnabled())
			logger.debug("preprocess_position_final", page, position);

		VirtualPage.Builder result = new VirtualPage.Builder();
		Dimensions d = getResolvedCellDimensions();
		result.setWidth(d.width().in(UNIT));
		result.setHeight(d.height().in(UNIT));
		result.addContent(contents);
		return result.build();
	}

	/**
	 * Processes all pages of the given document.
	 */
	public VirtualDocument processDocument(VirtualDocument doc) {
		VirtualDocument.Builder processed = new VirtualDocument.Builder();
		for (VirtualPage pg : doc) {
			processed.addPage(process(pg));
		}
		return processed.build();
	}

	/**
	 * Processes all pages of all documents given during initialization.
	 * If more than one document was given in initialization, they are
	 * concatenated in their order.
	 */
	public VirtualDocument processAll() {
		VirtualDocument.Builder processed = new VirtualDocument.Builder();
		for (VirtualDocument doc : documents) {
			for (VirtualPage pg : doc) {
				processed.addPage(process(pg));
			}
		}
		return processed.build();
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
		if (positionsCache.containsKey(dims)) {
			// Return cached value
			if (logger.isDebugEnabled())
				logger.debug("preprocess_position_cached", positionsCache.get(dims));
			return positionsCache.get(dims);
		} else {
			// Calculate it
			AffineTransform position = resolvePositionInCell(dims);
			positionsCache.put(dims, position);
			return position;
		}
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
		/** Position with respect to the cell content (cell minus margins) */
		AffineTransform position = resolvePositionInCellContent(orig);
		Margins margins = settings.cellMargins;
		/** Translation needed to induce the margins */
		AffineTransform shift = AffineTransform.getTranslateInstance(
				margins.left().in(UNIT), margins.bottom().in(UNIT));
		position.preConcatenate(shift);
		return position;
	}

	/**
	 * Resolves the position of a rectangle of the given dimensions
	 * inside the cell content rectangle.
	 * This method always performs the full calculation.
	 * @param orig the rectangle whose position in the cell is to be obtained
	 * @return the position as a transformation matrix for the coordinate
	 *         system originating in the lower bottom corner of the cell,
	 *         with x-axis pointing right and y-axis pointing up
	 */
	private AffineTransform resolvePositionInCellContent(final Dimensions orig) {
		final double declaredScale = settings.scale;
		final boolean scaleExplicit = settings.isScaleGiven();
		final double rotation = settings.rotation;
		final DimensionSettings pageDimensions = settings.pageDimensions;
		final Resizing declaredResize = settings.resizing;
		final List<Alignment> align = settings.alignment;

		if (logger.isDebugEnabled())
			logger.debug("preprocess_pageSize_cell", orig, cellContent);

		/*
		 * To position the page box we are using a RectangleFrame object.
		 * Its "frame" represents the cell, whose dimensions are now known.
		 * This frame is used to position the page box according to some
		 * constraints which will be specified now.
		 */
		final RectangleFrame frame = new RectangleFrame
				(cellContent.width().in(UNIT), cellContent.height().in(UNIT));

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
		 * First, if resizing is AUTO, resolve it to any of the available
		 * values:
		 * If page dimensions are given explicitly, fit all pages to that
		 * rectangle, otherwise do not apply any resizing.
		 * For resizing values other than AUTO, just use that value.
		 */
		Resizing resize;
		if (declaredResize == Resizing.AUTO) {
			if (pageDimensions != DimensionSettings.AUTO) {
				// Page dimensions are given explicitly
				resize = Resizing.FIT;
			} else {
				resize = Resizing.NONE;
			}
		} else {
			resize = declaredResize;
		}

		/*
		 * Next, rule out those resizing options which operate solely with
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
			} else if (pageDimensions != DimensionSettings.AUTO) {
				assert pageDimensions.isValue();
				// No scale given, calculate it from output page size
				scale = scaleFromDimensions(pageDimensions.value(), orig);
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
		if (pageDimensions != DimensionSettings.AUTO && scaleExplicit) {
			assert pageDimensions.isValue();
			// Magnification needed to make the input page fit the pageDimensions
			double s = scaleFromDimensions(pageDimensions.value(), orig);
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
				// TODO: Implement
				throw new UnsupportedOperationException("Not implemented yet");
		}
		for (Alignment a : align) {
			a.invite(aligner, null);
		}
		aligner.prepareRectangleFrame();    // The return value is the frame, which we already have

		// Now let RectangleFrame do its job
		AffineTransform result = frame.positionRectangle
				(pageBox.width().in(UNIT), pageBox.height().in(UNIT));

		/*
		 * If the page box does not coincide with the page border,
		 * (ie. if scale correction has been applied), shift the page
		 * to align the page box over the center of the page.
		 */
		if (pageBoxOffset) {
			double horizontalShift =
					(pageBox.width().in(UNIT) - orig.width().in(UNIT)) / 2;
			double verticalShift =
					(pageBox.height().in(UNIT) - orig.height().in(UNIT)) / 2;
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
				.append("cell content dimensions: ").append(cellContent).append(", ")
				.append("settings: ").append(settings)
				.toString();
	}

	/**
	 * A reusable container of all the settings available for preprocessing
	 * the input pages.
	 *
	 * TODO: Make the settings class final and provide a separate builder?
	 * The Preprocessor itself is immutable, but it might be better not to
	 * share its instances directly. Instantiating a new one from settings
	 * any time it is necessary seems safer.
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
		private DimensionSettings pageDimensions = DimensionSettings.AUTO;
		/** Rotation of the page in radians in the direction from x-axis to y-axis. */
		private double rotation;
		/**
		 * Behaviour for page size.
		 * This is applied after scaling each page by {@code scale}.
		 */
		private Resizing resizing = Resizing.AUTO;
		/** Page alignment within the rectangle given by resolved dimensions */
		private List<Alignment> alignment = Arrays.asList
				(new CenterAlignment(0), new MiddleAlignment(0));
		/**
		 * Required dimensions of the circumscribed rectangle (the cell).
		 * This property can be set to override the preferred dimensions
		 * as calculated from the other settings.
		 */
		private DimensionSettings cellDimensions = DimensionSettings.AUTO;

		/**
		 * The margins of the cell.
		 */
		private Margins cellMargins = new Margins(new Length());

		public Settings() {}

		/**
		 * Constructs a new settings object with all values set to auto.
		 * @return a new {@code Preprocessor.Settings} object
		 */
		public static Settings auto() {
			return new Settings();
		}

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
			copy.cellMargins = cellMargins;
			return copy;
		}

		public double getScale() {
			return scale;
		}

		/**
		 * Sets the uniform scale to be applied to the pages.
		 * Note that this may not the final scale at which a page is
		 * rendered, because after scaling the page, its size may be further
		 * modified by resizing behaviour, (see {@link #setResizing}).
		 */
		public void setScale(double scale) {
			if (scale <= 0)
				throw new IllegalArgumentException("Scale must be a positive number");
			this.scale = scale;
		}

		public DimensionSettings getPageDimensions() {
			return pageDimensions;
		}

		/**
		 * Sets the dimensions of every page.
		 * This overrides the preferred dimensions which would otherwise be
		 * calculated from the initial dimensions, scale and cell dimensions.
		 * This variant accepts {@code DimensionSettings} object directly.
		 * @param dimensions the required cell dimensions
		 */
		void setPageDimensions(DimensionSettings dimensions) {
			if (dimensions == null)
				throw new IllegalArgumentException("Page dimensions cannot be null");
			this.pageDimensions = dimensions;
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
			this.pageDimensions = DimensionSettings.of(dimensions);
		}

		public double getRotation() {
			return rotation;
		}

		/** Sets the rotation of the page */
		public void setRotation(double rotation) {
			this.rotation = rotation;
		}

		public Resizing getResizing() {
			return resizing;
		}

		/**
		 * Sets the behaviour to be applied to page size.
		 * This behaviour takes effect after the pages have been rotated
		 * and scaled. The default value is {@link #resizing.NONE}, which
		 * has no effect on the pages, leaving them in the original state
		 * determined by scaling and rotating.
		 * @param resizing an object describing the behaviour
		 */
		public void setResizing(Resizing resizing) {
			this.resizing = resizing;
		}

		/**
		 * Sets the horizontal and vertical alignment by setting weights
		 * which determine how the free space is distributed in the direction
		 * in question.
		 * <li>In horizontal direction, parameter value of {@code -1} means
		 * left alignment, {@code 0} is centered and {@code 1} means right
		 * alignment.
		 * <li>In vertical direction, parameter value of {@code -1} means
		 * bottom alignment, {@code 0} is centered and {@code 1} means top
		 * alignment.
		 * @param horizontalWeight parameter for horizontal alignment as described above
		 * @param verticalWeight parameter for vertical alignment as described above
		 */
		public void setHorizontalAndVerticalAlignment
				(double horizontalWeight, double verticalWeight) {
			Alignment horizontal = new HorizontalWeightedAlignment(horizontalWeight);
			Alignment vertical = new VerticalWeightedAlignment(verticalWeight);
			this.alignment = Arrays.asList(horizontal, vertical);
		}

		public DimensionSettings getCellDimensions() {
			return cellDimensions;
		}

		/**
		 * Sets the dimensions of the circumscribed rectangle (the cell).
		 * This overrides the preferred dimensions which would otherwise be
		 * calculated from either the initial page dimensions and scale,
		 * or the required page dimensions.
		 * This variant accepts {@code DimensionSettings} object directly.
		 * @param dimensions the required cell dimensions
		 */
		void setCellDimensions(DimensionSettings dimensions) {
			if (dimensions == null)
				throw new IllegalArgumentException("Cell dimensions cannot be null");
			this.cellDimensions = dimensions;
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
			this.cellDimensions = DimensionSettings.of(dimensions);
		}

		/**
		 * Gets the margins of the cell.
		 * @return the cell margins
		 */
		public Margins getCellMargins() {
			return cellMargins;
		}

		/**
		 * Sets the margins of the cell.
		 * @param the margins to be applied to cell
		 */
		public void setCellMargins(Margins cellMargins) {
			this.cellMargins = cellMargins;
		}

		/** Checks whether scale has been explicitly set to a valid value */
		public boolean isScaleGiven() {
			return scale > 0;
		}

		/**
		 * Checks whether the resulting cell size is constrained by
		 * some concrete value given in settings.
		 * Specifically, this method returns true if all the settings
		 * of scale, page dimensions and cell dimensions are set to
		 * automatic.
		 * @return {@code true} if none of scale, page size and cell size
		 *         have been given an explicit value
		 */
		public boolean isAutoSize() {
			return scale <= 0
					&& pageDimensions == DimensionSettings.AUTO
					&& cellDimensions == DimensionSettings.AUTO;
		}

		@Override
		public String toString() {
			return new StringBuilder()
					.append("scale: ").append(scale).append(", ")
					.append("rotation: ").append(rotation).append(" rad, ")
					.append("resizing: ").append(resizing).append(", ")
					.append("alignment: ").append(alignment).append(", ")
					.append("page dimensions: ").append(pageDimensions).append(", ")
					.append("cell dimensions: ").append(cellDimensions).append(", ")
					.append("margins: ").append(cellMargins)
					.toString();
		}
	}

	/* Resizing */

	/**
	 * Specifies behaviour for page size.
	 */
	public static enum Resizing {
		/**
		 * Respects pageDimensions and scale.
		 */
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
		FILL,
		/**
		 * Indicates that no explicit resizing behaviour has been specified.
		 */
		AUTO;
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
		abstract R visit(HorizontalWeightedAlignment align, P param);
		abstract R visit(TopAlignment align, P param);
		abstract R visit(MiddleAlignment align, P param);
		abstract R visit(BottomAlignment align, P param);
		abstract R visit(VerticalWeightedAlignment align, P param);
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
		public Void visit(HorizontalWeightedAlignment align, Void param) {
			alignment.setHorizontalAlignment(alignment.new HorizontalWeighted(align.value));
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
		public Void visit(VerticalWeightedAlignment align, Void param) {
			alignment.setVerticalAlignment(alignment.new VerticalWeighted(align.value));
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

	/** Alignment by weighted distribution of the free space */
	private static class HorizontalWeightedAlignment extends SingleValued implements Alignment {
		private HorizontalWeightedAlignment(double value) {
			super(value);
		}

		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}

		@Override
		public String toString() {return "Horizontal weighted " + value;}
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

	/** Alignment by weighted distribution of the free space */
	private static class VerticalWeightedAlignment extends SingleValued implements Alignment {
		private VerticalWeightedAlignment(double value) {
			super(value);
		}

		@Override
		public <P, R> R invite(AlignmentVisitor<P, R> visitor, P param) {
			return visitor.visit(this, param);
		}

		@Override
		public String toString() {return "Vertical weighted " + value;}
	}
}
