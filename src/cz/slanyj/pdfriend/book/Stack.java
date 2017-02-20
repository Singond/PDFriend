package cz.slanyj.pdfriend.book;

import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;
import cz.slanyj.pdfriend.book.Field.Orientation;

/**
 * A vertical stack of Fields, ie. a collection of possibly folded sheets
 * of paper.
 * The Stack will be rendered into a collection of Sheets upon which Leaves
 * in proper position and order are placed.
 * @author Singon
 *
 */
public class Stack {

	/**
	 * The list of Sheet Fields represented by this Stack. The fields are
	 * numbered from bottom to top, ie the lowest one is 0.
	 */
	private final List<Field> fields;
	/** A list of all Sheets referenced by the Fields */
	private final List<Sheet> sheets;
	
	/**
	 * The width of this Stack before folding.
	 * All Sheets rendered by this Stack will use this width.
	 */
	private final double width;
	/**
	 * The height of this Stack before folding.
	 * All Sheets rendered by this Stack will use this height.
	 */
	private final double height;

	
	/**
	 * Constructs a new Stack with a default Field and Sheet.
	 * @param width The unfolded width of this Stack
	 * @param height The unfolded height of this Stack
	 */
	public Stack(double width, double height) {
		this.width = width;
		this.height = height;
		sheets = new LinkedList<>();
		Sheet s = new Sheet(width, height);
		sheets.add(s);
		Field f = new Field(s, new AffineTransform(), Orientation.POSITIVE);
		fields = new LinkedList<>();
		fields.add(f);
	}
	
	
	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	/**
	 * Assembles Fields on all Sheets into the final Signature.
	 * @param A template of Leaves to be placed into all Sheets.
	 */
	public Signature buildSignature(List<Leaf> template) {
		Signature signature = new Signature();
		Order<Leaf> orderMap = new Order<>();
		/** The order of the Leaf in the folded Stack */
		for (Field f : fields) {
			for (Leaf l : template) {
				if (f.isInSheet(l)) {
					Leaf nl = l.cloneAsTemplate();
					orderMap.addNext(nl);
					f.addLeaf(nl);
				}
			}
		}
		// Place the fields into their sheets
		for (Field f : fields) {
			f.placeLeaves();
		}
		for (Sheet s : sheets) {
			signature.add(s);
		}
		signature.setLeafOrder(orderMap);
		return signature;
	}
	
	/**
	 * Performs a manipulation on this Stack.
	 * @param manipulation An object representing the manipulation.
	 */
	public void performManipulation(Manipulation manipulation) {
		manipulation.manipulate(this);
	}

	/**
	 * Performs multiple manipulations on this Stack.
	 * @param manipulations A List of manipulations sorted from the
	 *            manipulation to be performed first to last.
	 */
	public void performManipulations(List<Manipulation> manipulations) {
		for (Manipulation m : manipulations) {
			performManipulation(m);
		}
	}

	/* Manipulations of the stack */

	/**
	 * Represents physical manipulation with a stack of Sheets, such as
	 * folding, cutting or stacking.
	 * @author Singon
	 *
	 */
	public static interface Manipulation {
		/** Manipulates the stack */
		public void manipulate(Stack stack);
	}

	/**
	 * Stacks two Stacks on top of each other.
	 * @author Singon
	 *
	 */
	public static class Join implements Manipulation {

		/** The stack to be joined */
		private final Stack other;
		/** The placement of the other stack */
		private final Placement placement;

		public Join(Stack other, Placement placement) {
			this.other = other;
			this.placement = placement;
		}

		/**
		 * Performs the stacking
		 * @see cz.slanyj.pdfriend.book.Stack.Join
		 */
		@Override
		public void manipulate(Stack stack) {
			/** The fields to be joined */
			List<Field> joined = other.fields;
			if (placement == Placement.TOP) {
				stack.fields.addAll(joined);
				stack.sheets.addAll(other.sheets);
			} else if (placement == Placement.BOTTOM) {
				stack.fields.addAll(0, joined);
				stack.sheets.addAll(0, other.sheets);
			}
		}

		/** Represents the top or bottom of the Stack */
		public static enum Placement {
			TOP, BOTTOM;
		}
	}
}
