package cz.slanyj.pdfriend.pages;

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
	 * Constructs a new Sheet given a recto and verso Page.
	 * @param recto
	 * @param verso
	 */
	public Sheet(Page recto, Page verso) {
		this.recto = recto;
		this.verso = verso;
		this.height = Math.max(recto.getHeight(), verso.getHeight());
		this.width = Math.max(recto.getWidth(), verso.getWidth());
	}
	
	/** The x-coordinate of the center of this Sheet on the Paper. */
	private double xPosition;
	/** The y-coordinate of the center of this Sheet on the Paper. */
	private double yPosition;
	/**
	 * The rotation of this Sheet measured as the angle in radians from the
	 * y-axis of the Paper to the side of this Sheet.
	 * An angle of 0 means the Sheet is upright (ie. the text is horizontal).
	 */
	private double rotation;
	/** The Page on the front surface of the Paper */
	private Orientation orientation;
	
	public static enum Orientation {
		/** Recto is on the front page, verso on back */
		RECTO_UP,
		/** Verso is on the front surface, recto on back */
		VERSO_UP;
	}
}
