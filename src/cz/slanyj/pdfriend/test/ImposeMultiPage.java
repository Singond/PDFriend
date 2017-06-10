package cz.slanyj.pdfriend.test;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.book.model.FlipDirection;
import cz.slanyj.pdfriend.book.model.Leaf;
import cz.slanyj.pdfriend.book.model.Sheet;
import cz.slanyj.pdfriend.book.model.SinglePage;
import cz.slanyj.pdfriend.book.model.Leaf.Orientation;
import cz.slanyj.pdfriend.book.model.MultiPage;
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
public class ImposeMultiPage {
	
	private static class MyMultiPage extends MultiPage {
		
		MyMultiPage(double w, double h) {
			super(w, h);
		}
		
		public void addPage(VirtualPage page, AffineTransform position) {
			Pagelet p = new Pagelet(page.getHeight(), page.getWidth(), position);
			p.setSource(page);
			super.addPagelet(p);
		}
	}

	public static void main(String[] args) {
		MyMultiPage page1 = new MyMultiPage(612, 792){};
		MyMultiPage page2 = new MyMultiPage(612, 792){};
		AffineTransform pagelet1Position = new AffineTransform();
//		pagelet1Position.translate(10, 10);
		pagelet1Position.scale(0.5, 0.5);
		pagelet1Position.rotate(0.1);
		AffineTransform pagelet2Position = new AffineTransform();
		pagelet2Position.scale(0.5, 0.5);
		pagelet2Position.translate(612, 0);
		
		Leaf leaf = new Leaf(page1, page2);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		leaf.numberPagesFrom(1);
		
		Leaf leaf2 = new Leaf(612, 792);
		leaf2.setAsFrontPosition(new Leaf.Position(918, 396, 0));
		leaf2.setOrientation(Orientation.VERSO_UP);
		leaf2.setFlipDirection(FlipDirection.AROUND_Y);
		leaf2.numberPagesFrom(3);
		
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
			VirtualPage five = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 4)));
			VirtualPage six = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 5)));
			
			((MyMultiPage) leaf.getRecto()).addPage(one, pagelet1Position);
			((MyMultiPage) leaf.getRecto()).addPage(two, pagelet2Position);
			((MyMultiPage) leaf.getVerso()).addPage(three, pagelet1Position);
			((MyMultiPage) leaf.getVerso()).addPage(four, pagelet2Position);
			((SinglePage) leaf2.getRecto()).setSource(five);
			((SinglePage) leaf2.getVerso()).setSource(six);
			
			// Build document model
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			VirtualPage sheetFront = sheet.renderFront();
			VirtualPage sheetBack = sheet.renderBack();
			doc.addPage(sheetFront);
			doc.addPage(sheetBack);
			
			// Render and save
			PDDocument output = new PDFRenderer().render(doc.build());
			output.save(new File("test/imposed-multipage.pdf"));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
