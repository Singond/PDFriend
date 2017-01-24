package cz.slanyj.pdfriend.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

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
		this.leaves = new ArrayList<>();
	}
	
	/**
	 * Returns a copy of the list of Leaves
	 * @return
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
	 * Returns the front side of this Sheet printed onto a new PDF page.
	 * The page is not added to its parent document automatically.
	 * @param doc The parent document of the future page.
	 * @return A new PDPage object.
	 * @throws IOException
	 */
	public PDPage renderFront(PDDocument doc) throws IOException {
		PDPage leaf = new PDPage();
		leaf.setMediaBox(new PDRectangle((float) width, (float) height));
		PDPageContentStream content = new PDPageContentStream(doc, leaf);
		LayerUtility lu = new LayerUtility(doc);
		for (Leaf l : leaves) {
			l.imposeFront(content, lu);
		}
		content.close();
		return leaf;
	}
	/**
	 * Returns the back side of this Sheet printed onto a new PDF page.
	 * The page is not added to its parent document automatically.
	 * @param doc The parent document of the future page.
	 * @return A new PDPage object.
	 * @throws IOException
	 */
	public PDPage renderBack(PDDocument doc) throws IOException {
		PDPage leaf = new PDPage();
		leaf.setMediaBox(new PDRectangle((float) width, (float) height));
		PDPageContentStream content = new PDPageContentStream(doc, leaf);
		LayerUtility lu = new LayerUtility(doc);
		// Mirror the whole layout to produce the back side
		content.transform(Matrix.getTranslateInstance((float) width, 0));
		content.transform(Matrix.getScaleInstance(-1, 1));
		for (Leaf l : leaves) {
			l.imposeBack(content, lu);
		}
		content.close();
		return leaf;
	}
}
