package cz.slanyj.pdfriend.book;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.SourcePage;
import cz.slanyj.pdfriend.book.FlipDirection;

/**
 * A single Leaf of the finished document. Consists of two Pages,
 * ie. recto (the odd-numbered one) and verso (the even-numbered one).
 * The Pages are considered to be of the same dimensions as the Leaf.
 * 
 * @author Singon
 *
 */
public class Leaf {

	/** The leaf width (x-direction) */
	private final double width;
	/** The leaf height (y-direction) */
	private final double height;
	/** The leading page, ie. odd-numbered */
	private final Page recto;
	/** The trailing page, ie. even-numbered */
	private final Page verso;
	/**
	 * The flip direction (how the verso is oriented with respect to recto).
	 * The default is flip around y-axis, ie. the top and bottom edges of
	 * recto correspond to top and bottom edges of verso, respectively;
	 * assuming the y-axis is taken as vertical.
	 */
	private FlipDirection flipDirection = FlipDirection.AROUND_Y;
	
	/**
	 * The Page on the front surface ("up") of the Sheet.
	 * The default is recto in front.
	 */
	private Orientation orientation = Orientation.RECTO_UP;
	/**
	 * The position of this Leaf on the front of the Sheet as represented
	 * by a transformation matrix.
	 * Specfically, this matrix moves a page from its initial position
	 * with lower left corner in [0, 0] into the desired position of the
	 * front page.
	 */
	private AffineTransform frontPosition;
	/**
	 * <p>
	 * The position of this Leaf on the back of the Sheet as represented by
	 * a transformation matrix. Exactly speaking, this matrix moves a page
	 * from its initial position with lower left corner in [0, 0] into a
	 * position as described in detail below:
	 * </p>
	 * <p>
	 * Because this class lacks any notion of the final Sheet dimensions,
	 * this position cannot be expressed directly in the coordinate system
	 * of the back side. This is due to the fact that the origins of the
	 * front-side and back-side coordinate systems of the Sheet are not
	 * aligned but instead shifted relative to each other by the Sheet width
	 * or height.
	 * </p>
	 * <p>
	 * In order to avoid this issue, the position for the back page is not
	 * given as viewed from the <em>back</em> side, but as viewed from the
	 * <em>front</em> side instead, as if the leaf was transparent. This
	 * implies the page is mirrored, in addition to any translation and
	 * rotation. The Sheet is then required to mirror the coordinate system
	 * before imposing this page. The direction of this flip is determined
	 * by the {@code Sheet.flipDirection} field.
	 * </p>
	 */
	private AffineTransform backPosition;
	/**
	 * Indicates whether the front- and back-position matrices are up to
	 * date with reference position.
	 */
	private boolean positionValid = false;
	/**
	 * The position matrix which is used to calculate the front and back
	 * position matrices. This is either the frontPosition or backPosition,
	 * as specified by {@code referenceIsFront} field.
	 */
	private AffineTransform referencePosition;
	/**
	 * Specifies whether the {@code position} matrix refers to the front
	 * side or back side.
	 */
	private boolean referenceIsFront = true;
		
	
	/**
	 * Constructs a new Leaf of the given dimensions.
	 * @param width
	 * @param height
	 */
	public Leaf(double width, double height) {
		this.recto = new Page(width, height);
		this.verso = new Page(width, height);
		this.width = width;
		this.height = height;
	}
	
	
	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	public FlipDirection getFlipDirection() {
		return flipDirection;
	}

	public void setFlipDirection(FlipDirection flip) {
		positionValid = false;
		this.flipDirection = flip;
	}

	/**
	 * Gets the position of this Leaf on the front side of the Sheet
	 * as a transformation matrix.
	 * @return The internal AffineTransform object.
	 */
	public AffineTransform getFrontPosition() {
		if (positionValid) {
			return frontPosition;
		} else {
			return updatePosition()[0];
		}
	}
	
	/**
	 * Gets the position of this Leaf on the back side of the Sheet
	 * as a transformation matrix.
	 * @return The internal AffineTransform object.
	 */
	public AffineTransform getBackPosition() {
		if (positionValid) {
			return backPosition;
		} else {
			return updatePosition()[1];
		}
	}
	
	/**
	 * Updates the frontPosition and backPosition fields to reflect the
	 * current referencePosition, taking into account whether that refers
	 * to the front or the back (the {@code referenceIsFront} field).
	 */
	private AffineTransform[] updatePosition() {
		if (referenceIsFront) {
			return updateFromFrontPosition(referencePosition);
		} else {
			return updateFromBackPosition(referencePosition);
		}
	}

