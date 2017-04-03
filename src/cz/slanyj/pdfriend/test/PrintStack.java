package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Order;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.book.Signature;
import cz.slanyj.pdfriend.book.Stack;
import cz.slanyj.pdfriend.book.Stack.Flip;
import cz.slanyj.pdfriend.book.Volume;
import cz.slanyj.pdfriend.geometry.Line;
import cz.slanyj.pdfriend.geometry.Point;
import cz.slanyj.pdfriend.impose.formats.PDFSourceDocument;

/**
 * A sample signature of two sheets.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class PrintStack {

	public static void main(String[] args) {
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		List<Leaf> template = new ArrayList<>();
		template.add(leaf);
		
		Stack stack = new Stack(1224, 1584);
		
		List<Stack.Manipulation> mm = new ArrayList<Stack.Manipulation>();
		mm.add(new Stack.Gather(2));
		Line axis2 = new Line(new Point(0, 792), 0);
		mm.add(new Stack.Fold(axis2, Stack.Fold.Direction.UNDER));
		Line axis = new Line(new Point(612, 0), Math.PI/2);
		mm.add(new Stack.Fold(axis, Stack.Fold.Direction.UNDER));
		mm.add(Flip.horizontal(612));
		//mm.add(new Stack.Gather(2));
		stack.performManipulations(mm);
		
		Stack copy = stack.copy();
		
		Signature signature = stack.buildSignature(template);
		signature.numberPagesFrom(1);
		
		Volume volume = new Volume();
		volume.add(signature);
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
			PDFSourceDocument sourceDoc = new PDFSourceDocument(source);
			volume.setSource(sourceDoc.getAllPages());
				
			volume.renderAndSaveDocument(new File("test/printed-stack.pdf"));
			Log.info("Finished printing stack");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
