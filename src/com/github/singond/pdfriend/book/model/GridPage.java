package com.github.singond.pdfriend.book.model;

import java.awt.geom.AffineTransform;

import com.github.singond.collections.ArrayMatrix;
import com.github.singond.collections.Matrix;
import com.github.singond.collections.MatrixIterable;
import com.github.singond.collections.MatrixIterator;
import com.github.singond.pdfriend.book.control.PageVisitor;


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
	public GridPage(int columns, int rows,
	                double cellWidth, double cellHeight,
	                double horizontalOffset, double verticalOffset,
	                GridOrientation orientation) {
		super(pageWidth(columns, rows, cellWidth, cellHeight, orientation),
		      pageHeight(columns, rows, cellWidth, cellHeight, orientation));
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
		MatrixIterator<Pagelet> iterator = cells.horizontallyAll().iterator();
		while (iterator.hasNext()) {
			iterator.next();
			int[] index = iterator.previousIndex();
			AffineTransform position = new AffineTransform(gridPos);
			position.translate(cellWidth*index[1], cellHeight*(rows-index[0]-1));
			Pagelet pagelet = new Pagelet(cellWidth, cellHeight, position);
			cells.set(index[0], index[1], pagelet);
			super.addPagelet(pagelet);
		}
		// Set the matrix as the backing matrix
		matrix = cells;
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
		MatrixIterator<Pagelet> origIter = original.matrix.horizontallyAll().iterator();
		
		while (origIter.hasNext()) {
			Pagelet p = new Pagelet(origIter.next());
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
	public MatrixIterable<Pagelet> pagelets(Direction direction) {
		switch (direction) {
			case COLUMNS:
				return matrix.vertically();
			case ROWS:
				return matrix.horizontally();
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
	public MatrixIterable<Pagelet> pagelets() {
		return pagelets(direction);
	}
	
	/**
	 * Makes all pages fit their cell.
	 */
	public void fitPages() {
		for (Pagelet p : getPagelets()) {
			p.fitPage();
		}
	}
	
	/**
	 * Scales up all pages by a fixed amount.
	 * @param scale magnification to be applied to all pages
	 */
	public void scalePages(double scale) {
		for (Pagelet p : getPagelets()) {
			p.scalePage(scale);
		}
	}
	
	/**
	 * Rotates all pages by the given angle.
	 * @param angle the angle of rotation in counter-clockwise direction
	 *        in radians to be applied to all pages
	 */
	public void rotatePages(double angle) {
		for (Pagelet p : getPagelets()) {
			p.rotatePage(angle);
		}
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
}