	/**
	 * Moves the leaf so that its front position is the one specified by the
	 * transform. The front page before transformation has its lower left
	 * corner at [0, 0].
	 * @param transform
	 */
	public void setAsFrontPosition(AffineTransform transform) {
		positionValid = false;
		referencePosition = transform;
		referenceIsFront = true;
	}
	/**
	 * Moves the leaf so that its front position is the one specified.
	 */
	public void setAsFrontPosition(Position position) {
		AffineTransform at = position.asMatrix();
		at.translate(-width/2, -height/2);
		setAsFrontPosition(at);
	}
	
	/**
	 * Moves the leaf so that its back position is the one specified by the
	 * transform. The back page before transformation has its lower left
	 * corner at [0, 0].
	 * @param transform
	 */
	public void setAsBackPosition(AffineTransform transform) {
		positionValid = false;
		referencePosition = transform;
		referenceIsFront = false;
	}
	/**
	 * Moves the leaf so that its back position is the one specified.
	 */
	public void setAsBackPosition(Position position) {
		AffineTransform at = position.asMatrix();
		at.translate(-width/2, -height/2);
		setAsBackPosition(at);
	}
	
	/**
	 * Updates the front matrix directly to the given matrix and updates the
	 * back position matrix accordingly so that the back page occupies the
	 * same extents as the front page, given the Leaf dimensions.
	 * @param transform A transformation beginning with a page with lower
	 * left corner in origin.
	 * @return Updated transformation matrices in array
	 * {front-position, back-position}.
	 */
	private AffineTransform[] updateFromFrontPosition(AffineTransform transform) {
		AffineTransform front = new AffineTransform(transform);
		frontPosition = front;
		
		AffineTransform back = new AffineTransform(transform);
		// Move the leaf back to its original extents
		back.translate(width/2, height/2);
		// Mirror the page (will be mirored again by Sheet)
		back.concatenate(flipDirection.getBackOrientation());
		// Move the center of the leaf to origin
		back.translate(-width/2, -height/2);
		backPosition = back;
		
		positionValid = true;
		return new AffineTransform[]{front, back};
	}
	
	/**
	 * Updates the back matrix directly to the given matrix and updates the
	 * front position matrix accordingly so that the front page occupies the
	 * same extents as the back page, given the Leaf dimensions.
	 * @param transform A transformation beginning with a page with lower
	 * left corner in origin.
	 * @return Updated transformation matrices in array
	 * {front-position, back-position}.
	 */
	private AffineTransform[] updateFromBackPosition(AffineTransform transform) {
		AffineTransform back = new AffineTransform(transform);
		backPosition = back;
		
		AffineTransform front = new AffineTransform(transform);
		// Move the leaf back to its original extents
		front.translate(width/2, height/2);
		// Mirror the page (will be mirored again by Sheet)
		// In theory, one should use the inverse of the matrix used in
		// updateFromFrontPosition, but it is assumed to be self-inverse.
		front.concatenate(flipDirection.getBackOrientation());
		// Move the center of the leaf to origin
		front.translate(-width/2, -height/2);
		frontPosition = front;
		
		positionValid = true;
		return new AffineTransform[]{front, back};
	}
	
	/**
	 * Sets the source pages for the recto and verso of this Leaf.
	 * @param recto
	 * @param verso
	 */
	public void setContent(SourcePage recto, SourcePage verso) {
		this.recto.setSource(recto);
		this.verso.setSource(verso);
	}

	/**
	 * Places the form XObject representing the upper page of this Leaf
	 * into the given content stream.
	 * Which page is upper and bottom is determined from the orientation
	 * property. If it is RECTO_UP, the recto is placed, verso otherwise.
	 * @param sheetContent The content stream of the target Sheet side.
	 * @param layerUtility The layer utility of the target Sheet.
	 * @throws IOException 
	 */
	public void imposeFront(PDPageContentStream sheetContent,
		                    LayerUtility layerUtility) throws IOException {
		if (orientation == Orientation.RECTO_UP) {
			imposeIfNotEmpty(sheetContent, layerUtility, recto, false);
		} else if (orientation == Orientation.VERSO_UP) {
			imposeIfNotEmpty(sheetContent, layerUtility, verso, false);
		} else {
			throw new IllegalStateException("Leaf orientation has not been set correctly.");
		}
	}
	
	/**
	 * Places the form XObject representing the bottom page of this Leaf
	 * into the given content stream.
	 * Which page is upper and bottom is determined from the orientation
	 * property. If it is RECTO_UP, the verso is placed, recto otherwise.
	 * This method flips the page vertically before moving it and
	 * the calling Sheet is expected to mirror the final placement again.
	 * @param sheetContent The content stream of the target Sheet side.
	 * @param layerUtility The layer utility of the target Sheet.
	 * @throws IOException 
	 */
	public void imposeBack(PDPageContentStream sheetContent,
		                    LayerUtility layerUtility) throws IOException {
		if (orientation == Orientation.RECTO_UP) {
			imposeIfNotEmpty(sheetContent, layerUtility, verso, true);
		} else if (orientation == Orientation.VERSO_UP) {
			imposeIfNotEmpty(sheetContent, layerUtility, recto, true);
		} else {
			throw new IllegalStateException("Leaf orientation has not been set correctly.");
		}
	}
		
