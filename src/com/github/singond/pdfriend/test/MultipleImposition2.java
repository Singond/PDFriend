package com.github.singond.pdfriend.test;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import com.github.singond.pdfriend.book.model.MultiPage;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.content.PDFPage;
import com.github.singond.pdfriend.format.process.PDFRenderer;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class MultipleImposition2 {
	
	private static class MyMultiPage extends MultiPage {
		
		MyMultiPage(double w, double h) {
			super(w, h);
		}
		
		public void addPage(VirtualPage page, AffineTransform position) {
			Pagelet p = new Pagelet(page.getWidth(), page.getHeight(), position);
			p.setSource(page);
			super.addPagelet(p);
		}
	}

	public static void main(String[] args) throws InvalidPasswordException, IOException {
		PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
		VirtualPage one = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 0)));

		MyMultiPage page1 = new MyMultiPage(612, 792);
		AffineTransform pagelet1Position = new AffineTransform();
		pagelet1Position.translate(50, 0);
//		pagelet1Position.scale(0.5, 0.5);
//		pagelet1Position.rotate(0.02);
		page1.addPage(one, pagelet1Position);
		VirtualPage vpage1 = page1.render();
		
		MyMultiPage page2 = new MyMultiPage(612, 792);
		AffineTransform pagelet2Position = new AffineTransform();
		pagelet2Position.translate(0, 0);
//		pagelet2Position.scale(0.5, 0.5);
//		pagelet2Position.rotate(0.02);
		page2.addPage(vpage1, pagelet2Position);
		VirtualPage vpage2 = page2.render();

		try {
			// Get content

			// Render and save
			VirtualDocument doc = new VirtualDocument(Arrays.asList(vpage2));
			PDDocument output = new PDFRenderer().render(doc);
			output.save(new File("test/imposed-multiple2.pdf"));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
