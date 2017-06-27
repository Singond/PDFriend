package com.github.singond.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * A type of MultiPage which stacks multiple pagelets onto each other.
 * The pagelets perform no transformation.
 * @author Singon
 */
public class LayeredPage extends MultiPage {
	/** The layers (ie. pagelets) of this page */
	private final List<Pagelet> layers;

	protected LayeredPage(double width, double height, int layers) {
		super(width, height);
		this.layers = new ArrayList<>(layers);
		for (int i=0; i<layers; i++) {
			Pagelet pglt = new Pagelet(width, height, new AffineTransform());
			this.layers.add(pglt);
			super.addPagelet(pglt);
		}
	}
	
	/**
	 * A copy constructor.
	 * @param original
	 */
	public LayeredPage(LayeredPage original) {
		super(original.getWidth(), original.getHeight());
		List<Pagelet> origPagelets = original.layers;
		this.layers = new ArrayList<>(origPagelets.size());
		for (Pagelet origPglt : origPagelets) {
			Pagelet pagelet = new Pagelet(origPglt);
			this.layers.add(pagelet);
			super.addPagelet(pagelet);
		}
	}
}
