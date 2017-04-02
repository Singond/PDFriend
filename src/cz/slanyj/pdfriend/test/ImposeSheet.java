package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.format.SourceDocument;
import cz.slanyj.pdfriend.format.SourcePage;
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
			SourceDocument sourceDoc = new SourceDocument(source);
			leaf.numberPagesFrom(1);
			leaf2.numberPagesFrom(3);
			List<SourcePage> sps = sourceDoc.getAllPages();
			leaf.setContent(sps);
			leaf2.setContent(sps);
			
			PDDocument doc = new PDDocument();
			PDPage sheetFront = sheet.renderFront(doc);
			PDPage sheetBack = sheet.renderBack(doc);
			doc.addPage(sheetFront);
			doc.addPage(sheetBack);
			
			// Save
			doc.save(new File("test/imposed-sheet.pdf"));
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
