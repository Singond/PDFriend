package cz.slanyj.pdfriend.test;

import java.util.Iterator;
import cz.slanyj.pdfriend.book.model.FlipDirection;
import cz.slanyj.pdfriend.book.model.Leaf;
import cz.slanyj.pdfriend.book.model.Sheet;
import cz.slanyj.pdfriend.book.model.Leaf.Orientation;
import cz.slanyj.pdfriend.book.model.Page;

/**
 * A sample sheet.
 * Assume landscape document of US Letter format (792 x 612 pt).
 * @author Singon
 *
 */
public class IterateSheet {

	public static void main(String[] args) {
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		leaf.numberPagesFrom(5);
		
		Leaf leaf2 = new Leaf(612, 792);
		leaf2.setAsFrontPosition(new Leaf.Position(918, 396, 0));
		leaf2.setOrientation(Orientation.VERSO_UP);
		leaf2.setFlipDirection(FlipDirection.AROUND_Y);
		leaf2.numberPagesFrom(7);
		
		Leaf leaf3 = new Leaf(612, 792);
		leaf3.setAsFrontPosition(new Leaf.Position(306, 1188, 0));
		leaf3.setOrientation(Orientation.VERSO_UP);
		leaf3.setFlipDirection(FlipDirection.AROUND_Y);
		leaf3.numberPagesFrom(1);
		
		Leaf leaf4 = new Leaf(612, 792);
		leaf4.setAsFrontPosition(new Leaf.Position(918, 1188, 0));
		leaf4.setOrientation(Orientation.VERSO_UP);
		leaf4.setFlipDirection(FlipDirection.AROUND_Y);
		leaf4.numberPagesFrom(3);
		
		Sheet sheet = new Sheet(1224, 1584);
		sheet.addLeaf(leaf);
		sheet.addLeaf(leaf2);
		sheet.addLeaf(leaf3);
		sheet.addLeaf(leaf4);
		
		for (Leaf l : sheet.leaves()) {
			System.out.println(l);
		}
		
		for (Page p : sheet.pages()) {
			System.out.println(p);
		}
	}

}
