package cz.slanyj.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import cz.slanyj.pdfriend.Bundle;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.document.VirtualPage;

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
		Log.verbose(Bundle.console, "sheet_renderingFront", this);
		/** Front side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		paper.setWidth(width);
		paper.setHeight(height);
		for (Leaf leaf : leaves) {
			/** The page to be imposed */
			Page page = leaf.getFrontPage();
			page.getContent().stream()
			                 .peek(cm -> cm.getTransform().preConcatenate(leaf.getFrontPosition()))
			                 .forEach(cm -> paper.addContent(cm.transformed()));
		}
		return paper.build();
	}
	
	/**
	 * Prints the back side of this Sheet onto a new virtual page.
	 * The page is not added to any document automatically.
	 * @return A new VirtualPage object with the front side of this Sheet.
	 */
	public VirtualPage renderBack() {
		Log.verbose(Bundle.console, "sheet_renderingFront", this);
		/** Front side of this sheet compiled into page */
		VirtualPage.Builder paper = new VirtualPage.Builder();
		paper.setWidth(width);
		paper.setHeight(height);
		final AffineTransform backside = AffineTransform.getTranslateInstance(width, 0);
		backside.concatenate(AffineTransform.getScaleInstance(-1,  1));
		for (Leaf leaf : leaves) {
			/** The page to be imposed */
			Page page = leaf.getBackPage();
			page.getContent().stream()
			                 .peek(cm -> cm.getTransform().preConcatenate(leaf.getBackPosition()))
			                 .peek(cm -> cm.getTransform().preConcatenate(backside))
			                 .forEach(cm -> paper.addContent(cm.transformed()));
		}
		return paper.build();
	}
	
	public Iterator<Leaf> leafIterator() {
		return leaves.iterator();
	}
	
	public Iterator<Page> pageIterator() {
		return new Iterator<Page>() {
			/** The current Leaf object */
			private Leaf currentLeaf;
			/** Iterator for Leaves */
			private final Iterator<Leaf> leafIterator = leafIterator();
			/** The last page returned was recto */
			private boolean isRecto;

			@Override
			public boolean hasNext() {
				return leafIterator.hasNext() || isRecto;
			}

			@Override
			public Page next() {
				if (isRecto) {
					isRecto = false;
					return currentLeaf.getVerso();
				} else {
					Leaf next = leafIterator.next();
					currentLeaf = next;
					isRecto = true;
					return next.getRecto();
				}
			}
		};
	}
	
	public Iterable<Leaf> leaves() {
		return new Iterable<Leaf>() {
			@Override
			public Iterator<Leaf> iterator() {
				return leafIterator();
			}
		};
	}
	
	public Iterable<Page> pages() {
		return new Iterable<Page>() {
			@Override
			public Iterator<Page> iterator() {
				return pageIterator();
			}
		};
	}
	
	@Override
	public String toString() {
		return "Sheet@" + hashCode();
	}
}
