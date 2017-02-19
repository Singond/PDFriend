package cz.slanyj.pdfriend.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * A signature of a document, made by folding one or more Sheets.
 * @author Singon
 *
 */
public class Signature {

	/** The sheets comprising this Signature */
	private final List<Sheet> sheets;
	/**
	 * An object keeping the order of Leaves in this Signature.
	 * This represents the order the Leaves will be numbered in.
	 */
	private Order<Leaf> leafOrder;
	
	
	public Signature() {
		this.sheets = SetUniqueList.setUniqueList(new LinkedList<Sheet>());
	}
	
	
	/**
	 * Provides access to the Sheets in this Signature.
	 * @return A shallow copy of the internal list of Sheets.
	 */
	public List<Sheet> getSheets() {
		return new ArrayList<>(sheets);
	}
	
	/**
	 * Adds a Sheet to this Signature (optional operation).
	 * @param sheet The Sheet to be added.
	 * @return False if the Sheet is already present in this Signature.
	 */
	public boolean add(Sheet sheet) {
		return sheets.add(sheet);
	}
	
	/**
	 * Sets the Leaf order to be used when numbering Leaves in this Signature.
	 * @param lo The order as a LeafOrder object.
	 */
	public void setLeafOrder(Order<Leaf> lo) {
		this.leafOrder = lo;
	}
	
	/**
	 * Renders the given sheet into the given document as two new pages
	 * (recto first, verso second).
	 * @param i
	 * @param doc
	 */
	private void renderSheet(Sheet sheet, PDDocument doc) throws IOException {
		PDPage front = sheet.renderFront(doc);
		doc.addPage(front);
		PDPage back = sheet.renderBack(doc);
		doc.addPage(back);
	}
	
	/**
	 * Renders all Sheets in this signature into the given document,
	 * each as two new pages (recto first, verso second).
	 */
	public void renderAllSheets(PDDocument doc) throws IOException {
		for (Sheet s : sheets) {
			renderSheet(s, doc);
		}
	}
}
