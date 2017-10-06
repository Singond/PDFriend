package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.geometry.plane.Line;
import com.github.singond.geometry.plane.Point;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.BoundBook;
import com.github.singond.pdfriend.book.Stack;
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
//		return new BoundBook(imposeAsVolume(source));
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("This method has not been implemented yet");
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
		
		public void foldHorizontallyDown() {
			manipulations.add(new HorizontalFoldInHalf(FoldDirection.UNDER));
		}
		
		public void foldHorizontallyUp() {
			manipulations.add(new HorizontalFoldInHalf(FoldDirection.OVER));
		}
		
		public void foldVerticallyDown() {
			manipulations.add(new VerticalFoldInHalf(FoldDirection.UNDER));
		}
		
		public void foldVerticallyUp() {
			manipulations.add(new VerticalFoldInHalf(FoldDirection.OVER));
		}
		
		public Codex build() {
			return new Codex(manipulations, preprocess, common);
		}
	}
	
	private class SheetStack {
		private final Stack stack;
		private double currentWidth;
		private double currentHeight;
		
		private SheetStack(Dimensions originalDimensions) {
			currentWidth = originalDimensions.width().in(unit);
			currentHeight = originalDimensions.height().in(unit);
			stack = new Stack(currentWidth, currentHeight);
		}
		
		private void manipulate(Stack.Manipulation manipulation) {
			stack.performManipulation(manipulation);
		}
	}
	
	private interface SheetStackManipulation {
		/**
		 * Registers this operation with the Stack to be performed later.
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
