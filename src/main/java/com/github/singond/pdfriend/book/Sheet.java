package com.github.singond.pdfriend.book;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.TransformableContents;
import com.github.singond.pdfriend.document.VirtualPage;

/**
 * A single sheet of paper upon which Pages from multiple Leaves are laid out.
 * @author Singon
 *
 */
public class Sheet implements BookElement {

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
		if (logger.isDebugEnabled())
			logger.debug("sheet_renderingFront", this);
		/** Front side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		paper.setWidth(width);
		paper.setHeight(height);
		for (Leaf leaf : leaves) {
			/** The page to be imposed */
			Page page = leaf.getFrontPage();
			if (!page.isBlank()) {
				TransformableContents contents = page.getContents();
				contents.transform(leaf.getFrontPosition());
				paper.addContent(contents);
			}
		}
		return paper.build();
	}

	/**
	 * Prints the back side of this Sheet onto a new virtual page
	 * with the given relationship between the front and back side
	 * of the medium.
	 *
	 * TODO Enable both ways of flipping the back side. Currently,
	 * the backside is always printed as if it was flipped about the vertical
	 * axis, while sometimes it may be desirable to flip it horizontally.
	 * The only difference this will make is the rotation of the backside:
	 * The backside flipped horizontally will be rotated by 180 degrees
	 * with respect to a backside flipped vertically.
	 * This concerns only the process of rendering the document into
	 * a virtual document; the internal representation of back side content
	 * is independent of rendering.
	 * NOTE This implementation covers only documents composed of Signatures,
	 * not single Pages.
	 *
	 * @param flip the orientation of the back side with respect to front
	 * @return A new VirtualPage object with the front side of this Sheet.
	 */
	public VirtualPage renderBack(FlipDirection flip) {
		if (logger.isDebugEnabled())
			logger.debug("sheet_renderingBack", this);
		/** Back side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		paper.setWidth(width);
		paper.setHeight(height);
		final AffineTransform backside = backTransform(width, height, flip);
//		final AffineTransform backside = AffineTransform.getTranslateInstance(width, 0);
//		backside.concatenate(AffineTransform.getScaleInstance(-1,  1));
		for (Leaf leaf : leaves) {
			/** The page to be imposed */
			Page page = leaf.getBackPage();
			if (!page.isBlank()) {
				TransformableContents contents = page.getContents();
				contents.transform(leaf.getBackPosition());
				contents.transform(backside);
				paper.addContent(contents);
			}
		}
		return paper.build();
	}

	/**
	 * Prints the back side of this Sheet onto a new virtual page,
	 * assuming the backside is flipped around vertical axis with respect
	 * to the front side.
	 *
	 * @return A new VirtualPage object with the front side of this Sheet.
	 */
	public VirtualPage renderBack() {
		return renderBack(FlipDirection.AROUND_Y);
	}

	/**
	 * Calculates the transformation of the back side given the sheet
	 * dimensions and flip directions.
	 */
	private AffineTransform backTransform(double width, double height,
	                                      FlipDirection flip) {
		final AffineTransform backside;
		switch (flip) {
			case AROUND_X:
				backside = AffineTransform.getTranslateInstance(0, height);
				break;
			case AROUND_Y:
				backside = AffineTransform.getTranslateInstance(width, 0);
				break;
			default:
				throw new AssertionError(flip);
		}
		backside.concatenate(flip.getBackOrientation());
		return backside;
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
	 *
	 * @see #leafIterator
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
	 *
	 * @see #leafIterator
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