	/**
	 * Places the form XObject representing the given page into the given
	 * content stream.
	 * @param sheetContent The content stream of the target Sheet side.
	 * @param layerUtility The layer utility of the target Sheet.
	 * @param pg Either the recto or verso of this leaf.
	 * @param mirror Mirror the page before transforming. Used for back pages.
	 * @throws IOException 
	 */
	private void impose(PDPageContentStream sheetContent,
	                    LayerUtility layerUtility,
	                    Page pg,
	                    boolean isBack) throws IOException {
		PDDocument parent = pg.getSource().getDoc();
		PDPage page = pg.getSource().getPage();
		PDFormXObject form = layerUtility.importPageAsForm(parent, page);
		
		AffineTransform trMatrix = !isBack ? getFrontPosition() : getBackPosition();
		
		sheetContent.saveGraphicsState();
		sheetContent.transform(new Matrix(trMatrix));
		sheetContent.drawForm(form);
		sheetContent.restoreGraphicsState();
	}
	/**
	 * Places the form XObject representing the given page into the given
	 * content stream.
	 * @param sheetContent The content stream of the target Sheet side.
	 * @param layerUtility The layer utility of the target Sheet.
	 * @param pg Either the recto or verso of this leaf.
	 * @param mirror Mirror the page before transforming. Used for back pages.
	 * @throws IOException 
	 */
	private void imposeIfNotEmpty(PDPageContentStream sheetContent,
	                              LayerUtility layerUtility,
	                              Page pg,
	                              boolean isBack) throws IOException {
		try {
			impose(sheetContent, layerUtility, pg, isBack);
		} catch (NullPointerException e) {
			int page = pg.getNumber();
			Log.info("Page %d is empty, skipping", page);
		}
	}
	
	/**
	 * Placement of the Leaf on the Sheet: Either recto up or down.
	 * This will affect which Page of this Leaf gets printed on the front
	 * and back of the parent Sheet.
	 * @author Singon
	 *
	 */
	public static enum Orientation {
		/** Recto is on the front surface, verso on back */
		RECTO_UP {
			@Override public Orientation inverse() {return VERSO_UP;}
		},
		/** Verso is on the front surface, recto on back */
		VERSO_UP {
			@Override public Orientation inverse() {return RECTO_UP;}
		};
		
		/** Returns the inverse Orientation */
		public abstract Orientation inverse();
	}
	
	/**
	 * A simple class representing the position a Leaf on a Sheet.
	 * This represents the position of the center and the rotation of
	 * the page in the plane of the Sheet.
	 * This class is immutable
	 * @author Singon
	 *
	 */
	public static class Position {
		
		/**
		 * The x-coordinate of the center of the Leaf on the front
		 * side of the Sheet.
		 */
		private final double xPosition;
		/**
		 * The y-coordinate of the center of the Leaf on the front
		 * side of the Sheet.
		 */
		private final double yPosition;
		/**
		 * The rotation of the Leaf measured as the angle in radians from
		 * the y-axis of the Sheet to the side of the Leaf, with positive
		 * being the counter-clockwise direction when viewed from the front
		 * of the Sheet. An angle of 0 means the Leaf is upright (ie. the
		 * text is horizontal).
		 */
		private final double rotation;
		/**
		 * The position as a transformation matrix required to bring
		 * a page from a position centered on origin to the position
		 * represented by this Position object.
		 */
		private final AffineTransform matrix;
				

		/**
		 * Constructs a new Position object representing a page centered at
		 * [x, y] and rotated by an angle.
		 */
		public Position(double x, double y, double rot) {
			xPosition = x;
			yPosition = y;
			rotation = rot;
			
			AffineTransform at;
			at = new AffineTransform();
			/*
			 * The transformations in reverse order
			 * (ie. their matrices from left to right):
			 */
			// Move to final position
			at.translate(xPosition, yPosition);
			// Apply rotation
			at.rotate(rotation);
			matrix = at;
		}
		
		/**
		 * Constructs a new Position object representing a page centered
		 * at [x, y] without rotation.
		 */
		public Position(double x, double y) {
			this(x, y, 0);
		}
		
		/**
		 * Returns the current x-position of the center of this leaf as given
		 * by its transformation matrix.
		 * @return
		 */
		public final double getXPosition() {
			return xPosition;
		}

		public final double getYPosition() {
			return yPosition;
		}

		public final double getRotation() {
			return rotation;
		}
		
		/**
		 * Returns the position as a transformation matrix required to bring
		 * a page from a position centered on origin to the position
		 * represented by this Position object.
		 */
		public AffineTransform asMatrix() {
			return new AffineTransform(matrix);
		}
	}
}
