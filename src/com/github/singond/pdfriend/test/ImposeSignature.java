package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.Order;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.model.FlipDirection;
import com.github.singond.pdfriend.book.model.Leaf;
import com.github.singond.pdfriend.book.model.Sheet;
import com.github.singond.pdfriend.book.model.Signature;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.book.model.Leaf.Orientation;
import com.github.singond.pdfriend.document.ImportException;
import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.format.process.PDFRenderer;

/**
 * A sample signature of two sheets.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class ImposeSignature {
	
	private static ExtendedLogger logger = Log.logger(ImposeSignature.class);

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
		
		Leaf leaf3 = new Leaf(612, 792);
		leaf3.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf3.setOrientation(Orientation.RECTO_UP);
		leaf3.setFlipDirection(FlipDirection.AROUND_Y);
		
		Leaf leaf4 = new Leaf(612, 792);
		leaf4.setAsFrontPosition(new Leaf.Position(918, 396, 0));
		leaf4.setOrientation(Orientation.VERSO_UP);
		leaf4.setFlipDirection(FlipDirection.AROUND_Y);
		
		
		Sheet sheet2 = new Sheet(1224, 792);
		sheet2.addLeaf(leaf3);
		sheet2.addLeaf(leaf4);
		
		Signature signature = new Signature();
		signature.add(sheet);
		signature.add(sheet2);
		signature.setLeafOrder(new Order<Leaf>());
		int next = signature.numberPagesFrom(25);
		logger.debug("Next page is {}", next);
		
		Volume volume = new Volume();
		volume.add(signature);
		
		try {
			// Get content
			File src = new File("test/lorem-letter.pdf");
			VirtualDocument doc = new PDFImporter(src).importDocument();
			new SequentialSourceProvider(doc).setSourceTo(volume.pages());
			
			VirtualDocument.Builder outDoc = new VirtualDocument.Builder();
			signature.renderAllSheets(outDoc);
			
			// Render and save
			PDDocument output = new PDFRenderer().render(outDoc.build());
			output.save(new File("test/imposed-signature.pdf"));
			output.close();
			logger.info("printDone", "imposed-signature.pdf");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
