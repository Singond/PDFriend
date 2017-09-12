package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.Signature;
import com.github.singond.pdfriend.book.Stack;
import com.github.singond.pdfriend.book.Volume;
import com.github.singond.pdfriend.book.Stack.Flip;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.imposition.Preprocessor.Settings;
import com.github.singond.geometry.plane.Line;
import com.github.singond.geometry.plane.Point;

/**
 * A booklet with one page of input per one page of output.
 * A document consisting of a single Signature. The signature is formed
 * by a simple Stack of sheets folded in half.
 * @author Singon
 *
 */
public class Booklet implements Imposable {
	
	/** The internal name of this imposable document type */
	private static final String NAME = "booklet";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(Booklet.class);

	/** Width of single Page. */
	private final double pageWidth;
	/** Height of single Page. */
	private final double pageHeight;
	/** The Volume representing this Booklet. */
	private final Volume volume;
	
	/**
	 * Constructs a new Booklet without content.
	 * @param width the width of single Page of the finished booklet
	 * @param height the height of single Page of the finished booklet
	 * @param pages the number of pages in the finished booklet
	 *        This number does not necessarily have to be a multiple of
	 *        four: It is increased to the first integer multiple
	 *        automatically.
	 * @param binding he edge of a Page where the binding is to be placed
	 * @param versoOpposite whether verso should be upside down with respect
	 *        to the recto.
	 *        This only has effect when {@code binding} is TOP or BOTTOM.
	 */
	public Booklet(double width, double height, int pages, Binding binding,
	               boolean versoOpposite) {
		if (width <= 0) {
			throw new IllegalArgumentException
					("Booklet width must be a positive number");
		}
		if (height <= 0) {
			throw new IllegalArgumentException
					("Booklet height must be a positive number");
		}
		if (binding == null) {
			throw new IllegalArgumentException
					("Booklet binding must be set to a non-null value");
		}
		
		logger.info("booklet_constructing", pages);
		pageWidth = width;
		pageHeight = height;
		
		if (pages < 1) {
			throw new IllegalArgumentException
				("A booklet must have at least one page");
		} else if (pages % 4 != 0) {
			int oldPages = pages;
			pages = ((pages / 4) + 1) * 4;
			logger.warn("booklet_padding", pages-oldPages);
		}
		assert (pages >= 1) && (pages % 4 == 0) : pages;
		
		Volume vol = new Volume();
		Signature signature = null;
		switch (binding) {
			case TOP:
				signature = withTopBinding(width, height, pages, versoOpposite);
				break;
			case RIGHT:
				signature = withRightBinding(width, height, pages);
				break;
			case BOTTOM:
				signature = withBottomBinding(width, height, pages, versoOpposite);
				break;
			case LEFT:
				signature = withLeftBinding(width, height, pages);
				break;
			default:
				assert false : binding;
		}
		signature.numberPagesFrom(1);
		vol.add(signature);
		volume = vol;
	}
	
	/**
	 * Constructs a new Booklet without content, with left binding.
	 * @param width the width of single Page of the finished booklet
	 * @param height the height of single Page of the finished booklet
	 * @param pages the number of pages in the finished booklet
	 */
	public Booklet(double width, double height, int pages) {
		this(width, height, pages, Binding.LEFT, false);
	}
	
	/**
	 * Returns a new Signature object representing a stack of sheets
	 * folded in half at the left edge.
	 * @param width width of one page of the folded sheet
	 * @param height width of one page of the folded sheet
	 * @param pages the number of pages in the finished booklet, including blanks.
	 *        It follows that the number must be an integer multiple of four.
	 * @return a new {@code Signature} object with leaves in proper positions
	 */
	private Signature withLeftBinding(double width, double height, int pages) {
		final Stack stack = new Stack(2*width, height);
		List<Stack.Manipulation> manipulations = new ArrayList<>(3);
		manipulations.add(new Stack.Gather(pages/4));
		Line foldAxis = new Line(new Point(width, 0), new Point(width, 1));
		manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
		manipulations.add(Flip.horizontal(width));
		stack.performManipulations(manipulations);
		
		Leaf leaf = new Leaf(width, height);
		leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
		
		return stack.buildSignature(leaf);
	}
	
	/**
	 * Returns a new Signature object representing a stack of sheets
	 * folded in half at the right edge.
	 * @param width width of one page of the folded sheet
	 * @param height width of one page of the folded sheet
	 * @param pages the number of pages in the finished booklet, including blanks.
	 *        It follows that the number must be an integer multiple of four.
	 * @return a new {@code Signature} object with leaves in proper positions
	 */
	private Signature withRightBinding(double width, double height, int pages) {
		final Stack stack = new Stack(2*width, height);
		List<Stack.Manipulation> manipulations = new ArrayList<>(2);
		manipulations.add(new Stack.Gather(pages/4));
		Line foldAxis = new Line(new Point(width, 0), new Point(width, 1));
		manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
		stack.performManipulations(manipulations);
		
		Leaf leaf = new Leaf(width, height);
		leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
		
		return stack.buildSignature(leaf);
	}
	
