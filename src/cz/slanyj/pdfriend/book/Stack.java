package cz.slanyj.pdfriend.book;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a vertical stack of Fields.
 * @author Singon
 *
 */
public class Stack {

	/**
	 * The list of Sheet Fields represented by this Stack. The fields are
	 * numbered from bottom to top, ie the lowest one is 0.
	 */
	private final List<Field> fields;

	public Stack() {
		fields = new LinkedList<>();
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
	
	/** Puts the contents of every Field into its corresponding Sheet.*/
	public void placeFields() {
		for (Field f : fields) {
			f.placeLeaves();
		}
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
			} else if (placement == Placement.BOTTOM) {
				stack.fields.addAll(0, joined);
			}
		}

		/** Represents the top or bottom of the Stack */
		public static enum Placement {
			TOP, BOTTOM;
		}
	}
}
