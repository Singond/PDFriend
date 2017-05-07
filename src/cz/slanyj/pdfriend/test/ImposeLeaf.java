package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.content.PDFPage;
import cz.slanyj.pdfriend.format.output.PDFRenderer;
import cz.slanyj.pdfriend.impose.formats.PDFSourcePage;

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
		
//		PDDocument doc = new PDDocument();
//		PDPage sheetFront = new PDPage();
//		PDPage sheetBack = new PDPage();
//		sheetFront.setMediaBox(new PDRectangle(1224, 792));
//		sheetBack.setMediaBox(new PDRectangle(1224, 792));
//		doc.addPage(sheetFront);
//		doc.addPage(sheetBack);
		
		PDPageContentStream content;
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
			VirtualPage one = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 0)));
			VirtualPage two = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 1)));
//			PDFPage one = new PDFPage(source, 0);
//			PDFPage two = new PDFPage(source, 1);
			leaf.getFrontPage().setSource(one);
			leaf.getBackPage().setSource(two);
			
			// Impose
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			doc.addPage(one);
			doc.addPage(two);
			
			// Output
			VirtualDocument document = doc.build();
			PDDocument out = new PDFRenderer().render(document);
			out.save(new File("test/imposed-leaf.pdf"));
			out.close();
			
//			content = new PDPageContentStream(doc, sheetFront, PDPageContentStream.AppendMode.APPEND, true);
//			LayerUtility lu = new LayerUtility(doc);
//			leaf.imposeFront(content, lu);
//			content.close();
//			content = new PDPageContentStream(doc, sheetBack, PDPageContentStream.AppendMode.APPEND, true);
//			float width = sheetFront.getMediaBox().getWidth();
//			content.transform(Matrix.getTranslateInstance(width, 0));
//			content.transform(Matrix.getScaleInstance(-1, 1));
//			leaf.imposeBack(content, lu);
//			content.close();
			
			// Save
//			doc.save(new File("test/imposed-leaf.pdf"));
//			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
