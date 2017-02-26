package cz.slanyj.pdfriend.book;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import cz.slanyj.pdfriend.Bundle;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.geometry.Transformations;
import cz.slanyj.pdfriend.book.Field.Orientation;
import cz.slanyj.pdfriend.geometry.Line;

/**
 * <p>A vertical stack of Fields, ie. a collection of possibly folded
 * sheets of paper. Here, each Field represents a single layer of such stack.
 * Multiple Fields may belong to the same Sheet; such as when the Sheet has
 * been folded in half.</p>
 * <p>The sheets can be manipulated, ie. folded and stacked to simulate these
 * procedures in real-world print production.
 * Once the Stack has been manipulated, Leaves can be placed into it.
 * This involves taking a pattern of Leaves (most usually, a single Leaf)
 * and applying it into each layer of the stack (ie. each Field), in the
 * order the layers are encountered when going from top to bottom.</p>
 * <p>The Stack is finally rendered into a Signature which contains Sheets
 * and properly imposed Leaves, which, when printed, folded and stacked
 * as specified in the manipulation phase and trimmed, will yield a section
 * of a book with sequentially arranged pages.</p>
 * @author Singon
 *
 */
public class Stack {

	/**
	 * The list of Sheet Fields represented by this Stack. The fields are
	 * numbered from top to bottom, ie the topmost one is 0.
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
	 * Constructs a new Stack, optionally with a default Field and Sheet.
	 * @param width The unfolded width of this Stack.
	 * @param height The unfolded height of this Stack.
	 * @param initialize Whether to create default Field and Sheet.
	 */
	public Stack(double width, double height, boolean initialize) {
		this.width = width;
		this.height = height;
		sheets = new LinkedList<>();
		fields = new LinkedList<>();
		if (initialize) {
			Sheet s = new Sheet(width, height);
			sheets.add(s);
			Field f = new Field(s, new AffineTransform(), Orientation.POSITIVE);
			fields.add(f);
		}
	}
	/**
	 * Constructs a new Stack with a default Field and Sheet.
	 * @param width The unfolded width of this Stack.
	 * @param height The unfolded height of this Stack.
	 */
	public Stack(double width, double height) {
		this(width, height, true);
	}
	
	
	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	/**
	 * Performs a partial-depth clone of this Stack by copying the graph
	 * of Fields down to the level of Sheets, while preserving their
	 * relations. The clone doesn't reach into Sheets, meaning the Sheets
	 * are copied blank, without Leaves.
	 * In order for this method to work correctly, all Sheets referenced
	 * by Fields must be present in the {@code sheets} list.
	 */
	public Stack copy() {
		/**
		 * Helper class to clone Sheets and keep correspondence between
		 * the clones and the originals, so that each original is not
		 * cloned twice.
		 * The Sheets are cloned blank, ie. without any Leaves.
		 * @author Singon
		 *
		 */
		class SheetCloner {
			/** The map of clones corresponding to originals */
			private Map<Sheet, Sheet> clones = new HashMap<>();
			
			/**
			 * For a given original Sheet, returns its single clone,
			 * creating one if it doesn't exist.
			 * On subsequent invocations for the same Sheet, it doesn't
			 * create a new object, returning always the first and only
			 * copy instead.
			 */
			Sheet cloneSheet(Sheet s) {
				if (clones.containsKey(s)) {
					return clones.get(s);
				} else {
					Sheet sheetClone = s.cloneBlank();
					clones.put(s, sheetClone);
					return sheetClone;
				}
			}
		}
		
		// Do the clone
		Stack clone = new Stack(width, height, false);
		SheetCloner cloner = new SheetCloner();
		for (Field f : fields) {
			Sheet parentClone = cloner.cloneSheet(f.getSheet());
			clone.fields.add(new Field(f, parentClone));
		}
		for (Sheet s : sheets) {
			clone.sheets.add(cloner.cloneSheet(s));
		}
		return clone;
	}
	
