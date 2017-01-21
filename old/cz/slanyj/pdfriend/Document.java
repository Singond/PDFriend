package cz.slanyj.pdfriend;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectForm;
import org.apache.pdfbox.util.LayerUtility;

import cz.slanyj.swing.Canvas;

// TODO Method to get the rectangle occupied by a given sheet. This will be used to fit the page into view in GUI.

/**
 * A wrapper for a PDDocument.
 * @author Sorondil
 *
 */
public class Document extends Project<Sheet.Real> /*implements Canvas.CanvasPainter*/ {
	
	/** The backing PDDocument represented by this wrapper. */
	private final PDDocument source;
	
	/** Constructs a Document wrapping the given PDDocument. */
	public Document(PDDocument doc) {
		this.source = doc;
	}
	/** Constructs a Document wrapping the PDDocument specified by the path. */
	public Document(String filepath) throws IOException {
		this(PDDocument.load(filepath));
	}
	/** Constructs a Document with a blank backing PDDocument. */
	public Document() {
		this(new PDDocument());
	}
	
	/** Adds the sheets to this document’s PDF structure. */
	private void assemble() {
		
		LayerUtility lutil = new LayerUtility(this);
		
		for (Sheet.Real sheet : getSheets()) {
			
			// Add the empty sheet (not including pages)
			addPage(sheet);

			// Create a temporary document to hold all pages on sheet
//			PDDocument temp = new PDDocument();
			for (Page.Real p : sheet.pages()) {
				if (p == null) continue;
//				temp.addPage(p);
				try {
					
//					PDPageContentStream contents = new PDPageContentStream(this, p);
//					PDDocument source = PDDocument.load("Fotky.pdf");
					PDPage page = (PDPage)(source.getDocumentCatalog().getAllPages().get(5));
					PDDocument test = PDDocument.load(new File("1.pdf"));
//					PDDocument smaller = new PDDocument();
//					smaller.addPage((PDPage)test.getDocumentCatalog().getAllPages().get(1));
					test.importPage(page);
//					PDXObjectForm page = lutil.importPageAsForm(test, 0);
//					lutil.appendFormAsLayer(sheet, page, p.getPlacement(), p.getName());
//					lutil.appendFormAsLayer(new PDPage(), page, new AffineTransform(), "layer");
					
					// Testing area
//					PDDocument doc = new PDDocument();
//					PDPage page = new PDPage();
//					this.addPage(sheet);
//					PDDocument layer = PDDocument.load(new File("1.pdf"));
					PDPageContentStream contents = new PDPageContentStream(this, sheet);
//					PDPageContentStream contents2 = new PDPageContentStream(test, sheet);
					LayerUtility lu = new LayerUtility(this);
					PDXObjectForm obj = lu.importPageAsForm(source, 2);
					lu.appendFormAsLayer(sheet, obj, p.getPlacement(), "layer");
//					this.save("blank.pdf");
//					smaller.save("smaller.pdf");
//					this.close();
					test.save("test.pdf");
					test.close();
					source.close();
//					smaller.close();
					contents.close();
//					contents2.close();
					

				} catch (IOException e) {
					// Should not occur, all the documents are virtual (not read from disk).
					e.printStackTrace();
				} /*catch (NullPointerException e) {
					// Non-existent page (The array may contain nulls, because it is initialized to some default size>1.
				}*/ catch (COSVisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
//						test.close();
				}
			}
			
/*			sheet.pages().stream()
				.map(p -> (Page.Real)p)
				.forEach(pr->{
//					pr.pages.stream().forEach(p->temp.addPage(p));
					temp.addPage(pr);
					PDXObjectForm page = lutil.importPageAsForm(temp, s);
				});*/
		}
	}
	
	/** Creates the PDF file. 
	 * @throws IOException 
	 * @throws COSVisitorException */
	public void write(String path) throws COSVisitorException, IOException {
		assemble();
		save(path);
		this.close();
	}
	
}
