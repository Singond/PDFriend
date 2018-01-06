package com.github.singond.pdfriend.book;

import java.awt.geom.AffineTransform;

import com.github.singond.collections.ArrayMatrix;
import com.github.singond.collections.Matrix;
import com.github.singond.collections.MatrixIterable;
import com.github.singond.collections.MatrixIterator;


/**
 * A type of MultiPage which provides tabular layout of pagelets.
 * <p>
 * The pagelets are arranged in an m x n rectangular grid and they
 * are accessed by their indices in this grid.
 * The column indices start at the leftmost column with index 0 and increase
 * to the right, while the row indices start at the topmost row with index 0
 * and increase when moving downwards.
 * </p><p>
 * A warning about the implementation: The index of a pagelet in the matrix
 * is not directly bound to the pagelet's position on the page. The index
 * serves merely as a way to access the pagelet, but the correspondence
 * of the index to the pagelet position on page must be explicitly established
 * during construction.
 * </p>
 *
 * @author Singon
 */
public class GridPage extends MultiPage {
	
	/**
	 * The grid of pagelets comprising the content of this page.
	 * This collection is only a reference for working with the pagelets
	 * in this GridPage. This collection must therefore be synchronized
	 * with the set of Pagelets in the superclass.
	 */
	private final Matrix<Pagelet> matrix;
	/**
	 * Traversal direction of the grid.
	 * Specifies the order in which the cells of the grid should be visited
	 * when iterating.
	 */
	private Direction direction = Direction.ROWS;
	/**
	 * Orientation of the grid, rotation in multiples of right angle.
	 */
	private final GridOrientation orientation;

	/**
	 * Constructs a new GridPage with cells of the given dimensions.
	 * @param columns number of columns in the grid
	 * @param rows number of rows in the grid
	 * @param cellWidth width of a single cell
	 * @param cellHeight height of a single cell
	 * @param orientation orientation of the grid
	 */
	public GridPage(double pageWidth, double pageHeight,
	                int columns, int rows,
	                double cellWidth, double cellHeight,
	                double horizontalOffset, double verticalOffset,
	                GridOrientation orientation) {
		super(pageWidth, pageHeight);
		this.orientation = orientation;
		
		/** Grid position */
		AffineTransform gridPos = AffineTransform.getTranslateInstance
				(horizontalOffset, verticalOffset);
		gridPos.rotate(orientation.angle);
		switch (orientation) {
			case UPRIGHT:
			default:
				break;
			case ROTATED_LEFT:
				gridPos.translate(0, -getWidth());
				break;
			case UPSIDE_DOWN:
				gridPos.translate(-getWidth(), -getHeight());
				break;
			case ROTATED_RIGHT:
				gridPos.translate(-getHeight(), 0);
				break;
		}
		
		// Initialize the matrix with null values
		Matrix<Pagelet> cells = new ArrayMatrix<>(rows, columns);
		// Fill the matrix with pagelets
		MatrixIterator<Pagelet> iterator = cells.horizontally().iterator();
		while (iterator.hasNext()) {
			iterator.next();
			int[] index = iterator.previousIndex();
			AffineTransform position = new AffineTransform(gridPos);
			position.translate(cellWidth*index[1], cellHeight*(rows-index[0]-1));
			// TODO Use SimplePagelet
			Pagelet pagelet = new AutoPagelet(cellWidth, cellHeight, position);
			cells.set(index[0], index[1], pagelet);
			super.addPagelet(pagelet);
		}
		// Set the matrix as the backing matrix
		matrix = cells;
	}
	
	/**
	 * Constructs a new GridPage with cells of the given dimensions.
	 * The page dimensions are calculated so as to fit the upper right
	 * vertex of the grid.
	 * @param columns number of columns in the grid
	 * @param rows number of rows in the grid
	 * @param cellWidth width of a single cell
	 * @param cellHeight height of a single cell
	 * @param orientation orientation of the grid
	 */
	public GridPage(int columns, int rows,
	                double cellWidth, double cellHeight,
	                double horizontalOffset, double verticalOffset,
	                GridOrientation orientation) {
		this(pageWidth(columns, rows, cellWidth, cellHeight, orientation) + horizontalOffset,
		     pageHeight(columns, rows, cellWidth, cellHeight, orientation) + verticalOffset,
		     columns, rows, cellWidth, cellHeight, horizontalOffset, verticalOffset, orientation);
	}

