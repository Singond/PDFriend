package com.github.singond.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.book.control.PageVisitor;
import com.github.singond.pdfriend.document.VirtualPage;

/**
 * A type of MultiPage which stacks multiple pagelets onto each other.
 * The pagelets perform no transformation.
 * @author Singon
 */
public class LayeredPage extends MultiPage {
	/** The layers (ie. pagelets) of this page */
	private final List<Pagelet> layers;

	public LayeredPage(double width, double height, int layers) {
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
	
	/**
	 * Puts the given virtual page as a source into the given layer.
	 * @param index the index of the affected layer
	 * @param source the virtual page to be used as a source for the given layer
	 */
	public void setLayerSource(int index, VirtualPage source) {
		layers.get(index).setSource(source);
	}
	
	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}
}
