package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.book.control.SequentialSourceProvider;
import cz.slanyj.pdfriend.book.control.SourceProvider;
import cz.slanyj.pdfriend.book.model.FlipDirection;
import cz.slanyj.pdfriend.book.model.GridPage;
import cz.slanyj.pdfriend.book.model.GridPage.Direction;
import cz.slanyj.pdfriend.book.model.GridPage.GridOrientation;
import cz.slanyj.pdfriend.book.model.Leaf;
import cz.slanyj.pdfriend.book.model.Sheet;
import cz.slanyj.pdfriend.book.model.Leaf.Orientation;
import cz.slanyj.pdfriend.document.ImportException;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.process.PDFImporter;
import cz.slanyj.pdfriend.format.process.PDFRenderer;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeGridPage {

	public static void main(String[] args) {
		GridOrientation go = GridOrientation.ROTATED_RIGHT;
		GridPage page1 = new GridPage(2, 2, 306, 396, go);
		GridPage page2 = new GridPage(2, 2, 306, 396, go);
//		page1.scalePages(0.55);
		page1.fitPages();
//		page1.rotatePages(Math.PI/2);
		page2.fitPages();
		page2.setDirection(Direction.COLUMNS);
		
		Leaf leaf = new Leaf(page1, page2);
		leaf.setAsFrontPosition(new Leaf.Position(396, 306, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		leaf.numberPagesFrom(1);
		
//		Sheet sheet = new Sheet(612, 792);
		Sheet sheet = new Sheet(792, 612);
		sheet.addLeaf(leaf);
		
		try {
			// Get content
			File srcFile = new File("test/lorem-letter.pdf");
			VirtualDocument source = new PDFImporter(srcFile).importDocument();
			SourceProvider sp = new SequentialSourceProvider(source);
			sp.setSourceTo(page1);
			sp.setSourceTo(page2);
			
			// Build document model
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			VirtualPage sheetFront = sheet.renderFront();
			VirtualPage sheetBack = sheet.renderBack();
			doc.addPage(sheetFront);
			doc.addPage(sheetBack);
			
			// Render and save
			PDDocument output = new PDFRenderer().render(doc.build());
			output.save(new File("test/imposed-gridpage.pdf"));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		}
	}

}
