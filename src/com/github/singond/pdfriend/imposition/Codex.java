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

	private final List<SheetStackManipulation> manipulations;
	private final Preprocessor.Settings preprocess;
	private final CommonSettings common;
	private final LengthUnit unit = Imposition.LENGTH_UNIT;
	
	public Codex(List<SheetStackManipulation> manipulations,
	             Preprocessor.Settings preprocess, CommonSettings common) {
		this.manipulations = new ArrayList<>(manipulations);
		this.preprocess = preprocess;
		this.common = common;
	}
	
	/** Creates testing Volume as a proof of concept. */
	private Volume imposeAsVolumeTest(VirtualDocument doc) {
		double[] docDims = doc.maxPageDimensions();
		Dimensions sheetSize;
		Dimensions pageSize = new Dimensions(docDims[0], docDims[1], unit);
		
		final SheetDimensions shDims = new SheetDimensions(
				pageSize.width().in(unit), pageSize.height().in(unit));
		// Calculate sheet size required to produce given page size
		ListIterator<SheetStackManipulation> it;
		it = manipulations.listIterator(manipulations.size());
		while (it.hasPrevious()) {
			it.previous().accommodateSheetDimensions(shDims);
		}
		sheetSize = new Dimensions(shDims.width, shDims.height, unit);
		
		SheetStack shStack = new SheetStack(sheetSize);
		for (SheetStackManipulation m : manipulations) {
			m.putToStack(shStack);
		}
		
		SourceProvider<Page> sp = new SequentialSourceProvider(doc);
		Leaf template = new Leaf(pageSize.width().in(unit), pageSize.height().in(unit));
		Volume volume = new Volume();
		int pageNumber = 1;
		while (sp.hasNextPage()) {
			Signature s = shStack.stack.buildSignature(template);
			sp.setSourceTo(s.pages());
			pageNumber = s.numberPagesFrom(pageNumber);
			volume.add(s);
		}
		return volume;
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
		return new BoundBook(imposeAsVolumeTest(source));
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

	public static final class Builder extends AbstractImposableBuilder<Codex> {
		private final List<SheetStackManipulation> manipulations;
		
		public Builder() {
			manipulations = new ArrayList<>();
		}
		
		public Builder foldHorizontallyDown() {
			manipulations.add(new HorizontalFoldInHalf(FoldDirection.UNDER));
			return this;
		}
		
		public Builder foldHorizontallyUp() {
			manipulations.add(new HorizontalFoldInHalf(FoldDirection.OVER));
			return this;
		}
		
		public Builder foldVerticallyDown() {
			manipulations.add(new VerticalFoldInHalf(FoldDirection.UNDER));
			return this;
		}
		
		public Builder foldVerticallyUp() {
			manipulations.add(new VerticalFoldInHalf(FoldDirection.OVER));
			return this;
		}
		
		public Codex build() {
			return new Codex(manipulations, preprocess, common);
		}
	}
	
	private final class SheetStack {
		private final Stack stack;
		private double currentWidth;
		private double currentHeight;
		
		/**
		 * Constructs a new SheetStack
		 * @param originalDimensions the dimensions of the unfolded sheet
		 */
		private SheetStack(Dimensions originalDimensions) {
			currentWidth = originalDimensions.width().in(unit);
			currentHeight = originalDimensions.height().in(unit);
			stack = new Stack(currentWidth, currentHeight);
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
	 * A folds along a horizontal line in the middle of the sheet's height.
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
	 * A folds along a vertical line in the middle of the sheet's width.
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
