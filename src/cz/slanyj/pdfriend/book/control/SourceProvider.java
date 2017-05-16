package cz.slanyj.pdfriend.book.control;

import cz.slanyj.pdfriend.book.model.Page;

/**
 * Puts VirtualPages into Page objects as their source.
 * Each SourceProvider is expected to visit the target Pages sequentially,
 * but the order in which source VirtualPages are inserted into them
 * (ie. which VirtualPage will be sourced into which Page)
 * is implementation-specific and will differ for each subclass.
 * 
 * @author Singon
 *
 */
public interface SourceProvider {

	/**
	 * For each Page from the given iterable container, set one or more
	 * VirtualPages as its source.
	 * @param pages
	 */
	public void setSourceTo(Iterable<Page> pages);
	
	/**
	 * Set one or more VirtualPages as the source of the given Page. 
	 * @param page
	 */
	public void setSourceTo(Page page);
}
