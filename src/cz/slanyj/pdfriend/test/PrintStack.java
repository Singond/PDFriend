package cz.slanyj.pdfriend.test;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import cz.slanyj.pdfriend.SourcePage;
import cz.slanyj.pdfriend.book.Field;
import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Leaf.Orientation;
import cz.slanyj.pdfriend.book.Sheet;
import cz.slanyj.pdfriend.book.Signature;
import cz.slanyj.pdfriend.book.Stack;
import cz.slanyj.pdfriend.book.Stack.Join.Placement;

/**
 * A sample signature of two sheets.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class PrintStack {

	public static void main(String[] args) {
		Leaf leaf = new Leaf(792, 612);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, Math.PI/2));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		
		Leaf leaf2 = new Leaf(792, 612);
		leaf2.setAsFrontPosition(new Leaf.Position(918, 396, Math.PI/2));
		leaf2.setOrientation(Orientation.RECTO_UP);
		leaf2.setFlipDirection(FlipDirection.AROUND_Y);
		
		Stack stack = new Stack();
		Sheet sheet = new Sheet(1224, 792);
		AffineTransform position1 = new AffineTransform();
		Field field1 = new Field(sheet, position1, Field.Orientation.POSITIVE);
		stack.addField(field1);
		
		Stack stack2 = new Stack();
		Sheet sheet2 = new Sheet(1224, 792);
		AffineTransform position2 = AffineTransform.getTranslateInstance(792, 0);
		Field field2 = new Field(sheet2, position1, Field.Orientation.NEGATIVE);
		stack2.addField(field2);
		
		field1.addLeaf(leaf);
		field2.addLeaf(leaf2);
		
		List<Stack.Manipulation> mm = new ArrayList<Stack.Manipulation>();
		mm.add(new Stack.Join(stack2, Stack.Join.Placement.TOP));
		stack.performManipulations(mm);
		stack.placeFields();
		
		Signature signature = new Signature();
		signature.add(sheet);
		
		try {
			// Get content
			PDDocument source = PDDocument.load(new File("test/document.pdf"));
			SourcePage one = new SourcePage(source, 0);
			SourcePage two = new SourcePage(source, 1);
			SourcePage three = new SourcePage(source, 2);
			SourcePage four = new SourcePage(source, 3);
			leaf.setContent(one, two);
			leaf2.setContent(three, four);
			
			PDDocument doc = new PDDocument();
			signature.renderAllSheets(doc);
			
			// Save
			doc.save(new File("test/printed-stack.pdf"));
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
