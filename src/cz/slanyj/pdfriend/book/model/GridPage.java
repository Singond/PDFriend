package cz.slanyj.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import cz.slanyj.collections.ArrayMatrix;
import cz.slanyj.collections.Matrix;
import cz.slanyj.collections.MatrixIterable;
import cz.slanyj.collections.MatrixIterator;
import cz.slanyj.pdfriend.book.control.PageVisitor;

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
	 * Constructs a new GridPage with cells of the given dimensions.
	 * @param columns number of columns in the grid
	 * @param rows number of rows in the grid
	 * @param cellWidth width of a single cell
	 * @param cellHeight height of a single cell
	 */
	public GridPage(int columns, int rows,
	                double cellWidth, double cellHeight) {
		super(cellWidth*columns, cellHeight*rows);
		
		// Initialize the matrix with null values
		Matrix<Pagelet> cells = new ArrayMatrix<>(rows, columns);
		// Fill the matrix with pagelets
		MatrixIterator<Pagelet> iterator = cells.horizontallyAll().iterator();
		while (iterator.hasNext()) {
			iterator.next();
			int[] index = iterator.previousIndex();
			AffineTransform position = new AffineTransform();
			position.translate(cellWidth*index[1], cellHeight*(rows-index[0]-1));
			Pagelet pagelet = new Pagelet(cellWidth, cellHeight, position);
			cells.set(index[0], index[1], pagelet);
			super.addPagelet(pagelet);
		}
		// Set the matrix as the backing matrix
		matrix = cells;
	}

	/**
	 * Provides a way to iterate through the pages by rows.
	 * @return an iterable traversing the grid left to right, top to bottom
	 */
	public MatrixIterable<Pagelet> pageletsByRows() {
		return matrix.horizontally();
	}

	/**
	 * Provides a way to iterate through the pages by columns.
	 * @return an iterable traversing the grid top to bottom, left to right
	 */
	public MatrixIterable<Pagelet> pageletsByColumns() {
		return matrix.vertically();
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
	public void setPagesScale(double scale) {
		for (Pagelet p : getPagelets()) {
			p.scalePage(scale);
		}
	}
	
	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}
}
