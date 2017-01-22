package cz.slanyj.pdfriend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class Util {
	
/*	public static void draw(PDRectangle rect, Graphics2D g) {
		g.fill(new Rectangle2D.Double(
				rect.getLowerLeftX(),
				rect.getLowerLeftY(),
				rect.getUpperRightX(),
				rect.getUpperRightY()));
	}*/
	
	/** Draws a rectangle filled with <code>fill</code> color
	 * and draws a border on the outside using the current Stroke
	 * and <code>border</code> color. */
/*	public static void drawWithBorder(
							PDRectangle rect,
							Graphics2D g,
							Color fill,
							Color border) {
		float thickness = ((BasicStroke)g.getStroke()).getLineWidth();
		Color original = g.getColor();
		g.setColor(border);
		g.draw(new Rectangle2D.Double(
				rect.getLowerLeftX(),
				rect.getLowerLeftY(),
				rect.getUpperRightX(),
				rect.getUpperRightY()));
		g.setColor(fill);
		g.fill(new Rectangle2D.Double(
				rect.getLowerLeftX(),
				rect.getLowerLeftY(),
				rect.getUpperRightX(),
				rect.getUpperRightY()));
		g.setColor(original);;
	}*/
	
/*	public static void drawWithBorder(
			PDRectangle rect,
			Graphics2D g) {
		drawWithBorder(rect, g, g.getColor(), Color.BLACK);
	}*/
	
	/** Transforms the given PDRectangle into Rectangle2D, which can be drawn using Graphics2D. */
	public static Rectangle2D drawable(PDRectangle r) {
		return new Rectangle2D.Double(
				r.getLowerLeftX(),
				r.getLowerLeftY(),
				r.getUpperRightX(),
				r.getUpperRightY());
	}
	
}
