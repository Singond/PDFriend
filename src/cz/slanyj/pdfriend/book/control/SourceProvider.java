package cz.slanyj.pdfriend.book.control;

import java.util.List;

import cz.slanyj.pdfriend.book.model.Page;

/**
 * Given a list of Pages, puts VirtualPages as their source.
 * 
 * @author Singon
 *
 */
public interface SourceProvider {

	/**
	 * For each Page from the given list, set one or more VirtualPages
	 * as its source. 
	 * @param pages
	 */
	public void setSourceTo(List<Page> pages);
	
	/**
	 * Set one or more VirtualPages as the source of the given Page. 
	 * @param page
	 */
	public void setSourceTo(Page page);
}
