package com.github.singond.pdfriend.book;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.SetUniqueList;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;

/**
 * A signature of a document, made by folding one or more Sheets.
 * @author Singon
 *
 */
public class Signature implements BookElement {

	/** The sheets comprising this Signature */
	private final List<Sheet> sheets;
	/**
	 * An object keeping the order of Leaves in this Signature.
	 * This represents the order the Leaves will be numbered in.
	 */
	private Order<Leaf> leafOrder;
	
	private static ExtendedLogger logger = Log.logger(Signature.class);
	
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
		logger.verbose("signature_numbering", this, number);
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
	 * Iterates through the leaves in the currently set order.
	 * @return A new Iterator object starting at the first Leaf.
	 */
	public Iterator<Leaf> leafIterator() {
		Order<Leaf> order;
		if (leafOrder == null) {
			throw new IllegalStateException("No leaf order has been set for "+this);
		} else {
			order = leafOrder;
		}
		
		List<Leaf> sheetList = sheets.stream()
				.flatMap(s -> s.getLeaves().stream())
				// Sort by order and put unordered Leaves to the end
				.sorted((x, y) -> {
					if (order.hasElement(x) && order.hasElement(y)) {
						return order.indexOf(x) - order.indexOf(y);
					} else {
						return 1;
					}
				})
				.collect(Collectors.toList());
		return sheetList.iterator();
	}
	
	/**
	 * Wraps this object to iterate through all Leaves in the current order.
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
	
	/**
	 * Renders the given sheet into the given document as two new pages
	 * (recto first, verso second).
	 * @param i
	 * @param doc
	 */
	private void renderSheet(Sheet sheet, VirtualDocument.Builder doc,
	                         Volume.RenderingSettings settings) {
		logger.verbose("signature_renderingSheet", sheet);
		VirtualPage front = sheet.renderFront();
		doc.addPage(front);
		VirtualPage back = sheet.renderBack(settings.getFlip());
		doc.addPage(back);
	}
	
	/**
	 * Renders all Sheets in this signature into the given document,
	 * each as two new pages (recto first, verso second).
	 */
	public void renderAllSheets(VirtualDocument.Builder doc,
	                            Volume.RenderingSettings settings) {
		logger.verbose("signature_rendering", this);
		for (Sheet s : sheets) {
			renderSheet(s, doc, settings);
		}
	}
	
	@Override
	public String toString() {
		return "Signature@" + hashCode();
	}
}
