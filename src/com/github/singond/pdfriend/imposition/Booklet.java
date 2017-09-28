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
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
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
	private Preprocessor.Settings preprocess = Preprocessor.Settings.auto();
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
		final Edge binding = this.binding;
		final boolean versoOpposite = this.versoOpposite;
		final Preprocessor.Settings preprocess = this.preprocess;
		final CommonSettings common = this.common;
		
		if (logger.isDebugEnabled()) {
			logger.debug("imposition_preprocessSettings", preprocess);
			logger.debug("imposition_commonSettings", common);
//			logger.debug("imposition_imposableSettings", NAME, );
		}
		
		/*
		 * Determine the size of the sheet before folding.
		 * 
		 * If only the sheet size is given, use it as such, if only the
		 * page size is given, double it (either horizontally or vertically,
		 * depending on the binding edge) and use the resulting rectangle
		 * as the sheet size.
		 * Specifying both sheet size and page size to non-automatic value
		 * is a conflict. In this case, throw an exception.
		 * 
		 * Start by validating the page and sheet size.
		 */
		Dimensions pageSize = common.getPageSize();
		Dimensions sheetSize = common.getSheetSize();
		if (pageSize == null) {
			throw new IllegalStateException("Page size is null");
		}
		if (sheetSize == null) {
			throw new IllegalStateException("Sheet size is null");
		}
		
		if (pageSize != CommonSettings.AUTO_DIMENSIONS) {
			if (sheetSize == CommonSettings.AUTO_DIMENSIONS) {
				// Only page size is given: determine sheet size
				logger.verbose("nup_pageSizeToSheetSize");
				sheetSize = sheetFromPage(pageSize, binding);
			} else {
				// Both are given: a conflict
				throw new IllegalStateException
					("Both sheet size and page size are set to a non-auto value");
			}
		} // Otherwise just leave sheetSize as it is
		pageSize = null;           // Won't need this anymore

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
	 * Calculates the size of page resulting from folding the given sheet
	 * along the edge given by {@code binding}.
	 * @param sheet the dimensions of the sheet to be folded
	 * @param binding the edge at which to fold the sheet in half
	 * @return the sheet dimensions halved horizontally or vertically,
	 *         based on the value of {@code binding}
	 */
	@SuppressWarnings("unused")
	private Dimensions pageFromSheet(Dimensions sheet, Edge binding) {
		Length width = sheet.width();
		Length height = sheet.height();
		
		switch (binding) {
			case TOP: case BOTTOM:
				return new Dimensions(width, height.times(1/2));
			case LEFT: case RIGHT:
				return new Dimensions(width.times(1/2), height);
			default:
				throw new AssertionError("Invalid binding value");
		}
	}
	
	/**
	 * Calculates the size of sheet necessary to produce the given page
	 * size by folding the sheet along the edge given by {@code binding}.
	 * @param page the desired page dimensions after folding the sheet
	 * @param binding the edge at which to fold the sheet in half
	 * @return the sheet dimensions doubled horizontally or vertically,
	 *         based on the value of {@code binding}
	 */
	private Dimensions sheetFromPage(Dimensions page, Edge binding) {
		Length width = page.width();
		Length height = page.height();
		
		switch (binding) {
			case TOP: case BOTTOM:
				return new Dimensions(width, height.times(2));
			case LEFT: case RIGHT:
				return new Dimensions(width.times(2), height);
			default:
				throw new AssertionError("Invalid binding value");
		}
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
