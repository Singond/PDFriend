package cz.slanyj.pdfriend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.swing.Canvas;

// TODO Method to get the rectangle occupied by a given sheet. This will be used to fit the page into view in GUI.

public abstract class Project<S extends Sheet> extends PDDocument implements Canvas.CanvasPainter {

	private final ArrayList<S> sheets = new ArrayList<>();
	
	protected ArrayList<S> getSheets() {
		return sheets;
	}
	
	public void add(S s) {
		sheets.add(s);
	}
	public void add(int index, S s) {
		sheets.add(index, s);
	}
	
	@Override
	public void paintToCanvas(Graphics2D g, AffineTransform view) {
		for (S s : sheets) {
			s.draw(g, view);
		}
	}
		
}
