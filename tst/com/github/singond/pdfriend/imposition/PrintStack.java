package com.github.singond.pdfriend.imposition;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.book.Signature;
import com.github.singond.pdfriend.book.Stack;
import com.github.singond.pdfriend.book.Volume;
import com.github.singond.pdfriend.book.Leaf.Orientation;
import com.github.singond.pdfriend.book.Stack.Flip;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.geometry.plane.Line;
import com.github.singond.geometry.plane.Point;

/**
 * A sample signature of two sheets.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class PrintStack {
	
	private static ExtendedLogger logger = Log.logger(PrintStack.class);
	/** Points per millimetre */
	private static final double PPMM = 72/25.4;

	public static void main(String[] args) {
		Leaf leaf = new Leaf(612, 792);
//		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		AffineTransform at = new AffineTransform();
		at.translate(84*PPMM, 50*PPMM);
		leaf.setAsFrontPosition(at);
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		double pageWidth = 300 * PPMM;
		double pageHeight = 400 * PPMM;
		
		Stack stack = new Stack(2*pageWidth, pageHeight);
		
		List<Stack.Manipulation> mm = new ArrayList<Stack.Manipulation>();
		mm.add(new Stack.Gather(2));
//		Line axis2 = new Line(new Point(0, 792), 0);
//		mm.add(new Stack.Fold(axis2, Stack.Fold.Direction.UNDER));
		Line axis = new Line(new Point(pageWidth, 0), Math.PI/2);
		mm.add(new Stack.Fold(axis, Stack.Fold.Direction.UNDER));
		mm.add(Flip.horizontal(pageWidth));
		//mm.add(new Stack.Gather(2));
		stack.performManipulations(mm);
		
		Signature signature = stack.buildSignature(leaf);
		signature.numberPagesFrom(1);
		
		Volume volume = new Volume();
		volume.add(signature);
		
		try {
			// Get content
			File srcFile = new File("test/lorem-letter-bg.pdf");
			VirtualDocument source = new PDFParser().parseDocument(Files.readAllBytes(srcFile.toPath()));
			new SequentialSourceProvider(source).setSourceTo(volume.pages());
			VirtualDocument output = volume.renderDocument();
			new PDFRenderer().renderAndSave(output, new File("test/printed-stack.pdf"));
			logger.info("Finished printing stack");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}

}
