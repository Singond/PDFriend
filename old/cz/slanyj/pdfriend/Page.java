package cz.slanyj.pdfriend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class Page extends AbstractPage {
	
	/** The number of the page in the source document. */
	private int number = 1;
	/** The coordinates of the center of mainbox. */
	private double[] position = new double[2];
	/** The clockwise rotation of the page. */
	private double rotation = 0;
	/** The scale of the page. */
	private double scale = 1;
	/** The placement of this page within the sheet given by an affine transformation. */
	private AffineTransform placement = new AffineTransform();
	public AffineTransform getPlacement() {return placement;}
	
	public static final double ROTATED_UP = 0;
	public static final double ROTATED_RIGHT = (Math.PI/2);
	public static final double ROTATED_DOWN = Math.PI;
	public static final double ROTATED_LEFT = 3*Math.PI/2;
	
	public String getName() {return "Page";}
	
	/**
	 * Constructs a new page with the given corners and rotation, in the given sheet.
	 * @param s The sheet to be placed into.
	 * @param width Page width.
	 * @param height Page height.
	 * @param x X-coordinate of the center.
	 * @param y Y-coordinate of the center.
	 * @param rot Clockwise rotation in radians.
	 */
/*	public Page(double width, double height, double x, double y, double rot) {
//		this.parentSheet = s;
	}
	
	public Page(double width, double height) {
		this(width, height, 0, 0, 0);
	}*/
		
	public Page() {
		super();
		setPosition(new double[]{getWidth()/2, getHeight()/2});
	}
	
	public Page(PDPage p) {
		super(p);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void setBoxes() {
		super.setBoxes();
		calculateTransformationMatrix();
	}
	
	public void setPosition(double[] p) {
		if (p.length != 2) throw new IllegalArgumentException("Invalid number of parameters in position definition.");
		position = p;
		calculateTransformationMatrix();
	}
	public void setPosition(double px, double py) {
		setPosition(new double[]{px, py});
	}
	public void setRotation(double rot) {
		rotation = rot;
		calculateTransformationMatrix();
	}
	public void setScale(double scale) {
		this.scale = scale;
		calculateTransformationMatrix();
	}
	
	/**
	 * Updates the transformation matrix which controls positioning this page on a sheet,
	 * based on the current values of position, rotation and scale.
	 */
	private void calculateTransformationMatrix() {
		placement = AffineTransform.getTranslateInstance(position[0], position[1]);
		placement.rotate(-rotation);		// Meh, I used opposite sign convention…
		placement.scale(scale, scale);
		placement.translate(-getWidth()/2, -getHeight()/2);
	}
	
	/**
	 * Draws itself using the given graphics object.
	 * @param g The Graphics2D object to be used in drawing.
	 * @param view The transform to be used.
	 */
	@Override
	public void draw(Graphics2D g, AffineTransform view) {
		Color orig = g.getColor();
		Stroke origStroke = g.getStroke();
		AffineTransform pageView = new AffineTransform(view);
		pageView.concatenate(placement);

		// Draw media box
		Shape media = pageView.createTransformedShape(Util.drawable(getMediaBox()));
		g.setColor(DisplayOptions.PAGE_MEDIABOX_COLOR);
		g.fill(media);
		g.setColor(DisplayOptions.PAGE_MEDIABOX_BORDER_COLOR);
		g.setStroke(DisplayOptions.PAGE_MEDIABOX_BORDER_STROKE);
		g.draw(media);
		
		// Draw bleed box
		Shape bleed = pageView.createTransformedShape(Util.drawable(getBleedBox()));
		g.setColor(DisplayOptions.PAGE_BLEED_COLOR);
		g.fill(bleed);
		g.setColor(DisplayOptions.PAGE_BLEED_BORDER_COLOR);
		g.setStroke(DisplayOptions.PAGE_BLEED_BORDER_STROKE);
		g.draw(bleed);
		
		// Draw trim box
		Shape trim = pageView.createTransformedShape(Util.drawable(getTrimBox()));
		g.setColor(DisplayOptions.PAGE_COLOR);
		g.fill(trim);
		g.setColor(DisplayOptions.PAGE_BORDER_COLOR);
		g.setStroke(DisplayOptions.PAGE_BORDER_STROKE);
		g.draw(trim);
	}
	
	/** Places the Page into the Sheet PDF. */
	
	public static class Real extends Page {
		/** The actual PDF page represented by this rectangle. */
//		private PDPage page;
		/** The Sheet this page is to be placed into. */
//		private final Sheet.Real sheet;
		
/*		Real(Sheet.Real s) {
//			super(0,0);
//			this.sheet = s;
		}
		Real(PDPage p) {
			super(p);
		}
		
		void impose() {
			
		}*/
	}
	
	public static class Template extends Page {
		
		/** The number of the page in the template. */
//		private int pageNo;
		
	}
}
