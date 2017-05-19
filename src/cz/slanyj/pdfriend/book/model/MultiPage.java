package cz.slanyj.pdfriend.book.model;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.slanyj.pdfriend.book.control.PageVisitor;
import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.Content.Movable;
import cz.slanyj.pdfriend.document.VirtualPage;

public class MultiPage extends Page {

	private final List<Pagelet> pagelets;
	
	public MultiPage(double width, double height) {
		super(width, height);
		pagelets = new ArrayList<>();
	}
	
	public void AddPage(VirtualPage page, AffineTransform position) {
		pagelets.add(new Pagelet(page, position));
	}
	
	@Override
	public Collection<Movable> getContent() {
		Set<Content.Movable> contents = new HashSet<>();;
		for (Pagelet p : pagelets) {
			for (Content.Movable cm : p.source.getMovableContent()) {
				cm.getTransform().preConcatenate(p.position);
				contents.add(cm);
			}
		}
		return contents;
	}

	@Override
	public <R, P, E extends Throwable> R invite(PageVisitor<R, P, E> visitor, P param) throws E {
		return visitor.visit(this, param);
	}
	
	/** A source page along with its position on this MultiPage. */
	private static class Pagelet {
		private final VirtualPage source;
		private final AffineTransform position;
		
		private Pagelet(VirtualPage source, AffineTransform position) {
			this.source = source;
			this.position = position;
		}
	}
}
