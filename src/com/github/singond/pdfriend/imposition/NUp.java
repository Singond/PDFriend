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
	
	// -1 indicates an unset field
	private int pages = -1;
	private int rows = 1;
	private int cols = 1;
	private GridType gridType = GridType.VALUE;
	private double horizontalOffset = 0;
	private double verticalOffset = 0;
	private NUpOrientation orientation = NUpOrientation.UPRIGHT;
	private FillDirection direction = FillDirection.ROWS;
	private Preprocessor.Settings preprocess = null;
	private CommonSettings common = null;
	
	/**
	 * Sets the number of pages to be created.
	 * @param pages the number of pages
	 * @return this NUp object
	 */
	public NUp setNumberOfPages(int pages) {
		this.pages = pages;
		return this;
	}

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
	 * Sets how much the grid is offset from the lower left corner.
	 * @param horizontalOffset the offset distance (positive values move
	 *        the grid to the right)
	 * @return this NUp object
	 */
	public NUp setHorizontalOffset(double horizontalOffset) {
		this.horizontalOffset = horizontalOffset;
		return this;
	}


	/**
	 * Sets how much the grid is offset from the lower left corner.
	 * @param verticalOffset the offset distance (positive values move
	 *        the grid upwards)
	 * @return this NUp object
	 */
	public NUp setVerticalOffset(double verticalOffset) {
		this.verticalOffset = verticalOffset;
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
		int pageCount = this.pages;
		final int rows = this.rows;
		final int cols = this.cols;
		final Preprocessor.Settings preprocess = this.preprocess;
		final CommonSettings common = this.common;

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
		
		
		if (rows < 1 || cols < 1) {
			// Grid rows and columns unset
			// TODO Build GridPage from known
		}
		
		// The rows and cols arguments should be OK, but check them anyway
//		if (rows < 1 || cols < 1) {
//			throw new IllegalStateException(String.format
//					("Wrong number of cells in grid: %dx%d", rows, cols));
//		}
		
		/*
		 * If the number of pages is unset, calculate the number of pages
		 * necessary to fit the whole document; otherwise use the value.
		 */
		if (pageCount < 0) {
			logger.verbose("nup_gridDimensions", rows, cols);
			pageCount = Util.ceilingDivision(doc.getLength(), rows*cols);
			logger.verbose("nup_settingPagesNo", pageCount);
		}
		
		// Determine grid cell dimensions
		double[] docFormat = doc.maxPageDimensions();
		Dimensions cell = new Dimensions(docFormat[0], docFormat[1], Imposition.LENGTH_UNIT);
		
		// A builder to provide the GridPages with desired settings
		GridPage.Builder builder = new GridPage.Builder()
				.setColumns(cols)
				.setRows(rows)
				.setCellWidth(cell.width().in(Imposition.LENGTH_UNIT))
				.setCellHeight(cell.height().in(Imposition.LENGTH_UNIT))
				.setHorizontalOffset(horizontalOffset)
				.setVerticalOffset(verticalOffset)
				.setOrientation(orientation.getValue())
				.setFillDirection(direction.getValue());

		// Pre-processing
		if (preprocess != null) {
			doc = new Preprocessor(doc, preprocess).processAll();
		}
		
		// Output
		List<Page> pages = new ArrayList<>(pageCount);
		int pageNumber = 0;
		while(pages.size() < pageCount) {
			GridPage page = builder.build();
			page.setNumber(++pageNumber);
			pages.add(page);
		}
		new SequentialSourceProvider(doc).setSourceTo(pages);

		return pages;
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
		this.preprocess = settings.copy();
	}
	
	@Override
	public void acceptCommonSettings(CommonSettings settings) {
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
