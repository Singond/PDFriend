package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import cz.slanyj.pdfriend.SourcePage;
import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.book.Sheet;
import cz.slanyj.pdfriend.book.Signature;

/**
 * A sample signature of two sheets.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeSignature {

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
		
		Leaf leaf3 = new Leaf(612, 792);
		leaf3.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf3.setOrientation(Orientation.RECTO_UP);
		leaf3.setFlipDirection(FlipDirection.AROUND_Y);
		
		Leaf leaf4 = new Leaf(612, 792);
		leaf4.setAsFrontPosition(new Leaf.Position(918, 396, 0));
		leaf4.setOrientation(Orientation.VERSO_UP);
		leaf4.setFlipDirection(FlipDirection.AROUND_Y);
		
		
		Sheet sheet2 = new Sheet(1224, 792);
		sheet2.addLeaf(leaf3);
		sheet2.addLeaf(leaf4);
		
		Signature signature = new Signature();
		signature.add(sheet);
		signature.add(sheet2);
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
			SourcePage one = new SourcePage(source, 0);
			SourcePage two = new SourcePage(source, 1);
			SourcePage three = new SourcePage(source, 2);
			SourcePage four = new SourcePage(source, 3);
			SourcePage five = new SourcePage(source, 4);
			SourcePage six = new SourcePage(source, 5);
			SourcePage seven = new SourcePage(source, 6);
			SourcePage eight = new SourcePage(source, 7);
			leaf.setContent(one, two);
			leaf2.setContent(three, four);
			leaf3.setContent(five, six);
			leaf4.setContent(seven, eight);
			
			PDDocument doc = new PDDocument();
			signature.renderAllSheets(doc);
			
			// Save
			doc.save(new File("test/imposed-signature.pdf"));
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