	/**
	 * Returns a new Signature object representing a stack of sheets
	 * folded in half at the top edge.
	 * @param width width of one page of the folded sheet
	 * @param height width of one page of the folded sheet
	 * @param pages the number of pages in the finished booklet, including blanks.
	 *        It follows that the number must be an integer multiple of four.
	 * @param versoOpposite whether verso should be upside down with respect
	 *        to the recto
	 * @return a new {@code Signature} object with leaves in proper positions
	 */
	private Signature withTopBinding(double width, double height, int pages,
	                                 boolean versoOpposite) {
		final Stack stack = new Stack(width, 2*height);
		List<Stack.Manipulation> manipulations = new ArrayList<>(2);
		manipulations.add(new Stack.Gather(pages/4));
		Line foldAxis = new Line(new Point(0, height), new Point(1, height));
		manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
		stack.performManipulations(manipulations);
		
		Leaf leaf = new Leaf(width, height);
		leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
		/*
		 * With the default flip direction about y, the verso is opposite
		 * already. When verso is to be in the same direction as recto,
		 * make it flip around the x-axis:
		 */
		if (!versoOpposite) {
			leaf.setFlipDirection(FlipDirection.AROUND_X);
		}
		
		return stack.buildSignature(leaf);
	}
	
	/**
	 * Returns a new Signature object representing a stack of sheets
	 * folded in half at the bottom edge.
	 * @param width width of one page of the folded sheet
	 * @param height width of one page of the folded sheet
	 * @param pages the number of pages in the finished booklet, including blanks.
	 *        It follows that the number must be an integer multiple of four.
	 * @param versoOpposite whether verso should be upside down with respect
	 *        to the recto
	 * @return a new {@code Signature} object with leaves in proper positions
	 */
	private Signature withBottomBinding(double width, double height, int pages,
	                                    boolean versoOpposite) {
		final Stack stack = new Stack(width, 2*height);
		List<Stack.Manipulation> manipulations = new ArrayList<>(3);
		manipulations.add(new Stack.Gather(pages/4));
		Line foldAxis = new Line(new Point(0, height), new Point(1, height));
		manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
		manipulations.add(Flip.vertical(height));
		stack.performManipulations(manipulations);
		
		Leaf leaf = new Leaf(width, height);
		leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
		/*
		 * With the default flip direction about y, the verso is opposite
		 * already. When verso it to be in the same direction as recto,
		 * make it flip around the x-axis:
		 */
		if (!versoOpposite) {
			leaf.setFlipDirection(FlipDirection.AROUND_X);
		}
		
		return stack.buildSignature(leaf);
	}
	
	/**
	 * Returns a new Booklet without content, adapted for imposing the
	 * given document by getting its number of pages and page dimensions.
	 * @param source the document to adapt to
	 * @param binding the edge with binding
	 * @param versoOpposite whether verso should be upside down with respect
	 *        to the recto.
	 *        This only has effect when {@code binding} is TOP or BOTTOM.
	 * @return a new Booklet object
	 */
	public static Booklet from(VirtualDocument source, Binding binding,
	                           boolean versoOpposite) {
		double[] dimensions = source.maxPageDimensions();
		int pageCount = source.getLength();
		return new Booklet(dimensions[0], dimensions[1], pageCount, binding, versoOpposite);
	}
	
	/**
	 * Returns a new Booklet without content, adapted for imposing the
	 * given document by getting its number of pages and page dimensions.
	 * This variant is bound on the left edge.
	 * @param source the document to adapt to
	 * @return a new Booklet object
	 */
	public static Booklet from(VirtualDocument source) {
		return Booklet.from(source, Binding.LEFT, false);
	}
	
	public Volume volume() {
		return volume;
	}
	
//	@Override
	@Deprecated
	public VirtualDocument getDocument() {
		// FIXME Fill the document with content before returning it!
		return volume.renderDocument();
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public void setPreprocessing(Settings settings) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public boolean prefersMultipleInput() {
		return false;
	}

	@Override
	public VirtualDocument impose(VirtualDocument source) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public VirtualDocument impose(List<VirtualDocument> sources) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/**
	 * Specifies where the booklet binding is located.
	 * Specifically, this value indicated on which edge of the folded
	 * booklet the binding is placed, when looking at the front page
	 * in correct (ie. top is on top) orientation.
	 */
	public static enum Binding {
		/** The fold (and binding) is on the top edge. */
		TOP,
		/** The fold (and binding) is on the right edge. */
		RIGHT,
		/** The fold (and binding) is on the bottom edge. */
		BOTTOM,
		/** The fold (and binding) is on the left edge. */
		LEFT;
	}
}
