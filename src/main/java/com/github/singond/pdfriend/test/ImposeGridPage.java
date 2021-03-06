package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.GridPage;
import com.github.singond.pdfriend.book.GridPage.Direction;
import com.github.singond.pdfriend.book.GridPage.GridOrientation;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.Leaf.Orientation;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.book.Sheet;
import com.github.singond.pdfriend.book.SourceProvider;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
@SuppressWarnings("deprecation")
public class ImposeGridPage {

	private static ExtendedLogger logger = Log.logger(ImposeGridPage.class);

	public static void main(String[] args) {
		logger.info("This is a test of the GridPage class functionality");
		GridOrientation go = GridOrientation.ROTATED_LEFT;
		GridPage page1 = new GridPage(2, 2, 306, 396, 0, 0, go);
		GridPage page2 = new GridPage(2, 2, 306, 396, go);
//		page1.scalePages(0.55);
//		page1.rotatePages(Math.PI/2);
		page2.setDirection(Direction.COLUMNS);

		Leaf leaf = new Leaf(page1, page2);
		leaf.setAsFrontPosition(new Leaf.Position(396, 306, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		leaf.numberPagesFrom(1);

//		Sheet sheet = new Sheet(612, 792);
		Sheet sheet = new Sheet(792, 612);
		sheet.addLeaf(leaf);

		try {
			// Get content
			File srcFile = new File("test/lorem-letter.pdf");
			@SuppressWarnings("resource")
			VirtualDocument source = new PDFParser().parseDocument(Files.newInputStream(srcFile.toPath()));
			SourceProvider<Page> sp = new SequentialSourceProvider(source);
			sp.setSourceTo(page1);
			sp.setSourceTo(page2);

			// Build document model
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			VirtualPage sheetFront = sheet.renderFront();
			VirtualPage sheetBack = sheet.renderBack();
			doc.addPage(sheetFront);
			doc.addPage(sheetBack);

			// Render and save
			PDDocument output = new PDFRenderer().render(doc.build());
			logger.info("Saving file test/imposed-gridpage.pdf");
			output.save(new File("test/imposed-gridpage.pdf"));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}

}
