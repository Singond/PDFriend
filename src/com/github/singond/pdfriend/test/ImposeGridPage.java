package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.FlipDirection;
import com.github.singond.pdfriend.book.model.GridPage;
import com.github.singond.pdfriend.book.model.Leaf;
import com.github.singond.pdfriend.book.model.Sheet;
import com.github.singond.pdfriend.book.model.GridPage.Direction;
import com.github.singond.pdfriend.book.model.GridPage.GridOrientation;
import com.github.singond.pdfriend.book.model.Leaf.Orientation;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ImportException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.format.process.PDFRenderer;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeGridPage {
	
	private static ExtendedLogger logger = Log.logger(ImposeGridPage.class);

	public static void main(String[] args) {
		logger.info("This is a test of the GridPage class functionality");
		GridOrientation go = GridOrientation.ROTATED_LEFT;
		GridPage page1 = new GridPage(2, 2, 306, 396, 0, 0, go);
		GridPage page2 = new GridPage(2, 2, 306, 396, go);
//		page1.scalePages(0.55);
		page1.fitPages();
//		page1.rotatePages(Math.PI/2);
		page2.fitPages();
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
			VirtualDocument source = new PDFImporter().importDocument(Files.readAllBytes(srcFile.toPath()));
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
		} catch (ImportException e) {
			e.printStackTrace();
		}
	}

}
