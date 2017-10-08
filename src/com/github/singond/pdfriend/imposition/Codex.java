package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.github.singond.geometry.plane.Line;
import com.github.singond.geometry.plane.Point;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.BoundBook;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.Leaf;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.book.Signature;
import com.github.singond.pdfriend.book.SourceProvider;
import com.github.singond.pdfriend.book.Stack;
import com.github.singond.pdfriend.book.Volume;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;

/**
 * The common bound book, also called "codex", produced by folding a large
 * sheet of paper several times and binding the resulting sections together
 * to form the book block.
 *
 * @author Singon
 */
public class Codex extends AbstractImposable implements Imposable {

	/** The internal name of this imposable document type */
	private static final String NAME = "codex";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(Codex.class);

	private final int sheetsInSignature;
	private final List<SheetStackManipulation> manipulations;
	
	private final Preprocessor.Settings preprocess;
	private final CommonSettings common;
	private final LengthUnit unit = Imposition.LENGTH_UNIT;
	
	/**
	 * The sole constructor; this is to be used by {@Codex.Builder}.
	 * @param sheetsInSignature the number of sheets comprising each signature
	 * @param manipulations list of manipulations to be applied to the stack
	 *        of sheets in the order they are performed
	 * @param preprocess preprocessing settings
	 * @param common common imposition settings
	 */
	private Codex(int sheetsInSignature,
	             List<SheetStackManipulation> manipulations,
	             Preprocessor.Settings preprocess, CommonSettings common) {
		if (sheetsInSignature < 1)
			throw new IllegalArgumentException
					("The number of sheets in signature must be a positive number");
		if (manipulations == null)
			throw new IllegalArgumentException("The list of manipulations must not be null");
		if (preprocess == null)
			throw new IllegalArgumentException("Preprocessor settings must not be null");
		if (common == null)
			throw new IllegalArgumentException("Common settings must not be null");
		
		this.sheetsInSignature = sheetsInSignature;
		this.manipulations = new ArrayList<>(manipulations);
		this.preprocess = preprocess;
		this.common = common;
	}
	
	/**
	 * Returns a new {@code Codex.Builder}.
	 * The returned builder manipulates the sheets to that the
	 * <em>lower right</em> corner of the sheets stay fixed.
	 * <p>
	 * This kind of builder is suitable for building left-to-right books
	 * (ie. books with binding at the left side, viewed from the front),
	 * which are the most common type in the Western world.
	 * 
	 * @return a new builder instance
	 */
	public static final Builder rightBuilder() {
		return new RightBuilder();
	}

	/**
	 * Returns a new {@code Codex.Builder}.
	 * The returned builder manipulates the sheets to that the
	 * <em>lower left</em> corner of the sheets stay fixed.
	 * <p>
	 * This kind of builder is suitable for building right-to-left books
	 * (ie. books with binding at the right side, viewed from the front),
	 * which would be suitable for right-to-left languages such as Arabic
	 * or Hebrew.
	 * 
	 * @return a new builder instance
	 */
	public static final Builder leftBuilder() {
		return new LeftBuilder();
	}
	
	/** Creates testing Volume as a proof of concept. */
	@SuppressWarnings("unused")
	private Volume imposeAsVolumeTest(VirtualDocument doc) {
		double[] docDims = doc.maxPageDimensions();
		Dimensions sheetSize;
		Dimensions pageSize = new Dimensions(docDims[0], docDims[1], unit);
		
		sheetSize = sheetSizeFromPageSize(pageSize, manipulations);
		return buildVolume(sheetSize, pageSize, manipulations, doc);
	}
	
	/**
	 * Imposes the given virtual document into a new book volume
	 * according to the current settings of this {@code Codex} object.
	 */
	private Volume imposeAsVolume(VirtualDocument doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("imposition_preprocessSettings", preprocess);
			logger.debug("imposition_commonSettings", common);
//			logger.debug("imposition_imposableSettings", NAME, );
		}
		
