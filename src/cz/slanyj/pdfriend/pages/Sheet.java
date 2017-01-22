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
	 */
	private Orientation orientation;
	/**
	 * The position of this Sheet on the front of the Paper as represented
	 * by a transformation matrix.
	 */
	private AffineTransform frontPosition;
	/**
	 * A flag to indicate the validity state of the frontPosition matrix.
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
	
	/**
	 * Gets the position of this Sheet on the front side of the Paper
	 * as a transformation matrix.
	 * @return An AffineTransform object representing the current values
	 * of x and y positions, rotation and orientation.
	 */
	public AffineTransform getPosition() {
		if (positionValid) {
			return frontPosition;
		} else {
			AffineTransform newMatrix = calculatePositionMatrix();
			frontPosition = newMatrix;
			positionValid = true;
			return newMatrix;
		}
	}

	/**
	 * Returns the position matrix calculated from the current values
	 * of x and y positions, rotation and orientation.
	 */
	private AffineTransform calculatePositionMatrix() {
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
	 * Which page is upper and lower is determined from the orientation
	 * property. If it is RECTO_UP, the recto is placed, verso otherwise.
	 * @param paperContent The content stream of the target Paper page.
	 * @param layerUtility The layer utility of the target Paper.
	 * @throws IOException 
	 */
	public void imposeFront(PDPageContentStream paperContent,
		                    LayerUtility layerUtility) throws IOException {
		if (orientation == Orientation.RECTO_UP) {
			imposeRecto(paperContent, layerUtility);
		} else if (orientation == Orientation.RECTO_UP) {
			imposeVerso(paperContent, layerUtility);
		} else {
			throw new IllegalStateException("Sheet orientation has not been set correctly.");
		}
	}
	
	/**
	 * Places the form XObject representing the recto page into the given
	 * content stream.
	 * @param paperContent The content stream of the target Paper page.
	 * @param layerUtility The layer utility of the target Paper.
	 * @throws IOException 
	 */
	public void imposeRecto(PDPageContentStream paperContent,
		                    LayerUtility layerUtility) throws IOException {
		impose(paperContent, layerUtility, recto);
	}
	
	/**
	 * Places the form XObject representing the verso page into the given
	 * content stream.
	 * @param paperContent The content stream of the target Paper page.
	 * @param layerUtility The layer utility of the target Paper.
	 * @throws IOException 
	 */
	public void imposeVerso(PDPageContentStream paperContent,
		                    LayerUtility layerUtility) throws IOException {
		impose(paperContent, layerUtility, verso);
	}
	
	/**
	 * Places the form XObject representing the given page into the given
	 * content stream.
	 * The content stream should be from the same document as the
	 * @param paperContent The content stream of the target Paper.
	 * @param layerUtility The layer utility of the target Paper.
	 * @param Either the recto or verso of this sheet.
	 * @throws IOException 
	 */
	private void impose(PDPageContentStream paperContent,
	                    LayerUtility layerUtility,
	                    Page pg) throws IOException {
		PDDocument parent = pg.getSource().getDoc();
		PDPage page = pg.getSource().getPage();
		PDFormXObject form = layerUtility.importPageAsForm(parent, page);
		
		paperContent.saveGraphicsState();
		paperContent.transform(new Matrix(getPosition()));
		paperContent.drawForm(form);
		paperContent.restoreGraphicsState();
	}
	
	public static enum Orientation {
		/** Recto is on the front page, verso on back */
		RECTO_UP,
		/** Verso is on the front surface, recto on back */
		VERSO_UP;
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