	/**
	 * Constructs a new GridPage with cells of the given dimensions,
	 * in the default upright orientation.
	 * @param columns number of columns in the grid
	 * @param rows number of rows in the grid
	 * @param cellWidth width of a single cell
	 * @param cellHeight height of a single cell
	 */
	public GridPage(int columns, int rows,
	                double cellWidth, double cellHeight,
	                double horizontalOffset, double verticalOffset) {
		this(columns, rows, cellWidth, cellHeight, horizontalOffset,
		     verticalOffset, GridOrientation.UPRIGHT);
	}
	
	/**
	 * Constructs a new GridPage with cells of the given dimensions,
	 * in the default upright orientation.
	 * @param columns number of columns in the grid
	 * @param rows number of rows in the grid
	 * @param cellWidth width of a single cell
	 * @param cellHeight height of a single cell
	 */
	public GridPage(int columns, int rows,
	                double cellWidth, double cellHeight,
	                GridOrientation orientation) {
		this(columns, rows, cellWidth, cellHeight, 0, 0, orientation);
	}
	
	/**
	 * Constructs a new GridPage with cells of the given dimensions,
	 * in the default upright orientation.
	 * @param columns number of columns in the grid
	 * @param rows number of rows in the grid
	 * @param cellWidth width of a single cell
	 * @param cellHeight height of a single cell
	 */
	public GridPage(int columns, int rows,
	                double cellWidth, double cellHeight) {
		this(columns, rows, cellWidth, cellHeight, 0, 0, GridOrientation.UPRIGHT);
	}
	
	/**
	 * A copy constructor.
	 * @param original
	 */
	public GridPage(GridPage original) {
		super(original.getWidth(), original.getHeight());
		this.orientation = original.orientation;
		this.direction = original.direction; // (This one's not final)
		
		int[] dimensions = ((ArrayMatrix<?>) original.matrix).getDimensions();
		Matrix<Pagelet> cells = new ArrayMatrix<>(dimensions[0], dimensions[1]);
		MatrixIterator<Pagelet> origIter = original.matrix.horizontally().iterator();
		
		while (origIter.hasNext()) {
			Pagelet p = origIter.next().copy();
			int[] coords = origIter.previousIndex();
			cells.set(coords[0], coords[1], p);
			super.addPagelet(p);
		}
		this.matrix = cells;
	}
	
	/**
	 * Sets the order in which the cells of the grid should be visited
	 * when iterating.
	 * @param direction either by rows or by columns
	 * @see Direction
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public GridOrientation getOrientation() {
		return orientation;
	}
	
	/** Calculates necessary page width to fit the grid */
	private static double pageWidth(int columns, int rows,
	                                double cellWidth, double cellHeight,
	                                GridOrientation orientation) {
		switch (orientation) {
			case UPRIGHT:
			case UPSIDE_DOWN:
			default:
				return cellWidth * columns;
			case ROTATED_LEFT:
			case ROTATED_RIGHT:
				return cellHeight * rows;
		}
	}

	/** Calculates necessary page height to fit the grid */
	private static double pageHeight(int columns, int rows,
	                                 double cellWidth, double cellHeight,
	                                 GridOrientation orientation) {
		switch (orientation) {
			case UPRIGHT:
			case UPSIDE_DOWN:
			default:
				return cellHeight * rows;
			case ROTATED_LEFT:
			case ROTATED_RIGHT:
				return cellWidth * columns;
		}
	}

	/**
	 * Provides a way to iterate through the pages in the order specified
	 * by {@code direction} argument.
	 * @param direction the manner in which to iterate
	 * @return an iterable traversing the grid in {@code direction}
	 */
	public MatrixIterable<PageletView> pagelets(Direction direction) {
		switch (direction) {
			case COLUMNS:
				return pageletViewIterator(matrix.vertically());
			case ROWS:
				return pageletViewIterator(matrix.horizontally());
			default:
				throw new AssertionError("Bad GridPage direction is set: "+direction);
		}
	}
	
