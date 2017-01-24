package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import cz.slanyj.pdfriend.SourcePage;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.book.Sheet;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeSheet {

	public static void main(String[] args) {
		Leaf leaf = new Leaf(792, 612);
		leaf.setXPosition(306);
		leaf.setYPosition(396);
		leaf.setRotation(Math.PI/2);
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		Leaf leaf2 = new Leaf(792, 612);
		leaf2.setXPosition(918);
		leaf2.setYPosition(396);
		leaf2.setRotation(Math.PI/2);
		leaf2.setOrientation(Orientation.VERSO_UP);
		leaf2.setFlipDirection(FlipDirection.AROUND_Y);
		
		Sheet sheet = new Sheet(1224, 792);
		sheet.addLeaf(leaf);
		sheet.addLeaf(leaf2);
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/document.pdf"));
			SourcePage one = new SourcePage(source, 0);
			SourcePage two = new SourcePage(source, 1);
			SourcePage three = new SourcePage(source, 2);
			SourcePage four = new SourcePage(source, 3);
			leaf.setContent(one, two);
			leaf2.setContent(three, four);
			
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
