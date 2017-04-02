package cz.slanyj.pdfriend.format;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class SourceDocument {

	/** The source PDDocument. */
	private final PDDocument sourceDoc;
	
	
	/**
	 * Constructs a new SourceDocument with the given PDDocument as source.
	 * @param source The source PDDocument.
	 */
	public SourceDocument(PDDocument source) {
		this.sourceDoc = source;
	}
	
	
	/**
	 * Returns a page of this source document with the given page number.
	 * Note that the pages are numbered from zero.
	 * @param pageNumber
	 * @return The page wrapped in SourcePage object.
	 */
	public SourcePage getPage(int pageNumber) {
		return new SourcePage(sourceDoc, pageNumber);
	}
	
	/**
	 * Returns a list of all Pages in this document in proper order.
	 * @return A new ArrayList with the Pages.
	 */
	public List<SourcePage> getAllPages() {
		List<SourcePage> list = new ArrayList<>();
		Iterator<PDPage> pages = sourceDoc.getPages().iterator();
		while (pages.hasNext()) {
			SourcePage sp = new SourcePage(sourceDoc, pages.next());
			list.add(sp);
		}
		return list;
	}
}
