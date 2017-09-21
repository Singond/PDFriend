package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.Util;
import com.github.singond.pdfriend.book.GridPage;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.Preprocessor.Resizing;
import com.github.singond.pdfriend.imposition.Preprocessor.Settings;

/**
 * An n-up layout.
 * This is a simple imposition task which places several pages onto
 * a larger page.
 * @author Singon
 */
public class NUp implements Imposable {

	/** The internal name of this imposable document type */
	private static final String NAME = "n-up";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(NUp.class);
	
	private int rows = 1;
	private int cols = 1;
	private GridType gridType = GridType.VALUE;
	private NUpOrientation orientation = NUpOrientation.UPRIGHT;
	private FillDirection direction = FillDirection.ROWS;
	private Preprocessor.Settings preprocess = null;
	private CommonSettings common = null;
	
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
	 * Imposes the given virtual document into a list of grid pages
	 * according to the current settings of this {@code NUp} object.
	 */
	public List<Page> imposeAsPages(VirtualDocument doc) {
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
		Dimensions pageSize = common.getPageSize();
		Dimensions sheetSize = common.getSheetSize();
		if (sheetSize != CommonSettings.AUTO_DIMENSIONS) {
			if (pageSize == CommonSettings.AUTO_DIMENSIONS) {
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
		if (pageSize == CommonSettings.AUTO_DIMENSIONS) {
			// Case A
			pc = casePageSize(doc, pageCount, rows, cols, orientation,
			                  direction, preprocess, common);
		} else if (gridType == GridType.AUTO) {
			// Case D
			pc = caseGrid(doc, pageCount, pageSize, orientation,
			              direction, preprocess, common);
		} else if (preprocess.isAutoSize()) {
			// Case C
			pc = caseCellSize(doc, pageCount, rows, cols, pageSize,
			                  orientation, direction, preprocess, common);
		} else if (common.getMargins() == CommonSettings.AUTO_MARGINS) {
			// Case B
			pc = caseMargins(doc, pageCount, rows, cols, pageSize,
			                 orientation, direction, preprocess, common);
		} else {
			// All are set, a conflict
//			throw new IllegalStateException("Cell count, page size , ")
		}
		Preprocessor preprocessor = pc.preprocessor;
		GridPage.Builder builder = pc.builder;
		int cellsPerPage = pc.cellsPerPage;
		
		/*
		 * If the number of pages is unset, calculate the number of pages
		 * necessary to fit the whole document; otherwise use the value.
		 */
		if (pageCount < 0) {
			logger.verbose("nup_gridCount", cellsPerPage);
			pageCount = Util.ceilingDivision(doc.getLength(), cellsPerPage);
			logger.verbose("nup_settingPagesNo", pageCount);
		}
		
		// Pre-processing
		// TODO Pre-process only pages needed for pageCount
		if (preprocess != null) {
			doc = preprocessor.processAll();
		}
		
		// Output
		List<Page> pages = new ArrayList<>(pageCount);
		int pageNumber = 0;
		while (pages.size() < pageCount) {
			GridPage page = builder.build();
			page.setNumber(++pageNumber);
			pages.add(page);
		}
		new SequentialSourceProvider(doc).setSourceTo(pages);

		return pages;
	}
	
	private PageControllers casePageSize(VirtualDocument doc,
			int pageCount, final int rows, final int cols,
			final NUpOrientation orientation, final FillDirection direction,
			final Preprocessor.Settings preprocess, final CommonSettings common) {
		
		logger.verbose("nup_caseSize");
		
		final LengthUnit unit = Imposition.LENGTH_UNIT;
		
		// Resolve margins
		Margins margins = common.getMargins();
		if (margins == CommonSettings.AUTO_MARGINS) {
			margins = new Margins(0, 0, 0, 0, LengthUnits.METRE);
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
		
		// Margins
		Margins margins = common.getMargins();
		if (margins == CommonSettings.AUTO_MARGINS) {
			margins = new Margins(0, 0, 0, 0, LengthUnits.METRE);
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
		}
		
		// Page dimensions
		double pageWidth = pageSize.width().in(unit);
		double pageHeight = pageSize.height().in(unit);
		
		// Resolve margins
		Margins margins = common.getMargins();
		if (margins == CommonSettings.AUTO_MARGINS) {
			margins = new Margins(0, 0, 0, 0, LengthUnits.METRE);
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
		preprocess.setCellDimensions(cell);
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

	/**
	 * Imposes the given virtual document into a new virtual document
	 * according to the current settings of this {@code NUp} object.
	 */
	public VirtualDocument imposeAsDocument(VirtualDocument source) {
		List<Page> pages = imposeAsPages(source);
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		for (Page page : pages) {
			doc.addPage(page.render());
		}
		return doc.build();
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void acceptPreprocessSettings(Settings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Preprocess settings cannot be null");
		this.preprocess = settings.copy();
	}
	
	@Override
	public void acceptCommonSettings(CommonSettings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Settings cannot be null");
		this.common = settings;
	}

	/**
	 * {@inheritDoc}
	 * @return always the value of {@code false}
	 */
	@Override
	public boolean prefersMultipleInput() {
		return false;
	}

	@Override
	public VirtualDocument impose(VirtualDocument source) {
		return imposeAsDocument(source);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * N-up handles multiple document input by first concatenating them
	 * into one document in the order they appear in the argument.
	 */
	@Override
	public VirtualDocument impose(List<VirtualDocument> sources) {
		return imposeAsDocument(VirtualDocument.concatenate(sources));
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
		COLUMNS (GridPage.Direction.COLUMNS);
		
		/** The underlying direction object used in implementation */
		private GridPage.Direction value;
		
		private FillDirection(GridPage.Direction direction) {
			this.value = direction;
		}
		
		public GridPage.Direction getValue() {
			return value;
		}
	}
	
	private static enum GridType {
		/** Grid is given explicitly by number of columns and rows */
		VALUE,
		/** Grid should be determined from other settings */
		AUTO;
	}
}
