package cz.slanyj.pdfriend.book;

import java.io.IOException;
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
	/** The order-keeping object of Leaves in this Signature */
	private LeafOrder leafOrder;

	
	public Signature() {
		this.sheets = SetUniqueList.setUniqueList(new LinkedList<Sheet>());
	}
	
	
	public boolean add(Sheet sheet) {
		return sheets.add(sheet);
	}
	
	/**
	 * Sets the Leaf order to be used when numbering Leaves in this Signature.
	 * @param lo The order as a LeafOrder object.
	 */
	public void setLeafOrder(LeafOrder lo) {
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
	
	/**
	 * Keeps track of Leaf ordering in this Signature.
	 * Ensures that every Leaf is represented only once in the order,
	 * their indices are unique within this Signature and the indices
	 * form a "continuous" sequence from zero up with increment = 1.
	 * @author Singon
	 */
	public class LeafOrder {
		/** The next available index */
		private int index = 0;
		/** A map keeping key-value pairs of Leaf-index. */
		private Map<Leaf, Integer> orderMap = new HashMap<>();
		
		/**
		 * Adds the given Leaf as the next element in order.
		 * @throw UnsupportedOperationException If the Leaf is already
		 * present in this LeafOrder object.
		 */
		public void addNextLeaf(Leaf leaf) {
			// Place the Leaf in the order only if it is not present yet
			boolean wasPresent = orderMap.putIfAbsent(leaf, index++)==null;
			if (wasPresent) {
				throw new UnsupportedOperationException
					("The Leaf has already been added to this LeafOrder");
			}
		}
		
		/** Returns the index of the given Leaf. */
		public int indexOf(Leaf leaf) {
			return orderMap.get(leaf);
		}
	}
}
