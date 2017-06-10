package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.book.model.FlipDirection;
import cz.slanyj.pdfriend.book.model.GridPage;
import cz.slanyj.pdfriend.book.model.Leaf;
import cz.slanyj.pdfriend.book.model.Sheet;
import cz.slanyj.pdfriend.book.model.Leaf.Orientation;
import cz.slanyj.pdfriend.book.model.MultiPage.Pagelet;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.content.PDFPage;
import cz.slanyj.pdfriend.format.process.PDFRenderer;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeGridPage {

	public static void main(String[] args) {
		GridPage page1 = new GridPage(2, 1, 612, 792);
		GridPage page2 = new GridPage(2, 1, 612, 792);
		
		Leaf leaf = new Leaf(page1, page2);
		leaf.setAsFrontPosition(new Leaf.Position(612, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		leaf.numberPagesFrom(1);
		
		Sheet sheet = new Sheet(1224, 792);
		sheet.addLeaf(leaf);
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
			List<VirtualPage> sourcePages = new ArrayList<>();
			sourcePages.add(new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 0))));
			sourcePages.add(new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 1))));
			sourcePages.add(new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 2))));
			sourcePages.add(new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 3))));
			sourcePages.add(new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 4))));
			sourcePages.add(new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 5))));
			
			Iterator<VirtualPage> srcPgIter = sourcePages.iterator();
			for (Pagelet p : page1.pageletsByRows()) {
				if (srcPgIter.hasNext())
					p.setSource(srcPgIter.next());
			}
			for (Pagelet p : page2.pageletsByRows()) {
				if (srcPgIter.hasNext())
					p.setSource(srcPgIter.next());
			}
			
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
		}
	}

}
