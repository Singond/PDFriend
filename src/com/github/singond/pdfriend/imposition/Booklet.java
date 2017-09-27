package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.book.Signature;
import com.github.singond.pdfriend.book.SourceProvider;
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

	private Edge binding = Edge.LEFT;
	private boolean versoOpposite = false;
	private Preprocessor.Settings preprocess = null;
	private CommonSettings common = CommonSettings.auto();
	

	/**
	 * Returns the edge at which the binding is located.
	 * @return the edge of a Page where the binding will be placed
	 */
	public Edge getBinding() {
		return binding;
	}

	/**
	 * Sets the edge at which the binding is located.
	 * @param binding the edge of a Page where the binding is to be placed
	 */
	@Deprecated
	public void setBinding(Binding binding) {
		if (binding == null) {
			throw new IllegalArgumentException
			("Booklet binding must be set to a non-null value");
		}
		switch (binding) {
			case BOTTOM:
				this.binding = Edge.BOTTOM;
				break;
			case LEFT:
				this.binding = Edge.LEFT;
				break;
			case RIGHT:
				this.binding = Edge.RIGHT;
				break;
			case TOP:
				this.binding = Edge.TOP;
				break;
			default:
				throw new AssertionError("Unknown binding value");
			
		}
	}

	/**
	 * Sets the edge at which the binding is located.
	 * @param binding the edge of a Page where the binding is to be placed
	 */
	public void setBinding(Edge binding) {
		if (binding == null) {
			throw new IllegalArgumentException
					("Booklet binding must be set to a non-null value");
		}
		this.binding = binding;
	}

	/**
	 * Checks whether the verso of the booklet should be upside down.
	 * This only has effect when {@code binding} is TOP or BOTTOM.
	 * @return {@code true} if verso should be upside down with respect
	 *         to the recto.
	 */
	public boolean isVersoOpposite() {
		return versoOpposite;
	}

	/**
	 * Sets whether the verso of the booklet should be upside down.
	 * This only has effect when {@code binding} is TOP or BOTTOM.
	 * TODO Consider renaming the method
	 * @param versoOpposite whether verso should be upside down with respect
	 *        to the recto.
	 */
	public void setVersoOpposite(boolean versoOpposite) {
		this.versoOpposite = versoOpposite;
	}

	/**
	 * Returns a new {@code Volume} object resulting from imposing the given
	 * virtual document into a booklet according to the current settings.
	 * @param source the virtual document to be imposed
	 */
	private Volume imposeAsVolume(VirtualDocument source) {
		// Copy all nonfinal values defensively
		/**
		 * The number of pages in the finished booklet.
		 * The initial value does not have to be a multiple of four;
		 * it will be increased to the first integer multiple automatically.
		 */
		int pageCount = common.getPageCount();
		Binding binding = this.binding;
		boolean versoOpposite = this.versoOpposite;

		double[] dimensions = source.maxPageDimensions();
		double width = dimensions[0];
		double height = dimensions[1];
		
		if (width <= 0) {
			throw new IllegalArgumentException
					("Booklet width must be a positive number");
		}
		if (height <= 0) {
			throw new IllegalArgumentException
					("Booklet height must be a positive number");
		}
		
		logger.info("booklet_constructing", pageCount);
		
		/*
		 * Determine the number of pages in output.
		 * If the number is given explicitly, honor its value, otherwise
		 * take the number of pages in the input document.
		 * In any case, increase the value to the nearest integer multiple
		 * of four to reflect the fact that each output sheet contains four
		 * pages.
		 */
		if (pageCount < 1) {
			// Page count is automatic: resolve from input document length
			pageCount = source.getLength();
		}
		// Pad to multiple of four
		if (pageCount % 4 != 0) {
			int oldPages = pageCount;
			pageCount = ((pageCount / 4) + 1) * 4;
			logger.warn("booklet_padding", pageCount-oldPages);
		}
		assert (pageCount >= 1) && (pageCount % 4 == 0) : pageCount;
		
		/*
		 * Build the volume.
		 */
		Volume volume = new Volume();
		Signature signature = null;
		switch (binding) {
			case TOP:
				signature = withTopBinding(width, height, pageCount, versoOpposite);
				break;
			case RIGHT:
				signature = withRightBinding(width, height, pageCount);
				break;
			case BOTTOM:
				signature = withBottomBinding(width, height, pageCount, versoOpposite);
				break;
			case LEFT:
				signature = withLeftBinding(width, height, pageCount);
				break;
			default:
				assert false : binding;
		}
		signature.numberPagesFrom(1);
		volume.add(signature);
		
		/*
		 * Fill the volume with content.
		 */
		SourceProvider<Page> sp = new SequentialSourceProvider(source);
		sp.setSourceTo(volume.pages());
		
		return volume;
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
	 * Imposes the given virtual document into a new virtual document
	 * according to the current settings of this {@code Booklet} object.
	 */
	public VirtualDocument imposeAsDocument(VirtualDocument source) {
		return imposeAsVolume(source).renderDocument();
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public void acceptPreprocessSettings(Settings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Preprocess settings cannot be null");
		this.preprocess = settings.copy();
	}
	
	@Override
	public void acceptCommonSettings(CommonSettings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Settings cannot be null");
		this.common = settings;
	}

	/**
	 * {@inheritDoc}
	 * @return always the value of {@code false}
	 */
	@Override
	public boolean prefersMultipleInput() {
		return false;
	}

	@Override
	public VirtualDocument impose(VirtualDocument source) {
		return imposeAsVolume(source).renderDocument();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Booklet handles multiple document input by first concatenating them
	 * into one document in the order they appear in the argument.
	 */
	@Override
	public VirtualDocument impose(List<VirtualDocument> sources) {
		return impose(VirtualDocument.concatenate(sources));
	}
	
	/**
	 * Specifies where the booklet binding is located.
	 * Specifically, this value indicates on which edge of the folded
	 * booklet the binding is placed, when looking at the front page
	 * in correct (ie. top is on top) orientation.
	 */
	@Deprecated
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
