package com.github.singond.pdfriend.imposition;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.BoundBook;
import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.Signature;
import com.github.singond.pdfriend.book.Stack;
import com.github.singond.pdfriend.book.Volume;
import com.github.singond.pdfriend.book.Stack.Flip;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.Preprocessor.Resizing;
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
public class Booklet extends AbstractImposable<BoundBook>
		implements Imposable<BoundBook> {
	
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
		Margins margins;
		if (common.getMargins().isValue()) {
			margins = common.getMargins().value();
		} else {
			margins = Margins.NONE;
		}
		
		/*
		 * If the margins are not to be mirrored, they should be set
		 * to the preprocessor. This will apply the margins to each page
		 * individually without considering whether they are verso or recto.
		 * 
		 * However, if they are to be mirrored, we resort to a hack:
		 * The {@code Page} object will have the dimensions of the content
		 * area only, ie. the whole page minus the mirrored margins.
		 * This means that the {@code Page} **will not cover the whole
		 * physical page**. The space remaining between the {@code Page}
		 * edges and the edges of the physical page is equal to the margins.
		 * Because the {@code Page} does not cover the whole physical page,
		 * it must be shifted from the original position in order for the
		 * margins to be of correct widths at all edges.
		 * This translation is applied to the Leaf prior to putting it onto
		 * the folded stack.
		 * 
		 * HACK: This is not the intended way to use {@object Page} objects.
		 * Providing a Leaf which covers the whole physical page, yet keeps
		 * the mirroring relationship between the margins on its opposite
		 * {@code Page}s, seems a far better and cleaner solution.
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
		DimensionSettings pageSize = common.getPageSize();
		DimensionSettings sheetSize = common.getSheetSize();
		if (pageSize == null) {
			throw new IllegalStateException("Page size is null");
		}
		if (sheetSize == null) {
			throw new IllegalStateException("Sheet size is null");
		}
		
		boolean autoPage = pageSize == DimensionSettings.AUTO;
		boolean autoSheet = sheetSize == DimensionSettings.AUTO;
		
		if (!autoPage && autoSheet) {
			// Do nothing
		} else if (autoPage && !autoSheet) {
			assert sheetSize.isValue();
			// Sheet size is given: determine page size
			pageSize = DimensionSettings.of
					(pageFromSheet(sheetSize.value(),binding, mirroredMargins));
			logger.verbose("booklet_sheetSizeToPageSize", pageSize, sheetSize, margins);
		} else if (!autoPage && !autoSheet){
			// Both are given: a conflict
			throw new IllegalStateException
				("Both sheet size and page size are set to a non-auto value");
		}

		/*
		 * Finally, make the preprocessor aware of the page size.
		 * In booklet imposition, the "cell" of the preprocessor corresponds
		 * to one page in the output booklet shrunk by margins.
		 * 
		 * If the page size is auto, no configuration is needed, because
		 * automatic page sizing is the default behaviour of Preprocessor.
		 * 
		 * If, however, a certain page size is desired, that size must
		 * be passed to the preprocessor as the cell size.
		 * Note that mirrored margins must be applied during the construction
		 * of the Signature object, and the preprocessor must work with
		 * Note that the preprocessor cell must be the size of the page minus
		 * the margins
		 * Note that the {@code Page} does not cover the whole physical page,
		 * so the active area known to preprocessor must be equal to only this
		 * reduces size.
		 */
		/*
		 * If the preprocessor resizing is not set (auto), make it fit
		 * the cell, otherwise the default resizing will leave the pages
		 * overlapping or with gaps in between.
		 */
		if (pageSize != DimensionSettings.AUTO) {
			assert pageSize.isValue();
			Dimensions cellSize = new Dimensions(
					Length.subtract(pageSize.value().width(), mirroredMargins.horizontal()),
					Length.subtract(pageSize.value().height(), mirroredMargins.vertical()));
			preprocess.setCellDimensions(cellSize);
			if (preprocess.getResizing() == Resizing.AUTO
					&& !preprocess.isScaleGiven()) {
				logger.verbose("booklet_setResizingToFit");
				preprocess.setResizing(Resizing.FIT);
			}
		}
		
		/*
		 * Now preprocess the pages and store the page dimensions,
		 * if they are still unknown.
		 */
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		if (pageSize == DimensionSettings.AUTO) {
			Dimensions cell = preprocessor.getResolvedCellDimensions();
			pageSize = DimensionSettings.of(new Dimensions(
					Length.sum(cell.width(), mirroredMargins.horizontal()),
					Length.sum(cell.height(), mirroredMargins.vertical())));
			if (logger.isTraceEnabled())
				logger.trace("Page size = preprocessor cell size + margins");
		}
		doc = preprocessor.processAll();
		
		// TODO: Fix this, sometimes shows -1
		logger.info("booklet_constructing", pageCount);
		
		/*
		 * Determine the number of pages in output.
		 * If the number is given explicitly, honor its value, otherwise
		 * take the number of pages in the input document.
		 * In any case, increase the value to the nearest integer multiple
		 * of four to reflect the fact that each output sheet contains four
		 * pages.
		 */
		PageSource source = pageSourceBuilder(common, doc).build();
		if (pageCount < 1) {
			// Page count is automatic: resolve from input document length
			pageCount = source.size();
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
		assert pageSize.isValue();
		SignatureMaker maker = new SignatureMaker
				(pageSize.value(), mirroredMargins, unit, pageCount);
		Signature signature = maker.makeSignature();
		signature.numberPagesFrom(1);
		volume.add(signature);
		
		/*
		 * Fill the volume with content.
		 */
		PageFillers.fillSequentially(volume.pages(), source);
		
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
	 * @return always the value of {@code false}
	 */
//	@Override
	@Deprecated
	public boolean prefersMultipleInput() {
		return false;
	}

//	@Override
	public BoundBook impose(VirtualDocument source) {
		return new BoundBook(imposeAsVolume(source));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Booklet handles multiple document input by first concatenating them
	 * into one document in the order they appear in the argument.
	 */
	@Override
	public BoundBook impose(List<VirtualDocument> sources) {
		return impose(VirtualDocument.concatenate(sources));
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
		public ImposableBuilder<Booklet> acceptPreprocessSettings(Settings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Preprocess settings cannot be null");
			this.preprocess = settings;
			return this;
		}
		
		@Override
		public ImposableBuilder<Booklet> acceptCommonSettings(CommonSettings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Settings cannot be null");
			this.common = settings;
			return this;
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
	 * applied to the individual pages and the margins passed to
	 * {@code SignatureMaker} must be zero.
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
		 * @param page total dimensions of the page (including margins)
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
						("The number of pages must be divisible by four");
			}
			
			this.leftMargin = margins.left().in(unit);
			this.rightMargin = margins.right().in(unit);
			this.bottomMargin = margins.bottom().in(unit);
			this.topMargin = margins.top().in(unit);
			this.totalWidth = page.width().in(unit);
			this.totalHeight = page.height().in(unit);
			this.contentWidth = totalWidth - leftMargin - rightMargin;
			this.contentHeight = totalHeight - bottomMargin - topMargin;
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

		// TODO Remove parameter tags from javadoc
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
	}
}
