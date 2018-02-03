package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.SpecVal;
import com.github.singond.pdfriend.book.GridPage;
import com.github.singond.pdfriend.book.LoosePages;
import com.github.singond.pdfriend.book.MultiPage.PageletView;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.IntegerDimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.CommonSettings.MarginSettings;
import com.github.singond.pdfriend.imposition.Preprocessor.Resizing;
import com.github.singond.pdfriend.imposition.Preprocessor.Settings;

/**
 * An n-up layout.
 * This is a simple imposition task which places several pages onto
 * a larger page.
 * @author Singon
 */
public class NUp extends AbstractImposable<LoosePages>
		implements Imposable<LoosePages>, ImposableBuilder<NUp> {

	/** The internal name of this imposable document type */
	private static final String NAME = "n-up";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(NUp.class);
	
	private int rows = 1;
	private int cols = 1;
	private GridType gridType = GridType.VALUE;
	private NUpOrientation orientation = NUpOrientation.UPRIGHT;
	private FillDirection direction = FillDirection.ROWS;
	private FillMode fillMode = FillMode.SEQUENTIAL;
	private Preprocessor.Settings preprocess = null;
	private CommonSettings common = null;
	private RenderingSettings render = null;
	
	/**
	 * Sets the number of rows in the grid.
	 * Setting this value will cause the imposition to ignore any previous
	 * calls to {@link setAutoGrid}.
	 * @param rows the number of rows
	 * @return this NUp object
	 */
	public NUp setRows(int rows) {
		if (rows < 1)
			throw new IllegalArgumentException("Number of rows must be positive");
		this.gridType = GridType.VALUE;
		this.rows = rows;
		return this;
	}

	/**
	 * Sets the number of columns in the grid.
	 * Setting this value will cause the imposition to ignore any previous
	 * calls to {@link setAutoGrid}.
	 * @param cols the number of columns
	 * @return this NUp object
	 */
	public NUp setCols(int cols) {
		if (cols < 1)
			throw new IllegalArgumentException("Number of columns must be positive");
		this.gridType = GridType.VALUE;
		this.cols = cols;
		return this;
	}
	
	/**
	 * Sets the number of rows and columns to automatic value.
	 * The value will then be determined just before imposing,
	 * once the input document is known.
	 * Settings this value will cause the imposition to ignore any previous
	 * calls to {@link setRows} and {@link setCols}.
	 * @return
	 */
	public NUp setAutoGrid() {
		this.gridType = GridType.AUTO;
		return this;
	}

	/**
	 * Sets the orientation of the grid.
	 * @param orientation
	 * @return this NUp object
	 */
	public NUp setOrientation(NUpOrientation orientation) {
		if (orientation == null)
			throw new IllegalArgumentException("Orientation must not be null");
		this.orientation = orientation;
		return this;
	}

	/**
	 * Sets the direction in which the cells will be filled.
	 * @param direction
	 * @return this NUp object
	 */
	public NUp setDirection(FillDirection direction) {
		if (orientation == null)
			throw new IllegalArgumentException("Flip direction must not be null");
		this.direction = direction;
		return this;
	}

	/**
	 * Sets whe
	 * @param fillMode
	 * @return
	 */
	public NUp setFillMode(FillMode fillMode) {
		if (fillMode == null)
			throw new IllegalArgumentException("Fill mode must not be null");
		this.fillMode = fillMode;
		return this;
	}

	/**
	 * Imposes the given virtual document into a list of grid pages
	 * according to the current settings of this {@code NUp} object.
	 */
	private List<GridPage> imposeAsPages(VirtualDocument doc) {
		// Copy all nonfinal values defensively
		final int rows = this.rows;
		final int cols = this.cols;
		final NUpOrientation orientation = this.orientation;
		final FillDirection direction = this.direction;
		final Preprocessor.Settings preprocess = this.preprocess;
		final CommonSettings common = this.common;
		int pageCount = common.getPageCount();
		
		if (logger.isDebugEnabled()) {
			logger.debug("imposition_preprocessSettings", preprocess);
			logger.debug("imposition_commonSettings", common);
//			logger.debug("imposition_imposableSettings", NAME, );
		}
		
		/*
		 * Determine all properties of the page and grid.
		 * 
		 * In the output document of an n-up imposition, the page and the
		 * sheet are the same thing. This implies that page size and sheet
		 * size should both resolve to the same value.
		 * If both are explicitly set to a different value, an exception
		 * will be thrown to indicate this.
		 * If only sheet size is given, use it as page size.
		 * In any case, use only the page size in the grid construction.
		 */
		DimensionSettings pageSize = common.getPageSize();
		DimensionSettings sheetSize = common.getSheetSize();
		if (sheetSize != DimensionSettings.AUTO) {
			if (pageSize == DimensionSettings.AUTO) {
				logger.verbose("nup_pageSizeToSheetSize");
				pageSize = sheetSize;
			} else {
				if (!pageSize.equals(sheetSize)) {
					// The page size and sheet size are in conflict.
					throw new IllegalStateException
						("Sheet size and page size are set to a different value");
				}
			}
		} // Otherwise just leave pageSize as it is
		sheetSize = null;           // Won't need this anymore
		
		
		/*
		 * Choose which parameters need to be determined and construct
		 * a grid page builder.
		 */
		PageControllers pc = null; // TODO remove initial value after finishing
		if (pageSize == DimensionSettings.AUTO) {
			// Case A
			pc = casePageSize(doc, pageCount, rows, cols, orientation,
			                  direction, preprocess, common);
		} else if (gridType == GridType.AUTO) {
			// Case D
			assert pageSize.isValue();
			pc = caseGrid(doc, pageCount, pageSize.value(), orientation,
			              direction, preprocess, common);
		} else if (preprocess.isAutoSize()) {
			// Case C
			assert pageSize.isValue();
			pc = caseCellSize(doc, pageCount, rows, cols, pageSize.value(),
			                  orientation, direction, preprocess, common);
		} else if (common.getMargins() == MarginSettings.AUTO) {
			// Case B
			assert pageSize.isValue();
			pc = caseMargins(doc, pageCount, rows, cols, pageSize.value(),
			                 orientation, direction, preprocess, common);
		} else {
			// All are set, a conflict
			logger.verbose("nup_caseConflict");
			throw new IllegalStateException
					("Conflicting settings: cell count, page size, margins and cell size are all set");
		}
		Preprocessor preprocessor = pc.preprocessor;
		GridPage.Builder builder = pc.builder;
		int cellsPerPage = pc.cellsPerPage;
		
		/*logger.info("nup_finalSettings",
				builder.getRows(),
				builder.getColumns(),
				new Dimensions(builder.getCellWidth(),
				               builder.getCellHeight(),
				               Imposition.LENGTH_UNIT),
				"",
				"",
				"",
				"",
				new Dimensions(builder.getFuturePageWidth(),
				               builder.getFuturePageHeight(),
				               Imposition.LENGTH_UNIT),
				builder.getHorizontalOffset(),
				builder.getVerticalOffset(),
				builder.getOrientation(),
				builder.getFillDirection());*/
		
		// Pre-processing
		// TODO Pre-process only pages needed for pageCount
		if (preprocess != null) {
			doc = preprocessor.processAll();
		}
		
		/*
		 * FIXME: Resolve the page count only when filling the pages.
		 * For example, imposing 2x2 --nup-repeat currently produces
		 * only the first quarter of the output. (Ie. the page count
		 * does not take the page filling option into account.)
		 */
		
		/*
		 * If the number of pages is unset, calculate the number of pages
		 * necessary to fit the whole document; otherwise use the value.
		 */
		List<GridPage> pages;
		PageSource pageSrc = pageSourceBuilder(common, doc).build();
		if (pageCount < 0) {
			logger.verbose("nup_gridCount", cellsPerPage);
//			pageCount = Util.ceilingDivision(pageSrc.size(), cellsPerPage);
//			logger.verbose("nup_pageCountAll", pageCount);
			pages = new ArrayList<>();
		} else {
			logger.verbose("nup_pageCountPartial", pageCount);
			pages = new ArrayList<>(pageCount);
		}
		
		// Fill the output pages
		Iterator<VirtualPage> srcIter = pageSrc.iterator();
		switch (fillMode) {
			case FILL_PAGE:
				while (srcIter.hasNext()
						&& !(pageCount > 0 && pages.size() >= pageCount)) {
					addRepeatPage(srcIter, pages, builder);
				}
				break;
			case SEQUENTIAL:
				while (srcIter.hasNext()
						&& !(pageCount > 0 && pages.size() >= pageCount)) {
					addSequentialPage(srcIter, pages, builder);
				}
				break;
			case TWO_SIDED:
				while (srcIter.hasNext()
						&& !(pageCount > 0 && pages.size() >= pageCount)) {
					addDoublePage(srcIter, pages, builder);
				}
				break;
			default:
				throw new AssertionError(fillMode);
		}

		return pages;
	}
	
	private PageControllers casePageSize(VirtualDocument doc,
			int pageCount, final int rows, final int cols,
			final NUpOrientation orientation, final FillDirection direction,
			final Preprocessor.Settings preprocess, final CommonSettings common) {
		
		logger.verbose("nup_caseSize");
		
		final LengthUnit unit = Imposition.LENGTH_UNIT;
		
		// Resolve margins
		MarginSettings marginSettings = common.getMargins();
		Margins margins;
		switch (marginSettings.special()) {
			case AUTO:
				margins = new Margins(0, 0, 0, 0, LengthUnits.METRE);
				logger.verbose("nup_marginsResolveAuto", margins);
				break;
			case VALUE:
				margins = marginSettings.value();
				break;
			default:
				throw new AssertionError(marginSettings);
		}
		
		// Determine grid cell dimensions
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		Dimensions cell = preprocessor.getResolvedCellDimensions();
		double cellWidth = cell.width().in(unit);
		double cellHeight = cell.height().in(unit);
		
		// Determine page dimensions
		double pageWidth = Length.sum(cell.width().times(cols),
				margins.left(), margins.right()).in(unit);
		double pageHeight = Length.sum(cell.height().times(rows),
				margins.bottom(), margins.top()).in(unit);
		
		// A builder to provide the GridPages with desired settings
		GridPage.Builder builder = new GridPage.Builder()
				.setPageWidth(pageWidth)
				.setPageHeight(pageHeight)
				.setColumns(cols)
				.setRows(rows)
				.setCellWidth(cellWidth)
				.setCellHeight(cellHeight)
				.setHorizontalOffset(margins.left().in(unit))
				.setVerticalOffset(margins.bottom().in(unit))
				.setOrientation(orientation.getValue())
				.setFillDirection(direction.getValue());
		
		return new PageControllers(preprocessor, builder, rows*cols);
	}
	
	private PageControllers caseGrid(VirtualDocument doc,
			int pageCount, final Dimensions pageSize,
			final NUpOrientation orientation, final FillDirection direction,
			final Preprocessor.Settings preprocess, final CommonSettings common) {
		
		logger.verbose("nup_caseGrid");
		
		final LengthUnit unit = Imposition.LENGTH_UNIT;
		
		// Page dimensions
		double pageWidth = pageSize.width().in(unit);
		double pageHeight = pageSize.height().in(unit);
		
		// Cell dimensions
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		Dimensions cell = preprocessor.getResolvedCellDimensions();
		double cellWidth = cell.width().in(unit);
		double cellHeight = cell.height().in(unit);
		
		
		// Resolve margins
		MarginSettings marginSettings = common.getMargins();
		Margins margins;
		switch (marginSettings.special()) {
			case AUTO:
				margins = new Margins(0, 0, 0, 0, LengthUnits.METRE);
				logger.verbose("nup_marginsResolveAuto", margins);
				break;
			case VALUE:
				margins = marginSettings.value();
				break;
			default:
				throw new AssertionError(marginSettings);
		}
		
		double contentWidth = pageWidth - margins.left().in(unit)
		                      - margins.right().in(unit);
		double contentHeight = pageHeight - margins.top().in(unit)
		                       - margins.bottom().in(unit);
		// The maximum number of rows and columns fitting the page size
		// minus the margins:
		if (contentWidth <= 0 || contentHeight <= 0
				|| cellWidth <= 0 || cellHeight <= 0) {
			throw new IllegalStateException
					("The page content size is negative. Perhaps the margins are too wide?");
		}
		// Everything is positive numbers, so casting to int should do
		// the rounding-down
		int cols = (int) (contentWidth / cellWidth);
		int rows = (int) (contentHeight / cellHeight);
		
		// Gap between margins and content
		double horizontalGap = (contentWidth - cols * cellWidth) / 2;
		double verticalGap = (contentHeight - rows * cellHeight) / 2;
		
		// Setup the builder
		GridPage.Builder builder = new GridPage.Builder()
				.setPageWidth(pageWidth)
				.setPageHeight(pageHeight)
				.setColumns(cols)
				.setRows(rows)
				.setCellWidth(cellWidth)
				.setCellHeight(cellHeight)
				.setHorizontalOffset(margins.left().in(unit) + horizontalGap)
				.setVerticalOffset(margins.bottom().in(unit) + verticalGap)
				.setOrientation(orientation.getValue())
				.setFillDirection(direction.getValue());
		
		return new PageControllers(preprocessor, builder, rows*cols);
	}
	
	private PageControllers caseMargins(VirtualDocument doc,
			int pageCount, final int rows, final int cols,
			final Dimensions pageSize,
			final NUpOrientation orientation, final FillDirection direction,
			final Preprocessor.Settings preprocess, final CommonSettings common) {
		
		logger.verbose("nup_caseMargins");
		
		final LengthUnit unit = Imposition.LENGTH_UNIT;
		
		// Determine grid cell dimensions
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		Dimensions cell = preprocessor.getResolvedCellDimensions();
		double cellWidth = cell.width().in(unit);
		double cellHeight = cell.height().in(unit);
		
		// Page dimensions
		double pageWidth = pageSize.width().in(unit);
		double pageHeight = pageSize.height().in(unit);
		
		// Distribute the remaining space equally to margins on both sides
		double marginLeft = (pageWidth - cols * cellWidth) / 2;
		double marginBottom = (pageHeight - rows * cellHeight) / 2;
		
		if (marginLeft <= 0)
			throw new ArithmeticException(String.format(
					"Cannot fit %d columns of width %s into the page of size %s",
					 cols, cell.width(), pageSize));
		if (marginBottom <= 0)
			throw new ArithmeticException(String.format(
					"Cannot fit %d rows of height %s into the page of size %s",
					 cols, cell.height(), pageSize));
		
		// A builder to provide the GridPages with desired settings
		GridPage.Builder builder = new GridPage.Builder()
				.setPageWidth(pageWidth)
				.setPageHeight(pageHeight)
				.setColumns(cols)
				.setRows(rows)
				.setCellWidth(cellWidth)
				.setCellHeight(cellHeight)
				.setHorizontalOffset(marginLeft)
				.setVerticalOffset(marginBottom)
				.setOrientation(orientation.getValue())
				.setFillDirection(direction.getValue());
		
		return new PageControllers(preprocessor, builder, rows*cols);
	}
	
	private PageControllers caseCellSize(VirtualDocument doc,
			int pageCount, final int rows, final int cols,
			final Dimensions pageSize,
			final NUpOrientation orientation, final FillDirection direction,
			final Preprocessor.Settings preprocess, final CommonSettings common) {
		
		logger.verbose("nup_caseCell");
		
		final LengthUnit unit = Imposition.LENGTH_UNIT;
		
		/*
		 * If the preprocessor resizing is not set (auto), make it fit
		 * the cell, otherwise the default resizing will leave the pages
		 * overlapping or with gaps in between.
		 */
		if (preprocess.getResizing() == Resizing.AUTO) {
			preprocess.setResizing(Resizing.FIT);
			logger.verbose("nup_setResizingToFit");
		}
		
		// Page dimensions
		double pageWidth = pageSize.width().in(unit);
		double pageHeight = pageSize.height().in(unit);
		
		// Resolve margins
		MarginSettings marginSettings = common.getMargins();
		Margins margins;
		switch (marginSettings.special()) {
			case AUTO:
				margins = new Margins(0, 0, 0, 0, LengthUnits.METRE);
				logger.verbose("nup_marginsResolveAuto", margins);
				break;
			case VALUE:
				margins = marginSettings.value();
				break;
			default:
				throw new AssertionError(marginSettings);
		}
		
		// Grid cell dimensions
		double contentWidth = pageWidth
				- Length.sum(margins.left(), margins.right()).in(unit);
		double contentHeight = pageHeight
				- Length.sum(margins.bottom(), margins.top()).in(unit);
		
		if (contentWidth <= 0)
			throw new ArithmeticException
			("The margins are too wide for the page");
		if (contentHeight <= 0)
			throw new ArithmeticException
					("The margins are too tall for the page");
		
		double cellWidth = contentWidth / cols;
		double cellHeight = contentHeight / rows;
		
		// Pass those to the preprocessor
		Dimensions cell = new Dimensions(cellWidth, cellHeight, unit);
		preprocess.setCellDimensions(DimensionSettings.of(cell));
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		
		// A builder to provide the GridPages with desired settings
		GridPage.Builder builder = new GridPage.Builder()
				.setPageWidth(pageWidth)
				.setPageHeight(pageHeight)
				.setColumns(cols)
				.setRows(rows)
				.setCellWidth(cellWidth)
				.setCellHeight(cellHeight)
				.setHorizontalOffset(margins.left().in(unit))
				.setVerticalOffset(margins.bottom().in(unit))
				.setOrientation(orientation.getValue())
				.setFillDirection(direction.getValue());
		
		return new PageControllers(preprocessor, builder, rows*cols);
	}
	
	private static void addSequentialPage(Iterator<VirtualPage> srcIter,
			List<GridPage> target, GridPage.Builder pageBuilder) {
		int lastPageNumber = target.size();
		if (srcIter.hasNext()) {
			GridPage page = pageBuilder.build();
			page.setNumber(++lastPageNumber);
			for (PageletView pg : page.pagelets()) {
				if (srcIter.hasNext())
					pg.setSource(srcIter.next());
			}
			target.add(page);
		}
	}
	
	private static void addRepeatPage(Iterator<VirtualPage> srcIter,
			List<GridPage> target, GridPage.Builder pageBuilder) {
		int lastPageNumber = target.size();
		if (srcIter.hasNext()) {
			GridPage page = pageBuilder.build();
			page.setNumber(++lastPageNumber);
			VirtualPage vp = srcIter.next();
			for (PageletView pg : page.pagelets()) {
				pg.setSource(vp);
			}
			target.add(page);
		}
	}
	
	private static void addDoublePage(Iterator<VirtualPage> srcIter,
			List<GridPage> target, GridPage.Builder pageBuilder) {
		int lastPageNumber = target.size();
		if (srcIter.hasNext()) {
			GridPage recto = pageBuilder.build();
			// TODO: Handle mirrored margins!
			pageBuilder.setFillDirection(mirrorVertically(pageBuilder.getFillDirection()));
			GridPage verso = pageBuilder.build();
			pageBuilder.setFillDirection(mirrorVertically(pageBuilder.getFillDirection()));
			recto.setNumber(++lastPageNumber);
			verso.setNumber(++lastPageNumber);
			Iterator<PageletView> rectoIter = recto.pagelets().iterator();
			Iterator<PageletView> versoIter = verso.pagelets().iterator();
			while (rectoIter.hasNext() && versoIter.hasNext()) {
				if (srcIter.hasNext()) {
					rectoIter.next().setSource(srcIter.next());
				}
				if (srcIter.hasNext()) {
					versoIter.next().setSource(srcIter.next());
				} else {
					break;      // Short-circuit out of rectoIter and versoIter
				}
			}
			target.add(recto);
			target.add(verso);
		}
	}
	
	private static class PageControllers {
		private final Preprocessor preprocessor;
		private final GridPage.Builder builder;
		private final int cellsPerPage;
		
		private PageControllers(Preprocessor preprocessor, GridPage.Builder builder, int cellsPerPage) {
			this.preprocessor = preprocessor;
			this.builder = builder;
			this.cellsPerPage = cellsPerPage;
		}
	}
	
	@Override
	public ImposableBuilder<NUp> acceptPreprocessSettings(Settings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Preprocess settings cannot be null");
		this.preprocess = settings.copy();
		return this;
	}
	
	@Override
	public ImposableBuilder<NUp> acceptCommonSettings(CommonSettings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Settings cannot be null");
		this.common = settings;
		return this;
	}
	
	@Override
	public ImposableBuilder<NUp> acceptRenderingSettings(RenderingSettings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Rendering settings cannot be null");
		this.render = settings;
		return this;
	}
	
	@Override
	public NUp build() {
		return this;
	}
	
	@Override
	public ImpositionTask buildTask() {
		return ImpositionTaskFactory.oneSided(build());
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * {@inheritDoc}
	 * @return always the value of {@code false}
	 */
//	@Override
	@Deprecated
	public boolean prefersMultipleInput() {
		return false;
	}

//	@Override
	public LoosePages impose(VirtualDocument source) {
		return new LoosePages(imposeAsPages(source));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * N-up handles multiple document input by first concatenating them
	 * into one document in the order they appear in the argument.
	 */
	@Override
	public LoosePages impose(List<VirtualDocument> sources) {
		return impose(VirtualDocument.concatenate(sources));
	}

	/** Represents orientation in one of the four principal directions. */
	public static enum NUpOrientation {
		/** No rotation */
		UPRIGHT (GridPage.GridOrientation.UPRIGHT),
		/** Rotated right by 90 degrees */
		ROTATED_RIGHT (GridPage.GridOrientation.ROTATED_RIGHT),
		/** Rotated by 180 degrees */
		UPSIDE_DOWN (GridPage.GridOrientation.UPSIDE_DOWN),
		/** Rotated left by 90 degrees */
		ROTATED_LEFT (GridPage.GridOrientation.ROTATED_LEFT);
		
		/** The underlying orientation object used in implementation */
		private GridPage.GridOrientation value;
		
		private NUpOrientation(GridPage.GridOrientation orientation) {
			this.value = orientation;
		}
		
		public GridPage.GridOrientation getValue() {
			return value;
		}
	}

	public static enum FillDirection {
		ROWS (GridPage.Direction.ROWS),
		COLUMNS (GridPage.Direction.COLUMNS),
		ROWS_REVERSE (GridPage.Direction.ROWS_REVERSE),
		COLUMNS_REVERSE (GridPage.Direction.COLUMNS_REVERSE);
		
		/** The underlying direction object used in implementation */
		private GridPage.Direction value;
		
		private FillDirection(GridPage.Direction direction) {
			this.value = direction;
		}
		
		GridPage.Direction getValue() {
			return value;
		}
	}
	
	private static GridPage.Direction mirrorVertically(GridPage.Direction dir) {
		switch (dir) {
			case COLUMNS:
				return GridPage.Direction.COLUMNS_REVERSE;
			case COLUMNS_REVERSE:
				return GridPage.Direction.COLUMNS;
			case ROWS:
				return GridPage.Direction.ROWS_REVERSE;
			case ROWS_REVERSE:
				return GridPage.Direction.ROWS;
			default:
				throw new AssertionError(dir);
		}
	}
	
	/**
	 * Options for filling input pages into the imposed document.
	 */
	public static enum FillMode {
		/**
		 * Each input page is used once.
		 * This is the default mode.
		 */
		SEQUENTIAL,
		/**
		 * Each page is used to populate entire output page before
		 * moving onto next input page.
		 */
		FILL_PAGE,
		/**
		 * Keep the verso of each input page aligned with its respective
		 * recto in the output.
		 */
		TWO_SIDED;
	}
	
	/**
	 * The number of rows and columns in the grid.
	 */
	static class GridDimensions extends SpecVal<GridType, IntegerDimensions> {

		private static final Map<GridType, GridDimensions> instanceMap = new EnumMap<>(GridType.class);
		static {
			for (GridType type : GridType.values()) {
				instanceMap.put(type, new GridDimensions(type));
			}
		}
		
		private GridDimensions(GridType type) {
			super(type);
		}
		
		private GridDimensions(IntegerDimensions value) {
			super(value);
		}
		
		public static GridDimensions of(GridType type) {
			return instanceMap.get(type);
		}
		
		public static GridDimensions of(IntegerDimensions value) {
			return new GridDimensions(value);
		}

		@Override
		protected GridType getValueConstant() {
			return GridType.VALUE;
		}
	}
	
	static enum GridType {
		/** Grid is given explicitly by number of columns and rows */
		VALUE,
		/** Grid should be determined from other settings */
		AUTO;
	}
}
