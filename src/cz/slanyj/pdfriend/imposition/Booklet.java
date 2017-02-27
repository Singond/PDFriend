package cz.slanyj.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import cz.slanyj.pdfriend.Bundle;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.SourceDocument;
import cz.slanyj.pdfriend.book.FlipDirection;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.book.Stack;
import cz.slanyj.pdfriend.book.Stack.Flip;
import cz.slanyj.pdfriend.book.Volume;
import cz.slanyj.pdfriend.book.Signature;
import cz.slanyj.pdfriend.geometry.Line;
import cz.slanyj.pdfriend.geometry.Point;

/**
 * A booklet with one page of input per one page of output.
 * A document consisting of a single Signature. The signature is formed
 * by a simple Stack of sheets folded in half.
 * @author Singon
 *
 */
public class Booklet extends Document {

	/** Width of single Page. */
	private final double pageWidth;
	/** Height of single Page. */
	private final double pageHeight;
	/** The Volume representing this Booklet. */
	private final Volume volume;
	
	/**
	 * Constructs a new Booklet without content.
	 * @param width The width of single Page of the finished booklet.
	 * @param height The height of single Page of the finished booklet.
	 * @param pages The number of pages in the finished booklet.
	 * @param binding The edge of a Page where the binding is to be placed.
	 */
	public Booklet(double width, double height, int pages, Binding binding) {
		Log.info(Bundle.console, "booklet_constructing", pages);
		pageWidth = width;
		pageHeight = height;
		
		if (pages < 1) {
			throw new IllegalArgumentException
				("A booklet must have at least one page");
		} else if (pages % 4 != 0) {
			Log.warn(Bundle.console, "booklet-padding");
			pages = ((pages / 4) + 1) * 4;
		}
		assert (pages >= 1) && (pages % 4 == 0) : pages;
		
		Volume vol = new Volume();
		Signature signature = null;
		if (binding == Binding.VERTICAL) {
			signature = createVertical(width, height, pages);
		} else if (binding == Binding.HORIZONTAL) {
			signature = createHorizontal(width, height, pages);
		} else {
			assert false : binding;
		}
		signature.numberPagesFrom(1);
		vol.add(signature);
		volume = vol;
	}
	
	/**
	 * Constructs a new Booklet without content, with vertical binding.
	 * @param width The width of single Page of the finished booklet.
	 * @param height The height of single Page of the finished booklet.
	 * @param pages The number of pages in the finished booklet.
	 */
	public Booklet(double width, double height, int pages) {
		this(width, height, pages, Binding.VERTICAL);
	}
	
	private Signature createVertical(double width, double height, int pages) {
		final Stack stack = new Stack(2*width, height);
		List<Stack.Manipulation> manipulations = new ArrayList<>(3);
		manipulations.add(new Stack.Gather(pages/4));
		Line foldAxis = new Line(new Point(width, 0), new Point(width, 1));
		manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
		manipulations.add(Flip.horizontal(width));
		stack.performManipulations(manipulations);
		
		List<Leaf> pattern = new ArrayList<>(1);
		Leaf leaf = new Leaf(width, height);
		leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
		pattern.add(leaf);
		
		return stack.buildSignature(pattern);
	}
	
	private Signature createHorizontal(double width, double height, int pages) {
		final Stack stack = new Stack(width, 2*height);
		List<Stack.Manipulation> manipulations = new ArrayList<>(2);
		manipulations.add(new Stack.Gather(pages/4));
		Line foldAxis = new Line(new Point(0, height), new Point(1, height));
		manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
		stack.performManipulations(manipulations);
		
		List<Leaf> pattern = new ArrayList<>(1);
		Leaf leaf = new Leaf(width, height);
		leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
		leaf.setFlipDirection(FlipDirection.AROUND_X);
		pattern.add(leaf);
		
		return stack.buildSignature(pattern);
	}
	
	/**
	 * Returns a new Booklet without content, adapted for imposing the
	 * given document by getting its number of pages and page dimensions.
	 * @param source The document to adapt to.
	 * @param binding The edge with binding.
	 * @return A new Booklet object.
	 */
	public static Booklet from(SourceDocument source, Binding binding) {
		double[] dimensions = source.getPageDimensions();
		int pageCount = source.getPageCount();
		return new Booklet(dimensions[0], dimensions[1], pageCount, binding);
	}
	
	/**
	 * Returns a new Booklet without content, adapted for imposing the
	 * given document by getting its number of pages and page dimensions.
	 * @param source The document to adapt to.
	 * @return A new Booklet object.
	 */
	public static Booklet from(SourceDocument source) {
		return Booklet.from(source, Binding.VERTICAL);
	}
	
	@Override
	public Volume volume() {
		return volume;
	}
	
	/** Specifies how the booklet is folded. */
	public static enum Binding {
		/** The fold (and binding) is vertical. */
		VERTICAL,
		/** The fold (and binding) is horizontal. */
		HORIZONTAL;
	}
}
