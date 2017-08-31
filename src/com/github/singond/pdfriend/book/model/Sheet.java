package com.github.singond.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.BookUtils;
import com.github.singond.pdfriend.document.Contents;
import com.github.singond.pdfriend.document.VirtualPage;

/**
 * A single sheet of paper upon which Pages from multiple Leaves are laid out.
 * @author Singon
 *
 */
public class Sheet {

	/** The sheet width */
	private final double width;
	/** The sheet height */
	private final double height;
	/**
	 * A list of all pages positioned on this sheet, arranged in ascending
	 * order.
	 */
	private final List<Leaf> leaves;
	
	private static ExtendedLogger logger = Log.logger(Sheet.class);
	
	public Sheet(double width, double height) {
		this.width = width;
		this.height = height;
		this.leaves = SetUniqueList.setUniqueList(new LinkedList<Leaf>());
	}
	
	
	/**
	 * Provides access to the Leaves in this Sheet.
	 * @return A shallow copy of the internal list of Leaves.
	 */
	public List<Leaf> getLeaves() {
		return new ArrayList<>(leaves);
	}
	
	/**
	 * Adds a Leaf to this Sheet.
	 */
	public void addLeaf(Leaf leaf) {
		leaves.add(leaf);
	}
	
	/**
	 * A clone method.
	 * Creates a new blank Sheet with the same dimensions as this one.
	 * The Leaves are not copied, the new Sheet is empty.
	 * @return A new Sheet instance with dimensions of this Sheet.
	 */
	public Sheet cloneBlank() {
		return new Sheet(this.width, this.height);
	}
	
	/**
	 * Prints the front side of this Sheet onto a new virtual page.
	 * The page is not added to any document automatically.
	 * @return A new VirtualPage object with the front side of this Sheet.
	 */
	public VirtualPage renderFront() {
		logger.verbose("sheet_renderingFront", this);
		/** Front side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		paper.setWidth(width);
		paper.setHeight(height);
		for (Leaf leaf : leaves) {
			/** The page to be imposed */
			Page page = leaf.getFrontPage();
			if (!page.isBlank()) {
				Contents contents = page.getContents();
				contents.transform(leaf.getFrontPosition());
				paper.addContent(contents);
			}
		}
		return paper.build();
	}
	
	/**
	 * Prints the back side of this Sheet onto a new virtual page.
	 * The page is not added to any document automatically.
	 * @return A new VirtualPage object with the front side of this Sheet.
	 */
	public VirtualPage renderBack() {
		logger.verbose("sheet_renderingFront", this);
		/** Front side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		paper.setWidth(width);
		paper.setHeight(height);
		final AffineTransform backside = AffineTransform.getTranslateInstance(width, 0);
		backside.concatenate(AffineTransform.getScaleInstance(-1,  1));
		for (Leaf leaf : leaves) {
			/** The page to be imposed */
			Page page = leaf.getBackPage();
			if (!page.isBlank()) {
				Contents contents = page.getContents();
				contents.transform(leaf.getBackPosition());
				contents.transform(backside);
				paper.addContent(contents);
			}
		}
		return paper.build();
	}
	
	/**
	 * Iterates through the leaves.
	 * @return A new Iterator object starting at the first Leaf.
	 */
	public Iterator<Leaf> leafIterator() {
		return leaves.iterator();
	}
	
	/**
	 * Wraps this object to iterate through the Leaves in their order.
	 * @see {@link #leafIterator}
	 * @return This object wrapped as an Iterable<Leaf>.
	 */
	public Iterable<Leaf> leaves() {
		return new Iterable<Leaf>() {
			@Override
			public Iterator<Leaf> iterator() {
				return leafIterator();
			}
		};
	}
	
	/**
	 * Wraps this object to iterate through the pages in the order of the
	 * Leaves and with the recto of each Leaf coming right before its verso.
	 * @see {@link #leafIterator}
	 * @return This object wrapped as an Iterable<Page>.
	 */
	public Iterable<Page> pages() {
		return new Iterable<Page>() {
			@Override
			public Iterator<Page> iterator() {
				return BookUtils.pageIterator(leafIterator());
			}
		};
	}
	
	@Override
	public String toString() {
		return "Sheet@" + hashCode();
	}
}
