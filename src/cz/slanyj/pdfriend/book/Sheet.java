package cz.slanyj.pdfriend.book;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

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
	 * Returns the front side of this Sheet printed onto a new PDF page.
	 * The page is not added to its parent document automatically.
	 * @param doc The parent document of the future page.
	 * @return A new PDPage object.
	 * @throws IOException
	 */
//	public PDPage renderFront(PDDocument doc) throws IOException {
//		Log.verbose(Bundle.console, "sheet_renderingFront", this);
//		PDPage leaf = new PDPage();
//		leaf.setMediaBox(new PDRectangle((float) width, (float) height));
//		PDPageContentStream content = new PDPageContentStream(doc, leaf);
//		LayerUtility lu = new LayerUtility(doc);
//		for (Leaf l : leaves) {
//			l.imposeFront(content, lu);
//		}
//		content.close();
//		return leaf;
//	}
	
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
	 * Returns the back side of this Sheet printed onto a new PDF page.
	 * The page is not added to its parent document automatically.
	 * @param doc The parent document of the future page.
	 * @return A new PDPage object.
	 * @throws IOException
	 */
//	public PDPage renderBack(PDDocument doc) throws IOException {
//		Log.verbose(Bundle.console, "sheet_renderingBack", this);
//		PDPage leaf = new PDPage();
//		leaf.setMediaBox(new PDRectangle((float) width, (float) height));
//		PDPageContentStream content = new PDPageContentStream(doc, leaf);
//		LayerUtility lu = new LayerUtility(doc);
//		// Mirror the whole layout to produce the back side
//		content.transform(Matrix.getTranslateInstance((float) width, 0));
//		content.transform(Matrix.getScaleInstance(-1, 1));
//		for (Leaf l : leaves) {
//			l.imposeBack(content, lu);
//		}
//		content.close();
//		return leaf;
//	}
	
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
	
	@Override
	public String toString() {
		return "Sheet@" + hashCode();
	}
}
