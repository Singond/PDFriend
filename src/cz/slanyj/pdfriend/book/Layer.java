package cz.slanyj.pdfriend.book;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;

/**
 * An area on a Sheet with a specific position and own coordinate system,
 * meant to represent a single layer of a possibly folded stack of Sheets.
 * 
 * 
 * @author Singon
 *
 */
public class Layer {

	/** Parent Sheet this Layer belongs to. */
	private final Sheet sheet;
	/**
	 * Position of this Layer on the parent Sheet, as seen from the front
	 * of the Sheet, represented by a matrix.
	 */
	private final AffineTransform position;
	/*
	 * If mirroring the coordinates was considered swapping the front and
	 * back sides, the Orientation could actually be easily calculated from
	 * the transformation's determinant.
	 * However, this convention was not adopted and orientation is thus
	 * specified as a separate value. This allows full freedom in positio-
	 * ning Layers on the Sheet, allowing them to be placed in mirrored
	 * position, as useless as it might seem.
	 */
	/** Orientation of this Layer */
	private final Orientation orientation;
	
	/**
	 * A list of all Leaves positioned on this layer, arranged in ascending
	 * order.
	 */
	private final List<Leaf> leaves;
	
	
	/**
	 * Constructs a new Layer in the given Sheet, with the specified position
	 * and orientation.
	 * @param parent The parent sheet of this Layer
	 * @param pos The transformation matrix representing the position of this
	 * Layer on the Sheet, when viewed from the front side of the Sheet. This
	 * transformation should be composed of only rotations and translations,
	 * otherwise the page shape will be deformed.
	 * @param orient The orientation of this Layer, ie. either front on front
	 * (= positive) or front on back (= negative). In order not to obtain
	 * mirrored pages, the sign of the orientation should match the sign of
	 * the determinant of the {@code pos} matrix.
	 */
	public Layer(Sheet parent, AffineTransform pos, Orientation orient) {
		sheet = parent;
		position = pos;
		orientation = orient;
		leaves = SetUniqueList.setUniqueList(new LinkedList<Leaf>());
	}
	/**
	 * A copy constructor.
	 * Creates a new Layer which is a copy of the original with the same
	 * position and orientation. The new Layer is created in the Sheet
	 * given in argument. This does not copy any Leaves placed in the Layer.
	 * @param original The Layer to be copied.
	 * @param parent The parent Sheet of the newly created Layer.
	 * @return A new Layer object with the same position and orientation
	 * as the original.
	 */
	public Layer(Layer original, Sheet parent) {
		this(parent, original.position, original.orientation);
	}
	
	
	/**
	 * Returns the position of this Layer.
	 * @return A copy of the internal transformation matrix.
	 */
	public AffineTransform getPosition() {
		return new AffineTransform(position);
	}

	public Orientation getOrientation() {
		return orientation;
	}

	/** Returns the Sheet this Layer belongs to. */
	public Sheet getSheet() {
		return sheet;
	}

	/**
	 * @return A copy of the list of Leaves.
	 */
	public List<Leaf> getLeaves() {
		return new ArrayList<>(leaves);
	}
	
	/**
	 * Adds a Leaf to this Layer.
	 */
	public void addLeaf(Leaf leaf) {
		leaves.add(leaf);
	}
	
	/**
	 * Checks whether a Leaf would lie in the parent Sheet of this Layer,
	 * were it placed into this Layer at its current position.
	 */
	public boolean isInSheet(Leaf leaf) {
		// TODO Implement!
		return true;
	}
	
	/**
	 * Places all Leaves in this Layer into the parent Sheet.
	 */
	public void placeLeaves() {
		if (orientation == Orientation.POSITIVE) {
			for (Leaf l : leaves) {
				AffineTransform transform = new AffineTransform(position);
				transform.concatenate(l.getFrontPosition());
				l.setAsFrontPosition(transform);
				sheet.addLeaf(l);
			}
		} else if (orientation == Orientation.NEGATIVE) {
			for (Leaf l : leaves) {
				AffineTransform transform = new AffineTransform(position);
				transform.concatenate(l.getFrontPosition());
				l.setAsBackPosition(transform);
				l.setOrientation(l.getOrientation().inverse());
				sheet.addLeaf(l);
			}
		} else {
			throw new IllegalArgumentException("Wrong orientation specified for Layer");
		}
	}

	/**
	 * Placement of the Layer on the Sheet: Either the front corresponds
	 * to the front of the Sheet, or to the back.
	 * This will affect the orientation of Pages from this Layer once they
	 * are laid out onto the Sheet.
	 * @author Singon
	 *
	 */
	public static enum Orientation {
		/** Front of the Layer lies on the front of the Sheet */
		POSITIVE {
			@Override public Orientation inverse() {return NEGATIVE;}
		},
		/** Front of the Layer lies on the back of the Sheet */
		NEGATIVE {
			@Override public Orientation inverse() {return POSITIVE;}
		};
		
		/** Returns the inverse of this Orientation */
		public abstract Orientation inverse();
	}
}
