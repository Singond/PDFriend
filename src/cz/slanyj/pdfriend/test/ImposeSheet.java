package cz.slanyj.pdfriend.test;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import cz.slanyj.pdfriend.SourcePage;
import cz.slanyj.pdfriend.book.Field;
import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
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
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		Leaf leaf2 = new Leaf(612, 792);
		leaf2.setAsFrontPosition(new Leaf.Position(918, 396, 0));
		leaf2.setOrientation(Orientation.VERSO_UP);
		leaf2.setFlipDirection(FlipDirection.AROUND_Y);
		
		Sheet sheet = new Sheet(1224, 792);
		
		AffineTransform at;
		at = new AffineTransform(-1, 0, 0, 1, 1224, 0);
		//at.rotate(Math.PI/80);
		Field field = new Field(sheet, at, Field.Orientation.NEGATIVE);
		field.addLeaf(leaf);
		field.addLeaf(leaf2);
		field.placeLeaves();
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
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