		boolean autoPage = common.getPageSize() == CommonSettings.AUTO_DIMENSIONS;
		boolean autoSheet = common.getSheetSize() == CommonSettings.AUTO_DIMENSIONS;
		
		// Select use case and execute it
		if (autoPage) {
			if (autoSheet) {
				// Both are auto: use preferred page size and determine sheet size
				return caseAutoSize(doc);
			} else {
				// Only sheet size is given: determine page size
				return casePageSize(doc);
			}
		} else {
			if (autoSheet) {
				// Only page size is given: determine sheet size
				return caseSheetSize(doc);
			} else {
				// Both are given: a conflict
				throw new IllegalStateException
					("Both sheet size and page size are set to a non-auto value");
			}
		}
	}
	
	/**
	 * Assembles the final volume, using the preferred page size as the
	 * basis for the final sheet size.
	 * 
	 * @param doc the document to be imposed
	 * @return the document imposed into a new volume
	 */
	private Volume caseAutoSize(VirtualDocument doc) {
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		Dimensions pageSize = preprocessor.getResolvedCellDimensions();
		Dimensions sheetSize = sheetSizeFromPageSize(pageSize, manipulations);
		doc = preprocessDocument(doc, preprocessor, 0); // last arg is not used
		return buildVolume(sheetSize, pageSize, manipulations, doc);
	}
	
	/**
	 * Assembles the final volume, determining the page size from the
	 * other parameters.
	 * 
	 * @param doc the document to be imposed
	 * @return the document imposed into a new volume
	 */
	private Volume casePageSize(VirtualDocument doc) {
		throw new UnsupportedOperationException("Use case not implemented yet");
	}
	
	/**
	 * Assembles the final volume, determining the sheet size from the
	 * other parameters.
	 * 
	 * @param doc the document to be imposed
	 * @return the document imposed into a new volume
	 */
	private Volume caseSheetSize(VirtualDocument doc) {
		throw new UnsupportedOperationException("Use case not implemented yet");
	}
	
	/**
	 * Determines the sheet size necessary to produce the target page size
	 * by modifying the sheet by the given manipulations.
	 * @param pageSize the target sheet size
	 * @param manipulations the list of modifications to the sheet, like folding
	 * @return the sheet size necessary to produce the target page size
	 */
	private Dimensions sheetSizeFromPageSize(Dimensions pageSize,
			List<SheetStackManipulation> manipulations) {
		// Initialize sheet size to page size
		final SheetDimensions shDims = new SheetDimensions(
				pageSize.width().in(unit), pageSize.height().in(unit));
		
		// Modify the sheet size by applying the manipulations in reverse order
		ListIterator<SheetStackManipulation> it;
		it = manipulations.listIterator(manipulations.size());
		while (it.hasPrevious()) {
			it.previous().accommodateSheetDimensions(shDims);
		}
		return new Dimensions(shDims.width, shDims.height, unit);
	}
	
	/**
	 * Builds a Volume by creating signatures from given stack properties,
	 * filling them with pages of the given source document and appending
	 * them to a volume, until all pages of the source are processed.
	 * @param sheetSize
	 * @param pageSize
	 * @param manipulations
	 * @param doc the source document to be imposed
	 * @return a new instance of {@code Volume}
	 */
	private Volume buildVolume(Dimensions sheetSize, Dimensions pageSize,
	                           List<SheetStackManipulation> manipulations,
	                           VirtualDocument doc) {
		// A factory to provide instances of Signature
		SignatureFactory sf = new SignatureFactory(sheetSize, pageSize, manipulations);
		// Source provider to fill pages with content
		SourceProvider<Page> sp = new SequentialSourceProvider(doc);
		// The final volume to be returned
		Volume volume = new Volume();
		
		/*
		 * As long as there are more pages in the provider, create new
		 * signatures and add them to the volume.
		 */
		int pageNumber = 1;
		while (sp.hasNextPage()) {
			Signature s = sf.newSignature();
			sp.setSourceTo(s.pages());
			pageNumber = s.numberPagesFrom(pageNumber);
			volume.add(s);
		}
		return volume;
	}
	
	/**
	 * A factory to provide instances of {@code Signature} based on the given
	 * stack properties.
	 */
	private class SignatureFactory {
		private final Leaf leaf;
		private final SheetStack stack;
		
		SignatureFactory(Dimensions sheetSize, Dimensions pageSize,
		                 List<SheetStackManipulation> manipulations) {
			if (sheetSize == null)
				throw new IllegalArgumentException
						("The sheet size must not be null");
			if (pageSize == null)
				throw new IllegalArgumentException
						("The page size must not be null");
			if (manipulations == null)
				throw new IllegalArgumentException
						("The list of manipulations must not be null");
			
			stack = buildSheetStack(sheetSize, manipulations);
			leaf = new Leaf(pageSize.width().in(unit), pageSize.height().in(unit));
		}
		
		private final SheetStack buildSheetStack(Dimensions sheetSize,
		                                   List<SheetStackManipulation> manipulations) {
			// Build the sheet stack
			SheetStack shStack = new SheetStack(sheetSize, sheetsInSignature);
			for (SheetStackManipulation m : manipulations) {
				m.putToStack(shStack);
			}
			return shStack;
		}
		
		/**
		 * Builds a new {@code Signature} from the stack
		 * @return a new instance on each invocation
		 */
		Signature newSignature() {
			return stack.stack.copy().buildSignature(leaf);
		};
	}
	
	/** Preprocess the given document */
	// TODO Use pageCount parameter
	private VirtualDocument preprocessDocument(
			VirtualDocument doc, Preprocessor preprocessor, int pageCount) {
		return preprocessor.processDocument(doc);
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
	public BoundBook impose(VirtualDocument source) {
		return new BoundBook(imposeAsVolume(source));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Codex handles multiple document input by first concatenating them
	 * into one document in the order they appear in the argument.
	 */
	@Override
	public BoundBook impose(List<VirtualDocument> sources) {
		return impose(VirtualDocument.concatenate(sources));
	}

	/**
	 * A builder for {@code Codex} objects. This is the only public way to
	 * instantiate {@code Codex}.
	 * <p>
	 * Note that the list of manipulations in this builder is one-shot,
	 * ie. it is not possible to remove manipulations from it.
	 */
	public interface Builder extends ImposableBuilder<Codex> {
		/**
		 * Returns the number of sheets comprising each signature.
		 * @return the number of sheets
		 */
		public int getSheetsInSignature();

		/**
		 * Sets the number of sheets comprising each signature.
		 * If this value is not set, the default value of 1 is used.
		 * @param sheetsInSignature the number of sheets in each signature
		 * @return this {@code Builder} instance
		 */
		public Builder setSheetsInSignature(int sheetsInSignature);

		/**
		 * Folds the stack of sheets along a horizontal line in the middle
		 * of the sheet height, folding down.
		 * @return this {@code Builder} instance
		 */
		public Builder foldHorizontallyDown();
		
		/**
		 * Folds the stack of sheets along a horizontal line in the middle
		 * of the sheet height, folding up.
		 * @return this {@code Builder} instance
		 */
		public Builder foldHorizontallyUp();
		
		/**
		 * Folds the stack of sheets along a vertical line in the middle
		 * of the sheet width, folding down.
		 * @return this {@code Builder} instance
		 */
		public Builder foldVerticallyDown();
		
		/**
		 * Folds the stack of sheets along a vertical line in the middle
		 * of the sheet width, folding up.
		 * @return this {@code Builder} instance
		 */
		public Builder foldVerticallyUp();
	}
	
	/**
	 * A skeletal implementation of the {@code Builder} interface.
	 * <p>
	 * Note that the list of manipulations in this builder is one-shot,
	 * ie. it is not possible to remove manipulations from it.
	 */
	private static abstract class AbstractBuilder
			extends AbstractImposableBuilder<Codex> implements Builder {
		int sheetsInSignature = 1;
		final List<SheetStackManipulation> manipulations;
		
		/**
		 * Creates a new {@code Builder} object with default settings.
		 */
		private AbstractBuilder() {
			manipulations = new ArrayList<>();
		}
		
		void manipulate(SheetStackManipulation manipulation) {
			manipulations.add(manipulation);
		};
		
		/**
		 * Returns the number of sheets comprising each signature.
		 * @return the number of sheets
		 */
		@Override
		public int getSheetsInSignature() {
			return sheetsInSignature;
		}

		/**
		 * Sets the number of sheets comprising each signature.
		 * If this value is not set, the default value of 1 is used.
		 * @param sheetsInSignature the number of sheets in each signature
		 * @return this {@code Builder} instance
		 */
		@Override
		public Builder setSheetsInSignature(int sheetsInSignature) {
			if (sheetsInSignature < 1)
				throw new IllegalArgumentException
						("The number of sheets in signature must be a positive number");
			this.sheetsInSignature = sheetsInSignature;
			return this;
		}

		/**
		 * Returns a new instance of {@code Codex} from the current properties
		 * of this {@code Builder}.
		 */
		@Override
		public Codex build() {
			return new Codex(sheetsInSignature, manipulations, preprocess, common);
		}
	}
	
	/**
	 * This builder manipulates the sheets to that the lower left corner
	 * of the sheets stay fixed.
	 */
	private static final class LeftBuilder extends AbstractBuilder {

		@Override
		public Builder foldHorizontallyDown() {
			manipulate(new HorizontalFoldInHalf(FoldDirection.UNDER));
			return this;
		}
		
		@Override
		public Builder foldHorizontallyUp() {
			manipulate(new HorizontalFoldInHalf(FoldDirection.OVER));
			return this;
		}
		
		@Override
		public Builder foldVerticallyDown() {
			manipulate(new VerticalFoldInHalf(FoldDirection.UNDER));
			return this;
		}
		
		@Override
		public Builder foldVerticallyUp() {
			manipulate(new VerticalFoldInHalf(FoldDirection.OVER));
			return this;
		}
	}
	
	/**
	 * This builder manipulates the sheets to that the lower right corner
	 * of the sheets stay fixed.
	 */
	private static final class RightBuilder extends AbstractBuilder {

		/*
		 * Implementation note:
		 * 
		 * The requirement that the static point is the lower right corner,
		 * instead of the lower left corner (which is the way that Stack
		 * handles the sheets), is implemented by performing modifications
		 * on the sheet in the default position (with the fixed point in
		 * the lower left corner) and flipping the stack of sheets
		 * vertically in the end.
		 * One implication of this is that the manipulations must undergo
		 * some correction: For example, folding "up" is implemented as
		 * folding "down" (because the sheet is flipped; see above).
		 */
		
		@Override
		public Builder foldHorizontallyDown() {
			manipulate(new HorizontalFoldInHalf(FoldDirection.OVER));
			return this;
		}
		
		@Override
		public Builder foldHorizontallyUp() {
			manipulate(new HorizontalFoldInHalf(FoldDirection.UNDER));
			return this;
		}
		
		@Override
		public Builder foldVerticallyDown() {
			manipulate(new VerticalFoldInHalf(FoldDirection.OVER));
			return this;
		}
		
		@Override
		public Builder foldVerticallyUp() {
			manipulate(new VerticalFoldInHalf(FoldDirection.UNDER));
			return this;
		}
		
		@Override
		public Codex build() {
			/*
			 * Flip the stack horizontally so as to bring the free edge of paper
			 * to the right side.
			 */
			List<SheetStackManipulation> manips = new ArrayList<>(manipulations);
			manips.add(new HorizontalFlip());

			return new Codex(sheetsInSignature, manips, preprocess, common);
		}
	}
	
	private final class SheetStack {
		private final Stack stack;
		private double currentWidth;
		private double currentHeight;
		
		/**
		 * Constructs a new SheetStack
		 * @param originalDimensions the dimensions of the unfolded sheet
		 * @param sheetCount the number of sheets to stack before applying
		 *        the manipulations given
		 */
		private SheetStack(Dimensions originalDimensions, int sheetCount) {
			currentWidth = originalDimensions.width().in(unit);
			currentHeight = originalDimensions.height().in(unit);
			stack = new Stack(currentWidth, currentHeight);
			stack.performManipulation(new Stack.Gather(sheetCount));
		}
		
		private void manipulate(Stack.Manipulation manipulation) {
			stack.performManipulation(manipulation);
		}
	}
	
	private final class SheetDimensions {
		private double width;
		private double height;
		
		private SheetDimensions(double width, double height) {
			this.width = width;
			this.height = height;
		}
	}
	
	private interface SheetStackManipulation {
		
		/**
		 * Modifies the given sheet dimensions in such a way, that performing
		 * this manipulation on the modified dimensions will produce a sheet
		 * of the original dimensions (before modifying).
		 * @param dimensions the dimensions to be modified
		 */
		void accommodateSheetDimensions(SheetDimensions dimensions);
		
		/**
		 * Applies this manipulation to the given stack.
		 */
		void putToStack(SheetStack stack);
	}
	
	private static abstract class Fold implements SheetStackManipulation {
		protected final FoldDirection direction;
		
		Fold(FoldDirection direction) {
			this.direction = direction;
		}
	}
	
	/**
	 * A fold along a horizontal line in the middle of the sheet's height.
	 */
	private static class HorizontalFoldInHalf extends Fold implements SheetStackManipulation {
		
		HorizontalFoldInHalf(FoldDirection direction) {
			super(direction);
		}
		
		@Override
		public void accommodateSheetDimensions(SheetDimensions dimensions) {
			dimensions.height *= 2;
		}

		@Override
		public void putToStack(SheetStack stack) {
			double halfHeight = stack.currentHeight / 2;
			Line foldAxis = new Line(new Point(0, halfHeight), new Point(1, halfHeight));
			stack.manipulate(new Stack.Fold(foldAxis, direction.value()));
			stack.currentHeight = halfHeight;
		}
	}
	
	/**
	 * A fold along a vertical line in the middle of the sheet's width.
	 */
	private static class VerticalFoldInHalf extends Fold implements SheetStackManipulation {

		VerticalFoldInHalf(FoldDirection direction) {
			super(direction);
		}
		
		@Override
		public void accommodateSheetDimensions(SheetDimensions dimensions) {
			dimensions.width *= 2;
		}

		@Override
		public void putToStack(SheetStack stack) {
			double halfWidth = stack.currentWidth / 2;
			Line foldAxis = new Line(new Point(halfWidth, 0), new Point(halfWidth, 1));
			stack.manipulate(new Stack.Fold(foldAxis, direction.value()));
			stack.currentWidth = halfWidth;
		}
	}
	
	/**
	 * A flip along the vertical axis.
	 */
	private static class HorizontalFlip implements SheetStackManipulation {
		
		@Override
		public void accommodateSheetDimensions(SheetDimensions dimensions) {
			// Do nothing
		}

		@Override
		public void putToStack(SheetStack stack) {
			stack.manipulate(Stack.Flip.horizontal(stack.currentWidth));
		}
	}
	
	private enum FoldDirection {
		UNDER(Stack.Fold.Direction.UNDER),
		OVER(Stack.Fold.Direction.OVER);
		
		private final Stack.Fold.Direction value;
		
		private FoldDirection(Stack.Fold.Direction val) {
			value = val;
		}
		
		Stack.Fold.Direction value() {
			return value;
		}
	}
}
