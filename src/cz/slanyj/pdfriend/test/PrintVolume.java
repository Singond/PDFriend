package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.book.control.Order;
import cz.slanyj.pdfriend.book.control.SequentialSourceProvider;
import cz.slanyj.pdfriend.book.model.FlipDirection;
import cz.slanyj.pdfriend.book.model.Leaf;
import cz.slanyj.pdfriend.book.model.Sheet;
import cz.slanyj.pdfriend.book.model.Signature;
import cz.slanyj.pdfriend.book.model.Volume;
import cz.slanyj.pdfriend.book.model.Leaf.Orientation;
import cz.slanyj.pdfriend.document.ImportException;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.format.process.PDFImporter;
import cz.slanyj.pdfriend.format.process.PDFRenderer;

/**
 * A sample volume of two signatures.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class PrintVolume {
	
	private static final ExtendedLogger logger = Log.logger(PrintVolume.class);

	public static void main(String[] args) {
		Leaf leaf = new Leaf(612, 792);
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
		
		Leaf leaf3 = new Leaf(612, 792);
		leaf3.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf3.setOrientation(Orientation.RECTO_UP);
		leaf3.setFlipDirection(FlipDirection.AROUND_Y);
		leaf3.numberPagesFrom(5);
		
		Leaf leaf4 = new Leaf(612, 792);
		leaf4.setAsFrontPosition(new Leaf.Position(918, 396, 0));
		leaf4.setOrientation(Orientation.VERSO_UP);
		leaf4.setFlipDirection(FlipDirection.AROUND_Y);
		leaf4.numberPagesFrom(7);
		
		Sheet sheet2 = new Sheet(1224, 792);
		sheet2.addLeaf(leaf3);
		sheet2.addLeaf(leaf4);
		
		Signature signature = new Signature();
		signature.add(sheet);
		signature.add(sheet2);
		signature.setLeafOrder(new Order<Leaf>());
		
		Volume volume = new Volume();
		volume.add(signature);
		
		try {
			// Get content
			File srcFile = new File("test/lorem-letter.pdf");
			VirtualDocument source = new PDFImporter(srcFile).importDocument();
			new SequentialSourceProvider(source).setSourceTo(volume.pages());
			VirtualDocument doc = volume.renderDocument();
			new PDFRenderer().renderAndSave(doc, new File("test/printed-volume.pdf"));
			logger.info("Finished writing document");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
