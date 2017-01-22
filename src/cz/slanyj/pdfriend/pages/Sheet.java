package cz.slanyj.pdfriend.pages;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import cz.slanyj.pdfriend.SourcePage;

/**
 * A single Sheet of the finished document. Consists of two pages,
 * ie recto (the odd-numbered one) and verso (the even-numbered one).
 * The pages can be of different dimensions, but their centers will
 * be aligned.
 * @author Sorondil
 *
 */
public class Sheet {

	/** The sheet width (x-direction) */
	private final double width;
	/** The sheet height (y-direction) */
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
	 * The x-coordinate of the center of this Sheet on the front side
	 * of the Paper.
	 */
	private double xPosition;
	/**
	 * The y-coordinate of the center of this Sheet on the front side
	 * of the Paper.
	 */
	private double yPosition;
	/**
	 * The rotation of this Sheet measured as the angle in radians from the
	 * y-axis of the Paper to the side of this Sheet, with positive being
	 * the counter-clockwise direction when viewed from the front of the Paper.
	 * An angle of 0 means the Sheet is upright (ie. the text is horizontal).
	 */
	private double rotation;
	/**
	 * The Page on the front surface ("up") of the Paper.
	 * The default is recto in front.
	 */
	private Orientation orientation = Orientation.RECTO_UP;
	/**
	 * The position of this Sheet on the front of the Paper as represented
	 * by a transformation matrix.
	 */
	private AffineTransform frontPosition;
	/**
	 * The position of this Sheet on the front of the Paper as represented
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
	 * Constructs a new Sheet of the given dimensions.
	 * @param width
	 * @param height
	 */
	public Sheet(double width, double height) {
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
	 * Gets the position of this Sheet on the front side of the Paper
	 * as a transformation matrix.
	 * @return An AffineTransform object representing the current values
	 * of x and y positions, rotation and orientation.
	 */
	public AffineTransform getFrontPosition() {
		if (positionValid) {
			return frontPosition;
		} else {
			return updatePosition()[0];
		}
	}
	
	/**
	 * Gets the position of this Sheet on the front side of the Paper
	 * as a transformation matrix.
	 * @return An AffineTransform object representing the current values
	 * of x and y positions, rotation and orientation.
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
	 * of x and y positions, rotation and orientation.
	 */
	private AffineTransform calculateFrontPositionMatrix() {
		AffineTransform matrix = new AffineTransform();
		// The transformations in the reverse order:
		// Move to final position
		matrix.translate(xPosition, yPosition);
		// Apply rotation
		matrix.rotate(rotation);
		// Move the center to origin
		matrix.translate(-width/2, -height/2);
		return matrix;
	}
	/**
	 * Returns the position matrix calculated from the current values
	 * of x and y positions, rotation and orientation.
	 */
	private AffineTransform calculateBackPositionMatrix() {
		AffineTransform matrix = new AffineTransform();
		// The transformations in the reverse order:
		// Move to final position
		matrix.translate(xPosition, yPosition);
		// Apply rotation
		matrix.rotate(rotation);
		// Mirror the page (will be mirored again by Paper)
		matrix.concatenate(flipDirection.getBackOrientation());
		// Move the center to origin
		matrix.translate(-width/2, -height/2);
		return matrix;
	}
	
	/**
	 * Sets the source pages for the recto and verso of this sheet.
	 * @param recto
	 * @param verso
	 */
	public void setContent(SourcePage recto, SourcePage verso) {
		this.recto.setSource(recto);
		this.verso.setSource(verso);
	}

	/**
	 * Places the form XObject representing the upper page of this Sheet
	 * into the given content stream.
	 * Which page is upper and bottom is determined from the orientation
	 * property. If it is RECTO_UP, the recto is placed, verso otherwise.
	 * @param paperContent The content stream of the target Paper page.
	 * @param layerUtility The layer utility of the target Paper.
	 * @throws IOException 
	 */
	public void imposeFront(PDPageContentStream paperContent,
		                    LayerUtility layerUtility) throws IOException {
		if (orientation == Orientation.RECTO_UP) {
			impose(paperContent, layerUtility, recto, false);
		} else if (orientation == Orientation.VERSO_UP) {
			impose(paperContent, layerUtility, verso, false);
		} else {
			throw new IllegalStateException("Sheet orientation has not been set correctly.");
		}
	}
	
	/**
	 * Places the form XObject representing the bottom page of this Sheet
	 * into the given content stream.
	 * Which page is upper and bottom is determined from the orientation
	 * property. If it is RECTO_UP, the verso is placed, recto otherwise.
	 * This method flips the page vertically before moving it and
	 * the calling Paper is expected to mirror the final placement again.
	 * @param paperContent The content stream of the target Paper page.
	 * @param layerUtility The layer utility of the target Paper.
	 * @throws IOException 
	 */
	public void imposeBack(PDPageContentStream paperContent,
		                    LayerUtility layerUtility) throws IOException {
		if (orientation == Orientation.RECTO_UP) {
			impose(paperContent, layerUtility, verso, true);
		} else if (orientation == Orientation.VERSO_UP) {
			impose(paperContent, layerUtility, recto, true);
		} else {
			throw new IllegalStateException("Sheet orientation has not been set correctly.");
		}
	}
		
	/**
	 * Places the form XObject representing the given page into the given
	 * content stream.
	 * The content stream should be from the same document as the
	 * @param paperContent The content stream of the target Paper.
	 * @param layerUtility The layer utility of the target Paper.
	 * @param pg Either the recto or verso of this sheet.
	 * @param mirror Mirror the page before transforming. Used for back pages.
	 * @throws IOException 
	 */
	private void impose(PDPageContentStream paperContent,
	                    LayerUtility layerUtility,
	                    Page pg,
	                    boolean isBack) throws IOException {
		PDDocument parent = pg.getSource().getDoc();
		PDPage page = pg.getSource().getPage();
		PDFormXObject form = layerUtility.importPageAsForm(parent, page);
		
		AffineTransform trMatrix = !isBack ? getFrontPosition() : getBackPosition();
		
		paperContent.saveGraphicsState();
		paperContent.transform(new Matrix(trMatrix));
		paperContent.drawForm(form);
		paperContent.restoreGraphicsState();
	}
	
	public static enum Orientation {
		/** Recto is on the front page, verso on back */
		RECTO_UP,
		/** Verso is on the front surface, recto on back */
		VERSO_UP;
	}
	
	public static enum FlipDirection {
		/** Flipped around x-axis */
		AROUND_X(1, -1),
		/** Flipped around y-axis */
		AROUND_Y(-1, 1);
		
		private final AffineTransform backOrientation;
		
		private FlipDirection(double xScale, double yScale) {
			backOrientation = AffineTransform.getScaleInstance(xScale, yScale);
		}
		
		public AffineTransform getBackOrientation() {
			return backOrientation;
		}
	}
	
	/**
	 * Returns a new page as a child of given document. The page is not
	 * added to the document automatically.
	 * @param doc
	 * @return
	 * @throws IOException
	 */
	/*public PDPage printRecto(PDDocument doc) throws IOException {
		PDPage sheet = new PDPage();
		sheet.setMediaBox(new PDRectangle((float) width, (float) height));
		PDPageContentStream content = new PDPageContentStream(doc, sheet);
		LayerUtility lu = new LayerUtility(doc);
		
	}*/
	
	/*public PDPage printVerso(PDDocument doc) {
		return null;
	}*/
}
