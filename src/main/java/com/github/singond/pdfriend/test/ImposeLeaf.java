package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.Leaf.Orientation;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.content.PDFPage;
import com.github.singond.pdfriend.format.process.PDFRenderer;

/**
 * A sample leaf.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeLeaf {

	public static void main(String[] args) {
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);

		try (PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"))) {
			// Get content
			VirtualPage one = new VirtualPage(612, 792, Arrays.asList(
					new PDFPage(source, 0, "page-1")));
			VirtualPage two = new VirtualPage(612, 792, Arrays.asList(
					new PDFPage(source, 1, "page-2")));
//			((SinglePage) leaf.getFrontPage()).setSource(one);
//			((SinglePage) leaf.getBackPage()).setSource(two);

			// Impose
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			doc.addPage(one);
			doc.addPage(two);

			// Output
			VirtualDocument document = doc.build();
			PDDocument out = new PDFRenderer().render(document);
			out.save(new File("test/imposed-leaf.pdf"));
			out.close();
			source.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
