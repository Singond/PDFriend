package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.Leaf.Orientation;
import com.github.singond.pdfriend.book.Sheet;
import com.github.singond.pdfriend.book.SinglePage;
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
		sheet.addLeaf(leaf);
		sheet.addLeaf(leaf2);

		try (PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));) {
			// Get content
			VirtualPage one = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 0, "page-1")));
			VirtualPage two = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 1, "page-2")));
			VirtualPage three = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 2, "page-3")));
			VirtualPage four = new VirtualPage(612, 792, Arrays.asList(new PDFPage(source, 3, "page-4")));
			leaf.numberPagesFrom(1);
			leaf2.numberPagesFrom(3);
			((SinglePage) leaf.getRecto()).setSource(one);
			((SinglePage) leaf.getVerso()).setSource(two);
			((SinglePage) leaf2.getRecto()).setSource(three);
			((SinglePage) leaf2.getVerso()).setSource(four);

			// Build document model
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			VirtualPage sheetFront = sheet.renderFront();
			VirtualPage sheetBack = sheet.renderBack();
			doc.addPage(sheetFront);
			doc.addPage(sheetBack);

			// Render and save
			PDDocument output = new PDFRenderer().render(doc.build());
			output.save(new File("test/imposed-sheet.pdf"));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
