package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import cz.slanyj.pdfriend.SourcePage;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.book.Sheet;
import cz.slanyj.pdfriend.book.Signature;
import cz.slanyj.pdfriend.book.Volume;

/**
 * A sample volume of two signatures.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class PrintVolume {

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
		
		Leaf leaf3 = new Leaf(792, 612);
		leaf3.setXPosition(306);
		leaf3.setYPosition(396);
		leaf3.setRotation(Math.PI/2);
		leaf3.setOrientation(Orientation.RECTO_UP);
		leaf3.setFlipDirection(FlipDirection.AROUND_Y);
		
		Leaf leaf4 = new Leaf(792, 612);
		leaf4.setXPosition(918);
		leaf4.setYPosition(396);
		leaf4.setRotation(Math.PI/2);
		leaf4.setOrientation(Orientation.VERSO_UP);
		leaf4.setFlipDirection(FlipDirection.AROUND_Y);
		
		Sheet sheet2 = new Sheet(1224, 792);
		sheet2.addLeaf(leaf3);
		sheet2.addLeaf(leaf4);
		
		Signature signature = new Signature();
		signature.add(sheet);
		signature.add(sheet2);
		
		Volume volume = new Volume();
		volume.add(signature);
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/document.pdf"));
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
			
			volume.renderAndSaveDocument(new File("test/printed-volume.pdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
