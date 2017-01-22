package cz.slanyj.pdfriend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public abstract class AbstractPage extends PDPage {
	
	private double width;
	private double height;
	/**
	 * Distances of the Bleed Box from the Trim Box:
	 * top, right, bottom, left, respectively.
	 */
	private double[] bleeds = new double[]{0, 0, 0, 0};
	/**
	 * Distances of the Media Box from the Bleed Box:
	 * top, right, bottom, left, respectively.
	 */
	private double[] overbleeds = new double[]{0, 0, 0, 0};
	
	/** The box to crop the display to. */
	private Box mainBox = Box.MEDIA;
	
	
	public AbstractPage(PDPage p) {
		super(new COSDictionary(p.getCOSDictionary()));
		width = getMediaBox().getWidth();
		height = getMediaBox().getHeight();
	}
	
	public AbstractPage() {
		super();
		width = getMediaBox().getWidth();
		height = getMediaBox().getHeight();
	}
	
	protected double getWidth() {return width;}
	protected double getHeight() {return height;}
	
	public PDRectangle getMainBox() {
		switch (mainBox) {
			case MEDIA:
				return getMediaBox();
			case BLEED:
				return getBleedBox();
			case TRIM:
				return getTrimBox();
			default:
				return getMediaBox();
		}
	}
	
	public void setDimensions(double width, double height) {
		this.width = width;
		this.height = height;
		setBoxes();
	}
	public void setBleeds(double[] b) {
		if (b.length != 4) throw new IllegalArgumentException("Invalid number of parameters in bleed settings.");
		bleeds = b;
		setBoxes();
	}
	public void setBleeds(double b) {
		setBleeds(new double[]{b, b, b, b});
	}
	public void setOverbleeds(double[] b) {
		if (b.length != 4) throw new IllegalArgumentException("Invalid number of parameters in overbleed settings.");
		overbleeds = b;
		setBoxes();
	}
	public void setOverbleeds(double b) {
		setOverbleeds(new double[]{b, b, b, b});
	}
	
	/**
	 * Sets the MediaBox, BleedBox and TrimBox based on the
	 * stored bleeds and overbleeds values. 
	 */
	protected void setBoxes() {
		// Place the Media Box to the origin
		setMediaBox(new PDRectangle(
			(float)(width+bleeds[1]+bleeds[3]+overbleeds[1]+overbleeds[3]),
			(float)(height+bleeds[0]+bleeds[2]+overbleeds[0]+overbleeds[2])
		));
				
		PDRectangle bleedbox = new PDRectangle(
				(float)(width+bleeds[1]+bleeds[3]),
				(float)(height+bleeds[0]+bleeds[2])
		);
		bleedbox.setLowerLeftX((float)overbleeds[3]);
		bleedbox.setLowerLeftY((float)overbleeds[2]);
		setBleedBox(bleedbox);
		
		PDRectangle trimbox = new PDRectangle(
				(float)(width),
				(float)(height)
		);
		trimbox.setLowerLeftX((float)(overbleeds[3]+bleeds[3]));
		trimbox.setLowerLeftY((float)(overbleeds[2]+bleeds[2]));
		setTrimBox(trimbox);
	}
	
	public abstract void draw(Graphics2D g, AffineTransform view);
	
	/** Lists the kinds of boxes available in PDF. */
	public enum Box {
		MEDIA,
		BLEED,
		TRIM
	}
}