	/**
	 * Assembles Fields on all Sheets into the final Signature,
	 * placing the given pattern of Leaves into each Field in the order
	 * from bottom (Field 0) to top.
	 * The Leaves are placed recto-up onto the Field. If the Stack is to
	 * be filled in reverse order, ie. from bottom to top, it must be
	 * flipped first.
	 * @param pattern A pattern of Leaves to be placed into all Sheets.
	 */
	public Signature buildSignature(List<Leaf> pattern) {
		Signature signature = new Signature();
		Order<Leaf> orderMap = new Order<>();
		/** The order of the Leaf in the folded Stack */
		for (Field f : fields) {
			for (Leaf l : pattern) {
				if (f.isInSheet(l)) {
					Leaf nl = l.cloneAsTemplate();
					//nl.setOrientation(Leaf.Orientation.RECTO_UP);
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
	 * Stacks two existing Stacks on top of each other.
	 * @author Singon
	 *
	 */
	public static class Join implements Manipulation {

		/** The stack to be joined */
		private final Stack other;
		/** The placement of the other stack */
		private final Placement placement;

		/**
		 * Creates a new Join object which represents stacking another
		 * Stack on top or bottom of this one.
		 * @param other The Stack to be joined with this one.
		 * @param placement Specifies whether the other Stack should be
		 * placed to top or bottom of this Stack.
		 */
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
				stack.fields.addAll(0, joined);
				stack.sheets.addAll(0, other.sheets);
			} else if (placement == Placement.BOTTOM) {
				stack.fields.addAll(joined);
				stack.sheets.addAll(other.sheets);
			} else {
				assert false : placement;
			}
		}
	}
	
	/**
	 * Stacks a number of identical copies of this Stack into one.
	 * Each Stack has its own set of Sheets.
	 * @author Singon
	 */
	public static class Gather implements Manipulation {
		
		/**
		 * The number of copies in the finished stack
		 * (ie. number of new stacks plus one).
		 */
		private final int copies;
		/**
		 * Placement of copies, either top or bottom.
		 * Should make no difference, anyway.
		 */
		private final Placement placement;
		
		/**
		 * Creates a new Stack manipulation object, which stacks a given
		 * number of copies of this Stack on top or bottom of each other.
		 * @param n The number of copies of this Stack in the finished
		 * Stack (thus the number of newly created copies will be n-1).
		 * @param placement Where to put the copies, either top or bottom
		 * of the initial Stack.
		 * @throw IllegalArgumentException when n is less than one.
		 */
		public Gather(int n, Placement placement) {
			if (n < 1) {
				throw new IllegalArgumentException
					("Number of copies must be at least one.");
			} else if (n == 1) {
				Log.warn(Bundle.console, "stack_gatherOne");
			}
			this.copies = n;
			this.placement = placement;
		}
		/**
		 * Creates a new Stack manipulation object, which stacks a given
		 * number of copies of this Stack on top of each other.
		 * @param n The number of copies of this Stack in the finished
		 * Stack (thus the number of newly created copies will be n-1).
		 * @throw IllegalArgumentException when n is less than one.
		 */
		public Gather(int n) {
			this(n, Placement.BOTTOM);
		}
		
		
		public void manipulate(Stack stack) {
			Stack original = stack.copy();
			if (placement == Placement.TOP) {
				for (int i=0; i<copies-1; i++) {
					Stack copy = original.copy();
					stack.sheets.addAll(0, copy.sheets);
					stack.fields.addAll(0, copy.fields);
				}
			} else if (placement == Placement.BOTTOM) {
				for (int i=0; i<copies-1; i++) {
					Stack copy = original.copy();
					stack.sheets.addAll(copy.sheets);
					stack.fields.addAll(copy.fields);
				}
			} else {
				assert false: placement;
			}
		}
	}
	
	/**
	 * Folds the Stack along a given axis.
	 * @author Singon
	 */
	public static class Fold implements Manipulation {

		/** The axis of the fold. */
		private final Line axis;
		/** Direction of folding. */
		private final Direction direction;
		
		public Fold(Line axis, Direction dir) {
			if (dir == null)
				throw new IllegalArgumentException("Fold direction cannot be null");
			this.axis = axis;
			this.direction = dir;
		}
		
		@Override
		public void manipulate(Stack stack) {
			int fieldCount = stack.fields.size();
			// The folded part of the stack
			List<Field> foldedStack = new ArrayList<>(fieldCount);
			AffineTransform position = Transformations.mirror(axis);
			
			if (direction == Direction.UNDER) {
				// Iterate backwards, place new Fields to bottom of Stack
				int end = fieldCount;
				ListIterator<Field> iter = stack.fields.listIterator(end);
				while (iter.hasPrevious()) {
					Field f = iter.previous();
					position.concatenate(f.getPosition());
					Field folded = new Field(f.getSheet(),
					                         position,
					                         f.getOrientation().inverse());
					foldedStack.add(folded);
				}
				iter = null;
				stack.fields.addAll(foldedStack);
			} else if (direction == Direction.OVER) {
				// Iterate forward, place new Fields to the top of Stack
				for (Field f : stack.fields) {
					position.concatenate(f.getPosition());
					Field folded = new Field(f.getSheet(),
					                         position,
					                         f.getOrientation().inverse());
					foldedStack.add(0, folded);
				}
				stack.fields.addAll(0, foldedStack);
			} else {
				assert false : direction;
			}
		}
		
		/**
		 * Specifies the direction of folding.
		 * The Stack is assumed to be fixed in the origin and the newly
		 * created Fields can be placed either to top or to bottom. 
		 * @author Singon
		 */
		public static enum Direction {
			/**
			 * The origin stays in position and Fields are folded on top.
			 */
			OVER,
			/**
			 * The origin stays in position and Fields are folded to bottom.
			 */
			UNDER;
		}
	}
	
	/** Represents the top or bottom of the Stack */
	public static enum Placement {
		TOP, BOTTOM;
	}
}
