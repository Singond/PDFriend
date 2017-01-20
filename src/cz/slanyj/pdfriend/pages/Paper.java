package cz.slanyj.pdfriend.pages;

import java.util.ArrayList;
import java.util.List;

/**
 * The large sheet of paper upon which Pages from multiple Sheets are laid out.
 * @author Sorondil
 *
 */
public class Paper {

	/**
	 * A list of all pages positioned on this paper, arranged in ascending
	 * order.
	 */
	private final List<Page> pages;
	
	public Paper(List<Page> pages) {
		this.pages = new ArrayList<>(pages);
	}
	
	/*public Paper(Paper paper) {
		this(paper.getPages());
	}*/
	
	public List<Page> getPages() {
		return new ArrayList<>(pages);
	}
}
