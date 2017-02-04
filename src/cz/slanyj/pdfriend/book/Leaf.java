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
 * ie recto (the odd-numbered one) and verso (the even-numbered one).
 * The pages can be of different dimensions, but their centers will
 * be aligned.
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
	 * The x-coordinate of the center of this Leaf on the front side
	 * of the Sheet.
	 */
	private double xPosition;
	/**
	 * The y-coordinate of the center of this Leaf on the front side
	 * of the Sheet.
	 */
	private double yPosition;
	/**
	 * The rotation of this Leaf measured as the angle in radians from the
	 * y-axis of the Sheet to the side of this Leaf, with positive being
	 * the counter-clockwise direction when viewed from the front of the Sheet.
	 * An angle of 0 means the Leaf is upright (ie. the text is horizontal).
	 */
	private double rotation;
	/**
	 * The Page on the front surface ("up") of the Sheet.
	 * The default is recto in front.
	 */
	private Orientation orientation = Orientation.RECTO_UP;
	/**
	 * The position of this Leaf on the front of the Sheet as represented
	 * by a transformation matrix.
	 */
	private AffineTransform frontPosition;
	/**
	 * The position of this Leaf on the back of the Sheet as represented
	 * by a transformation matrix.
	 */
	private AffineTransform backPosition;
	/**
	 * A flag to indicate the validity state of the frontPosition and
	 * backPosition matrices.
	 * Set to {@code false} to force matrix recalculation on its next usage.
	 */
	private boolean positionValid = false;
	
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
	
	public double getXPosition() {
		return xPosition;
	}

	public void setXPosition(double xPosition) {
		positionValid = false;
		this.xPosition = xPosition;
	}

	public double getYPosition() {
		return yPosition;
	}

	public void setYPosition(double yPosition) {
		positionValid = false;
		this.yPosition = yPosition;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		positionValid = false;
		this.rotation = rotation;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		//positionValid = false;
		this.orientation = orientation;
	}
	
	public FlipDirection getFlipDirection() {
		return flipDirection;
	}

	public void setFlipDirection(FlipDirection flipDirection) {
		positionValid = false;
		this.flipDirection = flipDirection;
	}

	/**
	 * Gets the position of this Leaf on the front side of the Sheet
	 * as a transformation matrix. Performs recalculation if any of the
	 * prerequisite values has changed.
	 * @return An AffineTransform object representing the current values
	 * of x and y positions and rotation.
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
	 * as a transformation matrix. Performs recalculation if any of the
	 * prerequisite values has changed.
	 * @return An AffineTransform object representing the current values
	 * of x and y positions, rotation and flip direction.
	 */
	public AffineTransform getBackPosition() {
		if (positionValid) {
			return backPosition;
		} else {
			return updatePosition()[1];
		}
	}

	/**
	 * Updates the front and back position matrices and returns them.
	 * @return An array [frontPosition, backPosition] with freshly
	 * calculated values.
	 */
	private AffineTransform[] updatePosition() {
		AffineTransform newFrontMatrix = calculateFrontPositionMatrix();
		frontPosition = newFrontMatrix;
		AffineTransform newBackMatrix = calculateBackPositionMatrix();
		backPosition = newBackMatrix;
		positionValid = true;
		return new AffineTransform[]{frontPosition, backPosition};
	}
	
	/**
	 * Returns the position matrix calculated from the current values
	 * of x and y positions and rotation.
	 */
	private AffineTransform calculateFrontPositionMatrix() {
		AffineTransform matrix = new AffineTransform();
		/*
		 * The transformations in reverse order
		 * (ie. their matrices from left to right):
		 */
		// Move to final position
		matrix.translate(xPosition, yPosition);
		// Apply rotation
		matrix.rotate(rotation);
		// Move the center of the leaf to origin
		matrix.translate(-width/2, -height/2);
		return matrix;
	}
	/**
	 * Returns the position matrix calculated from the current values
	 * of x and y positions, rotation and flip direction.
	 */
	private AffineTransform calculateBackPositionMatrix() {
		AffineTransform matrix = new AffineTransform();
		/*
		 * The transformations in reverse order:
		 * (ie. their matrices from left to right):
		 */
		// Move to final position
		matrix.translate(xPosition, yPosition);
		// Apply rotation
		matrix.rotate(rotation);
		// Mirror the page (will be mirored again by Sheet)
		matrix.concatenate(flipDirection.getBackOrientation());
		// Move the center of the leaf to origin
		matrix.translate(-width/2, -height/2);
		return matrix;
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
}
