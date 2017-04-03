package cz.slanyj.pdfriend.impose.formats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.impose.SourceDocument;

public class PDFSourceDocument implements SourceDocument<PDFSourcePage> {

	/** The source PDDocument. */
	private final PDDocument sourceDoc;
	
	
	/**
	 * Constructs a new SourceDocument with the given PDDocument as source.
	 * @param source The source PDDocument.
	 */
	public PDFSourceDocument(PDDocument source) {
		this.sourceDoc = source;
	}
	
	
	/**
	 * Returns a page of this source document with the given page number.
	 * Note that the pages are numbered from zero.
	 * @param pageNumber
	 * @return The page wrapped in SourcePage object.
	 */
	@Override
	public PDFSourcePage getPage(int pageNumber) {
		return new PDFSourcePage(sourceDoc, pageNumber);
	}
	
	/**
	 * Returns a list of all Pages in this document in proper order.
	 * @return A new ArrayList with the Pages.
	 */
	@Override
	public List<PDFSourcePage> getAllPages() {
		List<PDFSourcePage> list = new ArrayList<>();
		Iterator<PDPage> pages = sourceDoc.getPages().iterator();
		while (pages.hasNext()) {
			PDFSourcePage sp = new PDFSourcePage(sourceDoc, pages.next());
			list.add(sp);
		}
		return list;
	}
}
