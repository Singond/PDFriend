package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.Stack;
import com.github.singond.pdfriend.book.control.Stack.Flip;
import com.github.singond.pdfriend.book.model.FlipDirection;
import com.github.singond.pdfriend.book.model.Leaf;
import com.github.singond.pdfriend.book.model.Signature;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.book.model.Leaf.Orientation;
import com.github.singond.pdfriend.document.ImportException;
import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.geometry.Line;
import com.github.singond.pdfriend.geometry.Point;

/**
 * A sample signature of two sheets.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class PrintStack {
	
	private static final ExtendedLogger logger = Log.logger(PrintStack.class);

	public static void main(String[] args) {
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
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
		
		Signature signature = stack.buildSignature(leaf);
		signature.numberPagesFrom(1);
		
		Volume volume = new Volume();
		volume.add(signature);
		
		try {
			// Get content
			File srcFile = new File("test/lorem-letter.pdf");
			VirtualDocument source = new PDFImporter(srcFile).importDocument();
			new SequentialSourceProvider(source).setSourceTo(volume.pages());
			VirtualDocument output = volume.renderDocument();
			new PDFRenderer().renderAndSave(output, new File("test/printed-stack.pdf"));
			logger.info("Finished printing stack");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
