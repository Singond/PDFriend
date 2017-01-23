package cz.slanyj.pdfriend.book;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * A signature of a document, made by folding one or more Sheets.
 * @author Sorondil
 *
 */
public class Signature {

	/** The sheets comprising this Signature */
	private final Set<Sheet> sheets;
	
	public Signature() {
		this.sheets = new LinkedHashSet<>();
	}
	
	public void add(Sheet sheet) {
		sheets.add(sheet);
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
