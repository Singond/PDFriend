package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.Util;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.model.GridPage;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.modules.Impose;

/**
 * An n-up layout.
 * This is a simple imposition task which places several pages onto
 * a larger page.
 * @author Singon
 */
public class NUp implements Imposable {

	private final List<Page> pages;
	
	private static ExtendedLogger logger = Log.logger(NUp.class);
	
	/**
	 * A visitor to invoke pagelet resizing on a grid page based on PageSize.
	 */
//	private static final PageSize.Visitor<GridPage, GridPageParams> GRID_PROVIDER
//			= new PageSize.Visitor<GridPage, GridPageParams>() {
//
//		/** A helper GridPage factory */
//		private GridPage gridFromTemplate(GridPageParams template,
//		                                  double cellWidth, double cellHeight) {
//			GridPage grid = new GridPage(template.cols, template.rows,
//			                             cellWidth, cellHeight,
//			                             template.horizontalOffset,
//			                             template.verticalOffset,
//			                             template.orientation.getValue());
//			return grid;
//		}
//
//		@Override
//		public GridPage visit(Scale size, GridPageParams template) {
//			GridPage grid = gridFromTemplate(template,
//			                                 template.cell.width().in(Impose.LENGTH_UNIT)*size.scalePage(),
//			                                 template.cell.height().in(Impose.LENGTH_UNIT)*size.scalePage());
//			grid.scalePages(size.scalePage());
//			return grid;
//		}
//
//		@Override
//		public GridPage visit(FitToLargest size, GridPageParams template) {
//			GridPage grid = gridFromTemplate(template,
//			                                 template.cell.width().in(Impose.LENGTH_UNIT),
//			                                 template.cell.height().in(Impose.LENGTH_UNIT));
//			grid.fitPages();
//			return grid;
//		}
//	};
	
	/**
	 * Constructs a new n-up document with the given layout.
	 * @param pages
	 * @param cols
	 * @param rows
	 * @param cell
	 * @param horizontalOffset
	 * @param verticalOffset
	 * @param orientation
	 * @param direction
	 * @param pageSize
	 */
	public NUp(int pages, int cols, int rows, Dimensions cell,
	           double horizontalOffset, double verticalOffset,
	           NUpOrientation orientation, FillDirection direction) {
		GridPageParams template = new GridPageParams(cols, rows, cell,
				horizontalOffset, verticalOffset, orientation);
		List<Page> pageList = new ArrayList<>();
		int pageNumber = 0;
		GridPage.Builder builder = new GridPage.Builder()
				.setColumns(cols)
				.setRows(rows)
				.setCellWidth(cell.width().in(Impose.LENGTH_UNIT))
				.setCellHeight(cell.height().in(Impose.LENGTH_UNIT))
				.setHorizontalOffset(horizontalOffset)
				.setVerticalOffset(verticalOffset)
				.setOrientation(orientation.getValue());
		while(pageList.size() < pages) {
//			GridPage page = new GridPage(template);
//			pageSize.invite(PAGE_SIZER, page);
//			GridPage page = pageSize.invite(GRID_PROVIDER, template);
			GridPage page = builder.build();
			page.setNumber(++pageNumber);
			pageList.add(page);
		}
		this.pages = pageList;
	}

	@Override
	public VirtualDocument getDocument() {
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		for (Page page : pages) {
			doc.addPage(page.render());
		}
		return doc.build();
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

	/**
	 * Builds NUp objects.
	 */
	public static class Builder {
		// -1 indicates an unset field
		private int pages = -1;
		private int rows = 1;
		private int cols = 1;
		private double horizontalOffset = 0;
		private double verticalOffset = 0;
		private NUpOrientation orientation = NUpOrientation.UPRIGHT;
		private FillDirection direction = FillDirection.ROWS;
		
		public Builder setNumberOfPages(int pages) {
			this.pages = pages;
			return this;
		}

		public Builder setRows(int rows) {
			if (rows < 1)
				throw new IllegalArgumentException("Number of rows must be positive");
			this.rows = rows;
			return this;
		}

		public Builder setCols(int cols) {
			if (cols < 1)
				throw new IllegalArgumentException("Number of columns must be positive");
			this.cols = cols;
			return this;
		}

		public Builder setHorizontalOffset(double horizontalOffset) {
			this.horizontalOffset = horizontalOffset;
			return this;
		}

		public Builder setVerticalOffset(double verticalOffset) {
			this.verticalOffset = verticalOffset;
			return this;
		}

		public Builder setOrientation(NUpOrientation orientation) {
			if (orientation == null)
				throw new IllegalArgumentException("Orientation must not be null");
			this.orientation = orientation;
			return this;
		}

		public Builder setDirection(FillDirection direction) {
			if (orientation == null)
				throw new IllegalArgumentException("Flip direction must not be null");
			this.direction = direction;
			return this;
		}

		public NUp buildFor(VirtualDocument doc) {
			int pages = this.pages;
			int rows = this.rows;
			int cols = this.cols;
			
			// The rows and cols arguments should be OK, but check them anyway
			if (rows < 1 || cols < 1) {
				throw new IllegalArgumentException(String.format
						("Wrong number of cells in grid: %dx%d", rows, cols));
			}
			
			/*
			 * If the number of pages is unset, calculate the number of pages
			 * necessary to fit the whole document; otherwise use the value.
			 */
			if (pages < 0) {
				logger.verbose("nup_gridDimensions", rows, cols);
				pages = Util.ceilingDivision(doc.getLength(), rows*cols);
				logger.verbose("nup_settingPagesNo", pages);
			}
			
			// Determine grid cell dimensions
			double[] docFormat = doc.maxPageDimensions();
			Dimensions cell = new Dimensions(docFormat[0], docFormat[1], Impose.LENGTH_UNIT);
			
			NUp result = new NUp(pages, cols, rows, cell,
			                     horizontalOffset, verticalOffset,
			                     orientation, direction);
			new SequentialSourceProvider(doc).setSourceTo(result.pages);
			return result;
		}
	}
	
	/**
	 * A simple data object to aggregate GridPage parameters.
	 */
	private static class GridPageParams {
		private final int cols;
		private final int rows;
		private final Dimensions cell;
		private final double horizontalOffset;
		private final double verticalOffset;
		private final NUpOrientation orientation;
		
		private GridPageParams(int cols, int rows, Dimensions cell, double horizontalOffset,
		                       double verticalOffset, NUpOrientation orientation) {
			this.cols = cols;
			this.rows = rows;
			this.cell = cell;
			this.horizontalOffset = horizontalOffset;
			this.verticalOffset = verticalOffset;
			this.orientation = orientation;
		}
	}
}
