package com.github.singond.pdfriend.imposition;

import java.awt.geom.AffineTransform;
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
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.Preprocessor.Settings;
import com.github.singond.geometry.plane.Line;
import com.github.singond.geometry.plane.Point;

/**
 * A booklet with one page of input per one page of output.
 * A document consisting of a single Signature. The signature is formed
 * by a simple Stack of sheets folded in half.
 * <p>
 * Instances of this class are immutable.
 * 
 * @author Singon
 *
 */
public class Booklet implements Imposable {
	
	/** The internal name of this imposable document type */
	private static final String NAME = "booklet";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(Booklet.class);

	private final Edge binding;
	private final boolean versoOpposite;
	private final Preprocessor.Settings preprocess;
	private final CommonSettings common;
	private final LengthUnit unit = Imposition.LENGTH_UNIT;
	
	private Booklet(Edge binding, boolean versoOpposite,
	                Preprocessor.Settings preprocess, CommonSettings common) {
		if (binding == null)
			throw new IllegalArgumentException("Binding must not be null");
		if (preprocess == null)
			throw new IllegalArgumentException("Preprocessor settings must not be null");
		if (common == null)
			throw new IllegalArgumentException("Common settings must not be null");
		
		this.binding = binding;
		this.versoOpposite = versoOpposite;
		this.preprocess = preprocess.copy();
		this.common = common;
	}

	/**
	 * Returns a new {@code Volume} object resulting from imposing the given
	 * virtual document into a booklet according to the current settings.
	 * @param doc the virtual document to be imposed
	 */
	private Volume imposeAsVolume(VirtualDocument doc) {
		// Copy all nonfinal values defensively
		/**
		 * The number of pages in the finished booklet.
		 * The initial value does not have to be a multiple of four;
		 * it will be increased to the first integer multiple automatically.
		 */
		int pageCount = common.getPageCount();
		
		if (logger.isDebugEnabled()) {
			logger.debug("imposition_preprocessSettings", preprocess);
			logger.debug("imposition_commonSettings", common);
//			logger.debug("imposition_imposableSettings", NAME, );
		}
		
		/*
		 * Resolve the margins into a valid value.
		 */
		Margins margins = common.getMargins();
		if (margins == CommonSettings.AUTO_MARGINS) {
			margins = Margins.NONE;
		}
		
		/*
		 * If the margins are not to be mirrored, they should be set
		 * to the preprocessor. This will apply the margins to each page
		 * individually without considering whether they are verso or recto.
		 * 
		 * However, if they are to be mirrored, they must not be applied
		 * to the individual pages, but rather to the Leaf prior to putting
		 * it onto the folded stack.
		 */
		// The mirrored margins (if any) to be applied later
		Margins mirroredMargins;
		if (common.isMirrorMargins()) {
			mirroredMargins = margins;
		} else {
			preprocess.setCellMargins(margins);
			mirroredMargins = Margins.NONE;
		}
		
		/*
		 * Determine the size of the sheet before folding and the size
		 * of a single page after folding.
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
		
		/*
		 * Next, reconcile page size and sheet size, if any of the two
		 * is given. If both are given, throw an exception.
		 */
//		if (pageSize != CommonSettings.AUTO_DIMENSIONS) {
//			if (sheetSize == CommonSettings.AUTO_DIMENSIONS) {
//				// Only page size is given: determine sheet size
//				logger.verbose("nup_pageSizeToSheetSize");
//				sheetSize = sheetFromPage(pageSize, binding, mirroredMargins);
//			} else {
//				// Both are given: a conflict
//				throw new IllegalStateException
//					("Both sheet size and page size are set to a non-auto value");
//			}
//		} // Otherwise just leave sheetSize as it is
//		pageSize = null;           // Won't need this anymore
		
		boolean autoPage = pageSize == CommonSettings.AUTO_DIMENSIONS;
		boolean autoSheet = sheetSize == CommonSettings.AUTO_DIMENSIONS;
		
//		boolean sheetSizeLater = false;
		if (!autoPage && autoSheet) {
			// Only page size is given: determine sheet size
//			logger.verbose("booklet_pageSizeToSheetSize");
//			sheetSize = sheetFromPage(pageSize, binding, mirroredMargins);
//			preprocess.setCellDimensions(pageSize);
		} else if (autoPage && !autoSheet) {
			// Sheet size is given: determine page size
			pageSize = pageFromSheet(sheetSize, binding, mirroredMargins);
			logger.verbose("booklet_sheetSizeToPageSize", pageSize, sheetSize, margins);
//			preprocess.setCellDimensions(pageSize);
		} else if (!autoPage && !autoSheet){
			// Both are given: a conflict
			throw new IllegalStateException
				("Both sheet size and page size are set to a non-auto value");
		} else {
//			sheetSizeLater = true;
		}

		/*
		 * Finally, make the preprocessor aware of the page size.
		 * In booklet imposition, the "cell" of the preprocessor corresponds
		 * to one page in the output booklet.
		 * If the page size is auto, no configuration is needed, because
		 * that is its default behaviour.
		 * If, however, a certain page size is desired, that size must
		 * be passed to the preprocessor as the cell size.
		 */
		if (pageSize != CommonSettings.AUTO_DIMENSIONS) {
			preprocess.setCellDimensions(pageSize);
		}
		
		
		
		/*
		 * Now preprocess the pages and store the page dimensions.
		 */
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		pageSize = preprocessor.getResolvedCellDimensions();
//		if (autoSheet) {
//			sheetSize = sheetFromPage(cell, binding, mirroredMargins);
//		}
//		double width = cell.width().in(unit);
//		double height = cell.height().in(unit);
		doc = preprocessor.processAll();
		
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
			pageCount = doc.getLength();
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
		SignatureMaker maker = new SignatureMaker(pageSize, mirroredMargins, unit, pageCount);
		Signature signature = maker.makeSignature();
		signature.numberPagesFrom(1);
		volume.add(signature);
		
		/*
		 * Fill the volume with content.
		 */
		SourceProvider<Page> sp = new SequentialSourceProvider(doc);
		sp.setSourceTo(volume.pages());
		
		return volume;
	}
	
