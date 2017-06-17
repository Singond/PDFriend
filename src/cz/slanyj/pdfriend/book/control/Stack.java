package cz.slanyj.pdfriend.book.control;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.geometry.Transformations;
import cz.slanyj.pdfriend.book.control.Layer.Orientation;
import cz.slanyj.pdfriend.book.model.Leaf;
import cz.slanyj.pdfriend.book.model.Sheet;
import cz.slanyj.pdfriend.book.model.Signature;
import cz.slanyj.pdfriend.geometry.Line;
import cz.slanyj.pdfriend.geometry.Point;

/**
 * <p>A vertical stack of Layers, ie. a collection of possibly folded
 * sheets of paper. Here, each Layer represents a single layer of such stack.
 * Multiple Layers may belong to the same Sheet; such as when the Sheet has
 * been folded in half.</p>
 * <p>The sheets can be manipulated, ie. folded and stacked to simulate these
 * procedures in real-world print production.
 * Once the Stack has been manipulated, Leaves can be placed into it.
 * This involves taking a pattern of Leaves (most usually, a single Leaf)
 * and applying it into each layer of the stack (ie. each Layer), in the
 * order the layers are encountered when going from top to bottom.</p>
 * <p>The Stack is finally rendered into a Signature which contains Sheets
 * and properly imposed Leaves, which, when printed, folded and stacked
 * as specified in the manipulation phase and trimmed, will yield a section
 * of a book with sequentially arranged pages.</p>
 * @author Singon
 *
 */
public class Stack {
	
	private static final ExtendedLogger logger = Log.logger(Stack.class);

