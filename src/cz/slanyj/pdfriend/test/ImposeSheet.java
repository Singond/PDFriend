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
import cz.slanyj.pdfriend.pages.Sheet;
import cz.slanyj.pdfriend.pages.Sheet.FlipDirection;
import cz.slanyj.pdfriend.pages.Sheet.Orientation;

public class ImposeSheet {

	public static void main(String[] args) {
		//Sheet sheet = new Sheet(792, 612);
		Sheet sheet = new Sheet(792, 612);
		sheet.setXPosition(306);
		sheet.setYPosition(396);
		sheet.setRotation(Math.PI/2);
		sheet.setOrientation(Orientation.RECTO_UP);
		sheet.setFlipDirection(FlipDirection.AROUND_Y);
		
		PDDocument doc = new PDDocument();
		PDPage paperFront = new PDPage();
		PDPage paperBack = new PDPage();
		paperFront.setMediaBox(new PDRectangle(1224, 792));
		paperBack.setMediaBox(new PDRectangle(1224, 792));
		doc.addPage(paperFront);
		doc.addPage(paperBack);
		
		PDPageContentStream content;
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/manual.pdf"));
			SourcePage one = new SourcePage(source, 0);
			SourcePage two = new SourcePage(source, 1);
			sheet.setContent(one, two);
			
			// Impose
			content = new PDPageContentStream(doc, paperFront, PDPageContentStream.AppendMode.APPEND, true);
			LayerUtility lu = new LayerUtility(doc);
			sheet.imposeFront(content, lu);
			content.close();
			content = new PDPageContentStream(doc, paperBack, PDPageContentStream.AppendMode.APPEND, true);
			float width = paperFront.getMediaBox().getWidth();
			content.transform(Matrix.getTranslateInstance(width, 0));
			content.transform(Matrix.getScaleInstance(-1, 1));
			sheet.imposeBack(content, lu);
			content.close();
			
			// Save
			doc.save(new File("test/imposed-sheet.pdf"));
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
