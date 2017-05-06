package cz.slanyj.pdfriend.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.Bundle;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;

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
	 * Assigns Page numbers to all Pages.
	 * Issues page numbers sequentially starting from the given number,
	 * while respecting the leaf order given as argument. Pages with order
	 * specified in the argument will be placed in this order and the
	 * remaining, unordered, pages (if any) will be placed to the end in
	 * the order they are encountered.
	 * @param number The number to number pages from. The recto of the first
	 * Leaf in current order will receive this page number.
	 * @param order The Leaf order to be used. To ensure correct results,
	 * it should contain all Leaves in this Signature.
	 * @return The next available page number, ie. the number of last page
	 * plus one.
	 * @throw {@code NullPointerException} when Leaf order is null.
	 */
	public int numberPagesFrom(int number, Order<Leaf> order) {
		Log.verbose(Bundle.console, "signature_numbering", this, number);
		if (order == null) {
			throw new NullPointerException("The leaf order cannot be null");
		}
		int nextPage = sheets.stream()
			.flatMap(s -> s.getLeaves().stream())
			// Sort by order and put unordered Leaves to the end
			.sorted((x,y) -> {
				if (order.hasElement(x) && order.hasElement(y)) {
					return order.indexOf(x) - order.indexOf(y);
				} else {
					return 1;
				}
			})
			.mapToInt(new ToIntFunction<Leaf>() {
				// Apply the numbers sequentially
				private int page = number;
				@Override
				public int applyAsInt(Leaf l) {
					l.numberPagesFrom(page);
					page += 2;
					return page;
				}
			})
			// Get the next available page number (used as return value)
			.reduce(0, Integer::max);
		return (int) (nextPage);
	}
	/**
	 * Assigns Page numbers to all Pages.
	 * Issues page numbers sequentially starting from the given number,
	 * while respecting the leaf order given by current value of the
	 * {@code leafOrder} field. Pages with order specified in the argument
	 * will be placed in this order and the remaining, unordered, pages
	 * (if any) will be placed to the end in the order they are encountered.
	 * @param number The number to number pages from. The recto of the first
	 * Leaf in current order will receive this page number.
	 * @return The next available page number, ie. the number of last page
	 * plus one.
	 * @throw {@code IllegalStateException} when Leaf order has not been set.
	 */
	public int numberPagesFrom(int number) {
		if (leafOrder == null) {
			throw new IllegalStateException("No leaf order has been set for "+this);
		} else {
			return numberPagesFrom(number, leafOrder);
		}
	}
	
	/**
	 * Renders the given sheet into the given document as two new pages
	 * (recto first, verso second).
	 * @param i
	 * @param doc
	 */
	private void renderSheet(Sheet sheet, VirtualDocument.Builder doc) throws IOException {
		Log.verbose(Bundle.console, "signature_renderingSheet", sheet);
		VirtualPage front = sheet.renderFront();
		doc.addPage(front);
		VirtualPage back = sheet.renderBack();
		doc.addPage(back);
	}
	
	/**
	 * Renders all Sheets in this signature into the given document,
	 * each as two new pages (recto first, verso second).
	 */
	public void renderAllSheets(VirtualDocument.Builder doc) throws IOException {
		Log.verbose(Bundle.console, "signature_rendering", this);
		for (Sheet s : sheets) {
			renderSheet(s, doc);
		}
	}
	
	@Override
	public String toString() {
		return "Signature@" + hashCode();
	}
}