	/**
	 * The list of Sheet Layers represented by this Stack. The layers are
	 * numbered from top to bottom, ie the topmost one is 0.
	 */
	private final List<Layer> layers;
	/** A list of all Sheets referenced by the Layers */
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
	 * Constructs a new Stack, optionally with a default Layer and Sheet.
	 * @param width The unfolded width of this Stack.
	 * @param height The unfolded height of this Stack.
	 * @param initialize Whether to create default Layer and Sheet.
	 */
	public Stack(double width, double height, boolean initialize) {
		this.width = width;
		this.height = height;
		sheets = new LinkedList<>();
		layers = new LinkedList<>();
		if (initialize) {
			Sheet s = new Sheet(width, height);
			sheets.add(s);
			Layer l = new Layer(s, new AffineTransform(), Orientation.POSITIVE);
			layers.add(l);
		}
	}
	/**
	 * Constructs a new Stack with a default Layer and Sheet.
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
	 * of Layers down to the level of Sheets, while preserving their
	 * relations. The clone doesn't reach into Sheets, meaning the Sheets
	 * are copied blank, without Leaves.
	 * In order for this method to work correctly, all Sheets referenced
	 * by Layers must be present in the {@code sheets} list.
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
		for (Layer l : layers) {
			Sheet parentClone = cloner.cloneSheet(l.getSheet());
			clone.layers.add(new Layer(l, parentClone));
		}
		for (Sheet s : sheets) {
			clone.sheets.add(cloner.cloneSheet(s));
		}
		return clone;
	}
	
	/**
	 * Assembles Layers on all Sheets into the final Signature,
	 * placing the given Leaf into each Layer in the order
	 * from bottom (Layer 0) to top.
	 * The Leaves are placed recto-up onto the Layer. If the Stack is to
	 * be filled in reverse order, ie. from bottom to top, it must be
	 * flipped first.
	 * @param leaf A Leaf whose blank copy is to be placed into all Sheets.
	 */
	public Signature buildSignature(Leaf leaf) {
		Signature signature = new Signature();
		Order<Leaf> orderMap = new Order<>();
		/** The order of the Leaf in the folded Stack */
		for (Layer lr : layers) {
			if (lr.isInSheet(leaf)) {
				Leaf nl = leaf.cloneAsTemplate();
				//nl.setOrientation(Leaf.Orientation.RECTO_UP);
				orderMap.addNext(nl);
				lr.addLeaf(nl);
			}
		}
		// Place the layers into their sheets
		for (Layer l : layers) {
			l.placeLeaves();
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
		 * @see cz.slanyj.pdfriend.book.control.Stack.Join
		 */
		@Override
		public void manipulate(Stack stack) {
			/** The layers to be joined */
			List<Layer> joined = other.layers;
			if (placement == Placement.TOP) {
				stack.layers.addAll(0, joined);
				stack.sheets.addAll(0, other.sheets);
			} else if (placement == Placement.BOTTOM) {
				stack.layers.addAll(joined);
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
				logger.warn("stack_gatherOne");
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
					stack.layers.addAll(0, copy.layers);
				}
			} else if (placement == Placement.BOTTOM) {
				for (int i=0; i<copies-1; i++) {
					Stack copy = original.copy();
					stack.sheets.addAll(copy.sheets);
					stack.layers.addAll(copy.layers);
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
			int layerCount = stack.layers.size();
			// The folded part of the stack
			List<Layer> foldedStack = new ArrayList<>(layerCount);
			AffineTransform fold = Transformations.mirror(axis);
			
			if (direction == Direction.UNDER) {
				// Iterate backwards, place new Layers to bottom of Stack
				int end = layerCount;
				ListIterator<Layer> iter = stack.layers.listIterator(end);
				while (iter.hasPrevious()) {
					Layer l = iter.previous();
					AffineTransform position = new AffineTransform(fold);
					position.concatenate(l.getPosition());
					Layer folded = new Layer(l.getSheet(),
					                         position,
					                         l.getOrientation().inverse());
					foldedStack.add(folded);
				}
				iter = null;
				stack.layers.addAll(foldedStack);
			} else if (direction == Direction.OVER) {
				// Iterate forward, place new Layers to the top of Stack
				for (Layer l : stack.layers) {
					AffineTransform position = new AffineTransform(fold);
					position.concatenate(l.getPosition());
					Layer folded = new Layer(l.getSheet(),
					                         position,
					                         l.getOrientation().inverse());
					foldedStack.add(0, folded);
				}
				stack.layers.addAll(0, foldedStack);
			} else {
				assert false : direction;
			}
		}
		
		/**
		 * Specifies the direction of folding.
		 * The Stack is assumed to be fixed in the origin and the newly
		 * created Layers can be placed either to top or to bottom.
		 * @author Singon
		 */
		public static enum Direction {
			/**
			 * The origin stays in position and Layers are folded on top.
			 */
			OVER,
			/**
			 * The origin stays in position and Layers are folded to bottom.
			 */
			UNDER;
		}
	}
	
	/**
	 * Turns this Stack over so that bottom becomes top and for each Layer,
	 * what was front side now becomes the back side.
	 * @author Singon
	 *
	 */
	public static class Flip implements Manipulation {

		/** The axis of flip. */
		private final Line axis;
		
		/**
		 * Constructs a new Flip object which flips the Stack about the
		 * given axis of rotation.
		 */
		public Flip(Line axis) {
			this.axis = axis;
		}
		
		/**
		 * Returns a new instance which flips the stack parallel to y-axis.
		 * @param width Reference width of the current extents of the stack.
		 * The axis will be placed into half this width in order to keep
		 * an area of this width (with one corner in origin) within its
		 * original extents.
		 * @return
		 */
		public static Flip horizontal(double width) {
			Line axis = new Line(new Point(width/2, 0), Math.PI/2);
			return new Flip(axis);
		}
		
		/**
		 * Returns a new instance which flips the stack parallel to x-axis.
		 * @param height Reference height of the current extents of the stack.
		 * The axis will be placed into half this height in order to keep
		 * an area of this height (with one corner in origin) within its
		 * original extents.
		 * @return
		 */
		public static Flip vertical(double height) {
			Line axis = new Line(new Point(0, height/2), 0);
			return new Flip(axis);
		}
		
		@Override
		public void manipulate(Stack stack) {
			// The original Layers stored in a new list
			List<Layer> orig = new ArrayList<>(stack.layers);
			AffineTransform flip = Transformations.mirror(axis);
			// Reverse the order of the layers, changing their orientation
			stack.layers.clear();
			ListIterator<Layer> iter = orig.listIterator(orig.size());
			while(iter.hasPrevious()) {
				Layer l = iter.previous();
				AffineTransform position = l.getPosition();
				position.concatenate(flip);
				Orientation ornt = l.getOrientation().inverse();
				Sheet sheet = l.getSheet();
				stack.layers.add(new Layer(sheet, position, ornt));
			}
		}
		
	}
	
	/** Represents the top or bottom of the Stack */
	public static enum Placement {
		TOP, BOTTOM;
	}
}
