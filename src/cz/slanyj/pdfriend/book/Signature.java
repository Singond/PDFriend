package cz.slanyj.pdfriend.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;

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
	 * Assigns Page numbers to all Pages.
	 * Issues page numbers sequentially starting from the given number,
	 * while respecting the leaf order given as argument.
	 * @param number The number to number pages from. The recto of the first
	 * Leaf in current order will receive this page number.
	 * @param order The Leaf order to be used.
	 * @return The next available page number, ie. the number of last page
	 * plus one.
	 * @throw {@code NullPointerException} when Leaf order is null.
	 */
	public int numberPagesFrom(int number, Order<Leaf> order) {
		if (order == null) {
			throw new NullPointerException("The leaf order cannot be null");
		}
		int leaves = sheets.stream()
			.flatMap(s -> s.getLeaves().stream())
			.sorted((x,y) -> {
				if (order.hasElement(x) && order.hasElement(y)) {
					return order.indexOf(x) - order.indexOf(y);
				} else {
					return 1;
				}
			})
			.mapToInt(new ToIntFunction<Leaf>() {
				private int leaves = number;
				@Override
				public int applyAsInt(Leaf l) {
					l.numberPagesFrom(leaves);
					leaves += 2;
					return leaves;
				}
			})
			.reduce(0, Integer::max);
		return (int) (number + 2*leaves);
	}
	/**
	 * Assigns Page numbers to all Pages.
	 * Issues page numbers sequentially starting from the given number,
	 * while respecting the leaf order given by current value of the
	 * {@code leafOrder} field.
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
