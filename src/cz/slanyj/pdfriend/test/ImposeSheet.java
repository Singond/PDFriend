package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.content.PDFPage;
import cz.slanyj.pdfriend.format.process.PDFRenderer;
import cz.slanyj.pdfriend.impose.formats.PDFSourceDocument;
import cz.slanyj.pdfriend.impose.formats.PDFSourcePage;
import cz.slanyj.pdfriend.book.Sheet;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeSheet {

	public static void main(String[] args) {
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		Leaf leaf2 = new Leaf(612, 792);
		leaf2.setAsFrontPosition(new Leaf.Position(918, 396, 0));
		leaf2.setOrientation(Orientation.VERSO_UP);
		leaf2.setFlipDirection(FlipDirection.AROUND_Y);
		
		Sheet sheet = new Sheet(1224, 792);
		sheet.addLeaf(leaf);
		sheet.addLeaf(leaf2);
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
			VirtualPage one = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 0)));
			VirtualPage two = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 1)));
			VirtualPage three = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 2)));
			VirtualPage four = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 3)));
			leaf.numberPagesFrom(1);
			leaf2.numberPagesFrom(3);
			List<VirtualPage> pgs = new ArrayList<>();
			pgs.add(one);
			pgs.add(two);
			pgs.add(three);
			pgs.add(four);
			leaf.setSourceFrom(pgs);
			leaf2.setSourceFrom(pgs);
			
			// Build document model
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			VirtualPage sheetFront = sheet.renderFront();
			VirtualPage sheetBack = sheet.renderBack();
			doc.addPage(sheetFront);
			doc.addPage(sheetBack);
			
			// Render and save
			PDDocument output = new PDFRenderer().render(doc.build());
			output.save(new File("test/imposed-sheet.pdf"));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
