package com.github.singond.pdfriend.book;

/**
 * Puts VirtualPages into Page objects as their source.
 * Each SourceProvider is expected to visit the target Pages sequentially,
 * but the order in which source VirtualPages are inserted into them
 * (ie. which VirtualPage will be sourced into which Page)
 * is implementation-specific and will differ for each subclass.
 * 
 * @author Singon
 * @param <P> the subtype of Page handled by this SourceProvider
 */
public interface SourceProvider<P extends Page> {

	/**
	 * Checks if this {@code SourceProvider} has more pages to provide.
	 * @return true if the next call to {@code setSourceTo(Page)} will not
	 *         fail due to the source provider not having any pages to provide
	 */
	public boolean hasNextPage();
	
	/**
	 * For each Page from the given iterable container, set one or more
	 * VirtualPages as its source.
	 * @param pages
	 */
	public void setSourceTo(Iterable<P> pages);
	
	/**
	 * Set one or more VirtualPages as the source of the given Page.
	 * @param page
	 */
	public void setSourceTo(P page);
}
