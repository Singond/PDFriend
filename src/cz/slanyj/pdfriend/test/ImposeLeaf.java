package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import cz.slanyj.pdfriend.SourcePage;
import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.Orientation;

/**
 * A sample leaf.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeLeaf {

	public static void main(String[] args) {
		//Leaf leaf = new Leaf(612, 792);
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		PDDocument doc = new PDDocument();
		PDPage sheetFront = new PDPage();
		PDPage sheetBack = new PDPage();
		sheetFront.setMediaBox(new PDRectangle(1224, 792));
		sheetBack.setMediaBox(new PDRectangle(1224, 792));
		doc.addPage(sheetFront);
		doc.addPage(sheetBack);
		
		PDPageContentStream content;
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
			SourcePage one = new SourcePage(source, 0);
			SourcePage two = new SourcePage(source, 1);
			leaf.setContent(one, two);
			
			// Impose
			content = new PDPageContentStream(doc, sheetFront, PDPageContentStream.AppendMode.APPEND, true);
			LayerUtility lu = new LayerUtility(doc);
			leaf.imposeFront(content, lu);
			content.close();
			content = new PDPageContentStream(doc, sheetBack, PDPageContentStream.AppendMode.APPEND, true);
			float width = sheetFront.getMediaBox().getWidth();
			content.transform(Matrix.getTranslateInstance(width, 0));
			content.transform(Matrix.getScaleInstance(-1, 1));
			leaf.imposeBack(content, lu);
			content.close();
			
			// Save
			doc.save(new File("test/imposed-leaf.pdf"));
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
