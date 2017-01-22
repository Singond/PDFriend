package cz.slanyj.pdfriend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Sheet<P extends Page> extends AbstractPage {
	
	/** The individual pages placed on this Sheet. */
	private ArrayList<P> pages = new ArrayList<>();
	protected ArrayList<P> pages() {return pages;}
	
	protected void add(P pr) {
		pages.add(pr);
	};
	
	@Override
	public void draw(Graphics2D g, AffineTransform view) {
		Color orig = g.getColor();
		Stroke origStroke = g.getStroke();
		
		// Draw shadow
		AffineTransform shadow =  AffineTransform.getTranslateInstance(
				DisplayOptions.SHADOW_OFFSET_X,	DisplayOptions.SHADOW_OFFSET_Y);		// Offset the shadow
		shadow.concatenate(view);														// Combine it with view
		g.setColor(DisplayOptions.SHADOW_COLOR);
		g.fill(shadow.createTransformedShape(Util.drawable(getMediaBox())));
		
		// Draw media box
		Shape media = view.createTransformedShape(Util.drawable(getMediaBox()));
		g.setColor(DisplayOptions.SHEET_MEDIABOX_COLOR);
		g.fill(media);
		g.setColor(DisplayOptions.SHEET_MEDIABOX_BORDER_COLOR);
		g.setStroke(DisplayOptions.SHEET_MEDIABOX_BORDER_STROKE);
		g.draw(media);
		
		// Draw bleed box
		Shape bleed = view.createTransformedShape(Util.drawable(getBleedBox()));
		g.setColor(DisplayOptions.SHEET_BLEED_COLOR);
		g.fill(bleed);
		g.setColor(DisplayOptions.SHEET_BLEED_BORDER_COLOR);
		g.setStroke(DisplayOptions.SHEET_BLEED_BORDER_STROKE);
		g.draw(bleed);
		
		// Draw trim box
		Shape trim = view.createTransformedShape(Util.drawable(getTrimBox()));
		g.setColor(DisplayOptions.SHEET_COLOR);
		g.fill(trim);
		g.setColor(DisplayOptions.SHEET_BORDER_COLOR);
		g.setStroke(DisplayOptions.SHEET_BORDER_STROKE);
		g.draw(trim);
		
		// Draw individual pages
		pages.stream().forEach(p -> {
			p.draw(g, view);
		});
		
		g.setStroke(origStroke);
		g.setColor(orig);
	}
	
	public static class Real extends Sheet<Page.Real> {
		private final Document doc;
		public Real(Document doc) {
			this.doc = doc;
		}

		public void add(Page.Real pr) {
			pages().add(pr);
		};
		
		
		public void imposePages() {
			
		}
	}
	
	public static class Template extends Sheet<Page.Template> {
		
	}
}
