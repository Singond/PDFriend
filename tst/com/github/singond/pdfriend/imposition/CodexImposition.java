package com.github.singond.pdfriend.imposition;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.singond.geometry.plane.Line;
import com.github.singond.geometry.plane.Point;
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
import com.github.singond.pdfriend.geometry.PaperFormat;
import com.github.singond.pdfriend.geometry.PaperFormats;

@SuppressWarnings("unused")
public class CodexImposition {

	/** Logger */
	private static ExtendedLogger logger = Log.logger(CodexImposition.class);
	
	File input = new File("test/lorem-letter-bg.pdf");
	
	@Test
	public void moduleCodex() {
		Preprocessor.Settings preprocess = Preprocessor.Settings.auto();
//		preprocess.setRotation(0.1);
		
		CommonSettings.Builder sb = new CommonSettings.Builder();
//		sb.setPageSize(PaperFormats.A5.dimensions(PaperFormat.Orientation.PORTRAIT));
//		sb.setSheetSize(PaperFormats.A2.dimensions(PaperFormat.Orientation.LANDSCAPE));

//		Codex codex = Codex.leftBuilder()
		Codex codex = Codex.rightBuilder()
//				.foldHorizontallyUp()
//				.foldVerticallyUp()
//				.foldHorizontallyUp()
//				.foldVerticallyUp()
				.foldHorizontallyDown()
				.foldVerticallyDown()
				.foldHorizontallyDown()
				.foldVerticallyDown()
//				.setSheetsInSignature(2)
				.acceptPreprocessSettings(preprocess)
				.acceptCommonSettings(sb.build())
				.build();
		
		File output = new File("test/imposed-codex.pdf");
		
		try {
			VirtualDocument inDoc = new PDFParser().parseDocument(Files.readAllBytes(input.toPath()));
			VirtualDocument outDoc = codex.imposeAndRender(inDoc);
			new PDFRenderer().renderAndSave(outDoc, output);
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void hardCodedCodex() {
		File output = new File("test/imposed-codex-hardcoded.pdf");
		
		Leaf leaf = new Leaf(612, 792);
//		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
//		AffineTransform at = new AffineTransform();
//		at.translate(84*PPMM, 50*PPMM);
//		leaf.setAsFrontPosition(at);
//		leaf.setOrientation(Orientation.RECTO_UP);
//		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		double pageWidth = 612;
		double pageHeight = 792;
		
		Stack stack = new Stack(4 * pageWidth, pageHeight);
		List<Stack.Manipulation> mm = new ArrayList<Stack.Manipulation>();
		
//		mm.add(new Stack.Gather(2));

		double fold1 = pageWidth * 2;
		Line axis1 = new Line(new Point(fold1, 0), new Point(fold1, 1));
		mm.add(new Stack.Fold(axis1, Stack.Fold.Direction.OVER));

		double fold2 = pageWidth;
		Line axis2 = new Line(new Point(fold2, 0), new Point(fold2, 1));
		mm.add(new Stack.Fold(axis2, Stack.Fold.Direction.OVER));

//		mm.add(Flip.horizontal(pageWidth));
		//mm.add(new Stack.Gather(2));
		
		stack.performManipulations(mm);
		Signature signature = stack.buildSignature(leaf);
		signature.numberPagesFrom(1);
		
		Volume volume = new Volume();
		volume.add(signature);
		
		try {
			// Get content
			VirtualDocument source = new PDFParser().parseDocument(Files.readAllBytes(input.toPath()));
			new SequentialSourceProvider(source).setSourceTo(volume.pages());
			VirtualDocument outputDoc = volume.renderDocument();
			new PDFRenderer().renderAndSave(outputDoc, output);
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
