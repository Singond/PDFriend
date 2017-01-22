package cz.slanyj.pdfriend.book;

import java.util.ArrayList;
import java.util.List;

/**
 * The large sheet of paper upon which Pages from multiple Leaves are laid out.
 * @author Sorondil
 *
 */
public class Sheet {

	/**
	 * A list of all pages positioned on this sheet, arranged in ascending
	 * order.
	 */
	private final List<Leaf> leaves;
	
	public Sheet(List<Leaf> leaves) {
		this.leaves = new ArrayList<>(leaves);
	}
	
	public List<Leaf> getLeaves() {
		return new ArrayList<>(leaves);
	}
}
