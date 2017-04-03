package cz.slanyj.pdfriend.impose;

import java.util.List;

/**
 * Represents a source document from which pages will be selected for imposition.
 * @author Singon
 *
 * @param <T> Type of the source pages
 */
public interface SourceDocument<P extends SourcePage> {
	
	/**
	 * Returns a page of this source document with the given page number.
	 * Note that the pages are numbered from zero.
	 * @param pageNumber
	 * @return The page wrapped in SourcePage object.
	 */
	public abstract P getPage(int pageNumber);
	
	/**
	 * Returns a list of all Pages in this document in proper order.
	 * @return A new ArrayList with the Pages.
	 */
	public abstract List<P> getAllPages();
}