	/**
	 * Calculates the size of page resulting from folding the given sheet
	 * in half along the edge given by {@code binding}, and further shrinking
	 * the area by subtracting the given margins.
	 * @param sheet the dimensions of the sheet to be folded
	 * @param binding the edge at which to fold the sheet in half
	 * @param margins the margins to be left around the page.
	 *        Note that on verso pages, these margins will be mirrored
	 *        around the binding edge.
	 * @return the sheet dimensions halved horizontally or vertically,
	 *         based on the value of {@code binding}, and shrunk by
	 *         {@code margins}
	 */
	private Dimensions pageFromSheet(Dimensions sheet, Edge binding,
	                                 Margins margins) {
		Length width = sheet.width();
		Length height = sheet.height();
		
		switch (binding) {
			case TOP: case BOTTOM:
				height = height.times(1d/2);
				break;
			case LEFT: case RIGHT:
				width = width.times(1d/2);
				break;
			default:
				throw new AssertionError("Invalid 'binding' value");
		}
		
		return new Dimensions(width, height);
//		return new Dimensions(Length.subtract(width, margins.horizontal()),
//		                      Length.subtract(height, margins.vertical()));
	}
	
	/**
	 * Calculates the size of sheet necessary to produce the given page size
	 * by folding the sheet in half along the edge given by {@code binding},
	 * while leaving the given margins around each page.
	 * @param page the desired page dimensions after folding the sheet
	 * @param binding the edge at which to fold the sheet in half
	 * @param margins the margins to be left around the page.
	 *        Note that on verso pages, these margins will be mirrored
	 *        around the binding edge.
	 * @return the page dimensions doubled horizontally or vertically,
	 *         based on the value of {@code binding}, and stretched by
	 *         {@code margins}
	 */
	private Dimensions sheetFromPage(Dimensions page, Edge binding,
	                                 Margins margins) {
		Length width = page.width();
		Length height = page.height();
		
		switch (binding) {
			case TOP: case BOTTOM:
				height = height.times(2);
				break;
			case LEFT: case RIGHT:
				width = width.times(2);
				break;
			default:
				throw new AssertionError("Invalid binding value");
		}
		
		return new Dimensions(width, height);
//		return new Dimensions(Length.sum(width, margins.horizontal()),
//		                      Length.sum(height, margins.vertical()));
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
	
	/**
	 * A builder for Booklet objects.
	 */
	public static class Builder implements ImposableBuilder<Booklet> {
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

		@Override
		public void acceptPreprocessSettings(Settings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Preprocess settings cannot be null");
			this.preprocess = settings;
		}
		
		@Override
		public void acceptCommonSettings(CommonSettings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Settings cannot be null");
			this.common = settings;
		}

		@Override
		public Booklet build() {
			return new Booklet(binding, versoOpposite, preprocess, common);
		}
	}
	
	/**
	 * <h2>Note</h2>
	 * The margins applied by this class are mirrored with respect to the
	 * binding edge. If non-mirrored margins are desired, they must be
	 * applied to the individual pages.
	 *
	 * @author Singon
	 *
	 */
	private class SignatureMaker {
		private final double contentWidth;
		private final double contentHeight;
		private final double totalWidth;
		private final double totalHeight;
		
		private final double leftMargin;
		private final double rightMargin;
		private final double bottomMargin;
		private final double topMargin;
		
		private final int pages;
		
		/**
		 * Constructs a new {@code SignatureMaker} object.
		 * @param page dimensions of the page without mirrored margins applied
		 * @param margins margins to be applied to recto pages. The margins
		 *        at verso pages will be mirrored with respect to the binding.
		 * @param unit the length unit used in the resulting objects
		 * @param pages the number of pages in the finished booklet, including blanks.
		 *        It follows that the number must be an integer multiple of four.
		 */
		private SignatureMaker(Dimensions page, Margins margins,
		                       LengthUnit unit, int pages) {
			if (pages % 4 != 0) {
				throw new IllegalArgumentException
						("The number of pages must be dividible by four");
			}
			
			this.contentWidth = page.width().in(unit);
			this.contentHeight = page.height().in(unit);
			this.leftMargin = margins.left().in(unit);
			this.rightMargin = margins.right().in(unit);
			this.bottomMargin = margins.bottom().in(unit);
			this.topMargin = margins.top().in(unit);
			this.totalWidth = contentWidth + leftMargin + rightMargin;
			this.totalHeight = contentHeight + bottomMargin + topMargin;
			this.pages = pages;
		}

		/**
		 * Builds a new signature using the settings given in initialization.
		 * @return a new {@code Signature} object representing a booklet
		 *         with the properties set in this {@code SignatureMaker}
		 */
		private Signature makeSignature() {
			switch (binding) {
				case TOP:
					return boundAtTop();
				case RIGHT:
					return boundAtRight();
				case BOTTOM:
					return boundAtBottom();
				case LEFT:
					return boundAtLeft();
				default:
					throw new AssertionError("Unknown binding value: " + binding);
			}
		}
		
		/**
		 * Returns a new Signature object representing a stack of sheets
		 * folded in half at the left edge.
		 * @return a new {@code Signature} object with leaves in proper positions
		 */
		private Signature boundAtLeft() {
			final Stack stack = new Stack(2*totalWidth, totalHeight);
			List<Stack.Manipulation> manipulations = new ArrayList<>(3);
			manipulations.add(new Stack.Gather(pages/4));
			Line foldAxis = new Line(new Point(totalWidth, 0), new Point(totalWidth, 1));
			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
			manipulations.add(Flip.horizontal(totalWidth));
			stack.performManipulations(manipulations);
			
			return stack.buildSignature(bookletLeaf());
		}

		/**
		 * Returns a new Signature object representing a stack of sheets
		 * folded in half at the right edge.
		 * @param totalWidth width of one page of the folded sheet
		 * @param totalHeight width of one page of the folded sheet
		 * @param pages the number of pages in the finished booklet, including blanks.
		 *        It follows that the number must be an integer multiple of four.
		 * @return a new {@code Signature} object with leaves in proper positions
		 */
		private Signature boundAtRight() {
			final Stack stack = new Stack(2*totalWidth, totalHeight);
			List<Stack.Manipulation> manipulations = new ArrayList<>(2);
			manipulations.add(new Stack.Gather(pages/4));
			Line foldAxis = new Line(new Point(totalWidth, 0), new Point(totalWidth, 1));
			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
			stack.performManipulations(manipulations);
			
			return stack.buildSignature(bookletLeaf());
		}
		
		/**
		 * Returns a new Signature object representing a stack of sheets
		 * folded in half at the top edge.
		 * @param totalWidth width of one page of the folded sheet
		 * @param totalHeight width of one page of the folded sheet
		 * @param pages the number of pages in the finished booklet, including blanks.
		 *        It follows that the number must be an integer multiple of four.
		 * @param versoOpposite whether verso should be upside down with respect
		 *        to the recto
		 * @return a new {@code Signature} object with leaves in proper positions
		 */
		private Signature boundAtTop() {
			final Stack stack = new Stack(totalWidth, 2*totalHeight);
			List<Stack.Manipulation> manipulations = new ArrayList<>(2);
			manipulations.add(new Stack.Gather(pages/4));
			Line foldAxis = new Line(new Point(0, totalHeight), new Point(1, totalHeight));
			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
			stack.performManipulations(manipulations);
			
			return stack.buildSignature(verticalBookletLeaf());
		}
		
		/**
		 * Returns a new Signature object representing a stack of sheets
		 * folded in half at the bottom edge.
		 * @param totalWidth width of one page of the folded sheet
		 * @param totalHeight width of one page of the folded sheet
		 * @param pages the number of pages in the finished booklet, including blanks.
		 *        It follows that the number must be an integer multiple of four.
		 * @param versoOpposite whether verso should be upside down with respect
		 *        to the recto
		 * @return a new {@code Signature} object with leaves in proper positions
		 */
		private Signature boundAtBottom() {
			final Stack stack = new Stack(totalWidth, 2*totalHeight);
			List<Stack.Manipulation> manipulations = new ArrayList<>(3);
			manipulations.add(new Stack.Gather(pages/4));
			Line foldAxis = new Line(new Point(0, totalHeight), new Point(1, totalHeight));
			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
			manipulations.add(Flip.vertical(totalHeight));
			stack.performManipulations(manipulations);
			
			return stack.buildSignature(verticalBookletLeaf());
		}
		
		private Leaf bookletLeaf() {
			Leaf leaf = new Leaf(contentWidth, contentHeight);
			AffineTransform position = AffineTransform.getTranslateInstance
					(leftMargin, bottomMargin);
			leaf.setAsFrontPosition(position);
			return leaf;
		}
		
		/**
		 * Returns the same as {@link #bookletLeaf}, but further flipped
		 * if {@code versoOpposite} is set to false.
		 * @return
		 */
		private Leaf verticalBookletLeaf() {
			Leaf leaf = bookletLeaf();
			/*
			 * With the default flip direction about y, the verso is opposite
			 * already. When verso is to be in the same direction as recto,
			 * make it flip around the x-axis:
			 */
			if (!versoOpposite) {
				leaf.setFlipDirection(FlipDirection.AROUND_X);
			}
			return leaf;
		}
		
//		/**
//		 * Returns a new Signature object representing a stack of sheets
//		 * folded in half at the left edge.
//		 * @param width width of one page of the folded sheet
//		 * @param height width of one page of the folded sheet
//		 * @param pages the number of pages in the finished booklet, including blanks.
//		 *        It follows that the number must be an integer multiple of four.
//		 * @return a new {@code Signature} object with leaves in proper positions
//		 */
//		private Signature withLeftBinding(double width, double height, int pages) {
//			final Stack stack = new Stack(2*width, height);
//			List<Stack.Manipulation> manipulations = new ArrayList<>(3);
//			manipulations.add(new Stack.Gather(pages/4));
//			Line foldAxis = new Line(new Point(width, 0), new Point(width, 1));
//			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
//			manipulations.add(Flip.horizontal(width));
//			stack.performManipulations(manipulations);
//
//			Leaf leaf = new Leaf(width, height);
//			leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
//
//			return stack.buildSignature(leaf);
//		}
//
//		/**
//		 * Returns a new Signature object representing a stack of sheets
//		 * folded in half at the right edge.
//		 * @param width width of one page of the folded sheet
//		 * @param height width of one page of the folded sheet
//		 * @param pages the number of pages in the finished booklet, including blanks.
//		 *        It follows that the number must be an integer multiple of four.
//		 * @return a new {@code Signature} object with leaves in proper positions
//		 */
//		private Signature withRightBinding(double width, double height, int pages) {
//			final Stack stack = new Stack(2*width, height);
//			List<Stack.Manipulation> manipulations = new ArrayList<>(2);
//			manipulations.add(new Stack.Gather(pages/4));
//			Line foldAxis = new Line(new Point(width, 0), new Point(width, 1));
//			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
//			stack.performManipulations(manipulations);
//
//			Leaf leaf = new Leaf(width, height);
//			leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
//
//			return stack.buildSignature(leaf);
//		}
//
//		/**
//		 * Returns a new Signature object representing a stack of sheets
//		 * folded in half at the top edge.
//		 * @param width width of one page of the folded sheet
//		 * @param height width of one page of the folded sheet
//		 * @param pages the number of pages in the finished booklet, including blanks.
//		 *        It follows that the number must be an integer multiple of four.
//		 * @param versoOpposite whether verso should be upside down with respect
//		 *        to the recto
//		 * @return a new {@code Signature} object with leaves in proper positions
//		 */
//		private Signature withTopBinding(double width, double height, int pages,
//		                                 boolean versoOpposite) {
//			final Stack stack = new Stack(width, 2*height);
//			List<Stack.Manipulation> manipulations = new ArrayList<>(2);
//			manipulations.add(new Stack.Gather(pages/4));
//			Line foldAxis = new Line(new Point(0, height), new Point(1, height));
//			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
//			stack.performManipulations(manipulations);
//
//			Leaf leaf = new Leaf(width, height);
//			leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
//			/*
//			 * With the default flip direction about y, the verso is opposite
//			 * already. When verso is to be in the same direction as recto,
//			 * make it flip around the x-axis:
//			 */
//			if (!versoOpposite) {
//				leaf.setFlipDirection(FlipDirection.AROUND_X);
//			}
//
//			return stack.buildSignature(leaf);
//		}
//
//		/**
//		 * Returns a new Signature object representing a stack of sheets
//		 * folded in half at the bottom edge.
//		 * @param width width of one page of the folded sheet
//		 * @param height width of one page of the folded sheet
//		 * @param pages the number of pages in the finished booklet, including blanks.
//		 *        It follows that the number must be an integer multiple of four.
//		 * @param versoOpposite whether verso should be upside down with respect
//		 *        to the recto
//		 * @return a new {@code Signature} object with leaves in proper positions
//		 */
//		private Signature withBottomBinding(double width, double height, int pages,
//		                                    boolean versoOpposite) {
//			final Stack stack = new Stack(width, 2*height);
//			List<Stack.Manipulation> manipulations = new ArrayList<>(3);
//			manipulations.add(new Stack.Gather(pages/4));
//			Line foldAxis = new Line(new Point(0, height), new Point(1, height));
//			manipulations.add(new Stack.Fold(foldAxis, Stack.Fold.Direction.UNDER));
//			manipulations.add(Flip.vertical(height));
//			stack.performManipulations(manipulations);
//
//			Leaf leaf = new Leaf(width, height);
//			leaf.setAsFrontPosition(new Leaf.Position(width/2, height/2, 0));
//			/*
//			 * With the default flip direction about y, the verso is opposite
//			 * already. When verso it to be in the same direction as recto,
//			 * make it flip around the x-axis:
//			 */
//			if (!versoOpposite) {
//				leaf.setFlipDirection(FlipDirection.AROUND_X);
//			}
//
//			return stack.buildSignature(leaf);
//		}
		
	}
}