	/**
	 * Provides a way to iterate through the pages in the order specified
	 * by the current value of the {@code direction} field.
	 * @return an iterable traversing the grid according to current value
	 *         of the {@code direction} field
	 */
	public MatrixIterable<PageletView> pagelets() {
		return pagelets(direction);
	}
	
	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}
	
	/**
	 * Specifies the order in which this grid page will be iterated.
	 * This will affect the order of source pages on the page.
	 */
	public enum Direction {
		/** Traverse the grid from left to right, top to bottom */
		ROWS,
		/** Traverse the grid from top to bottom, left to right */
		COLUMNS;
	}
	
	/**
	 * The angle by which the direction of the grid row from left to right,
	 * deviates from the positive x-coordinate.
	 */
	public enum GridOrientation {
		UPRIGHT (0),
		ROTATED_LEFT (Math.PI/2),
		UPSIDE_DOWN (Math.PI),
		ROTATED_RIGHT (Math.PI*3/2);
		
		public final double angle;
		
		private GridOrientation(double angle) {
			this.angle = angle;
		}
	}
	
	/**
	 * A builder of {@code GridPage} objects.
	 * This builder is reusable, ie. a single instance can generate
	 * {@code GridPage} objects repeatedly, possibly changing settings
	 * between each invocation of {@link #build}.
	 * All the {@code setXXX} methods return this builder object so as
	 * to enable chaining invocations.
	 */
	public static final class Builder {
		private double pageWidth = -1;
		private double pageHeight = -1;
		private int columns;
		private int rows;
		private double cellWidth;
		private double cellHeight;
		private double horizontalOffset;
		private double verticalOffset;
		private GridOrientation orientation;
		private Direction fillDirection;
		
		/**
		 * Builds a new instance of {@code GridPage} according to current
		 * settings of this {@code Builder}.
		 * @return
		 */
		public GridPage build() {
			if (pageWidth < 0)
				pageWidth = pageWidth(columns, rows, cellWidth, cellHeight, orientation)
						+ horizontalOffset;
			if (pageHeight < 0)
				pageHeight = pageHeight(columns, rows, cellWidth, cellHeight, orientation)
						+ verticalOffset;
			
			GridPage page = new GridPage
					(pageWidth, pageHeight, columns, rows, cellWidth, cellHeight,
					 horizontalOffset, verticalOffset, orientation);
			page.setDirection(fillDirection);
			return page;
		}
		
		/** Performs preliminary resolution of the page width */
		public double getFuturePageWidth() {
			return pageWidth;
		}
		/** Performs preliminary resolution of the page height */
		public double getFuturePageHeight() {
			return pageHeight;
		}
		public int getColumns() {
			return columns;
		}
		public int getRows() {
			return rows;
		}
		public double getCellWidth() {
			return cellWidth;
		}
		public double getCellHeight() {
			return cellHeight;
		}
		public double getHorizontalOffset() {
			return horizontalOffset;
		}
		public double getVerticalOffset() {
			return verticalOffset;
		}
		public GridOrientation getOrientation() {
			return orientation;
		}
		public Direction getFillDirection() {
			return fillDirection;
		}
		
		public Builder setPageWidth(double pageWidth) {
			this.pageWidth = pageWidth;
			return this;
		}
		public Builder setPageHeight(double pageHeight) {
			this.pageHeight = pageHeight;
			return this;
		}
		public Builder setColumns(int columns) {
			this.columns = columns;
			return this;
		}
		public Builder setRows(int rows) {
			this.rows = rows;
			return this;
		}
		public Builder setCellWidth(double cellWidth) {
			this.cellWidth = cellWidth;
			return this;
		}
		public Builder setCellHeight(double cellHeight) {
			this.cellHeight = cellHeight;
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
		public Builder setOrientation(GridOrientation orientation) {
			this.orientation = orientation;
			return this;
		}
		public Builder setFillDirection(Direction fillDirection) {
			this.fillDirection = fillDirection;
			return this;
		}
	}
}
