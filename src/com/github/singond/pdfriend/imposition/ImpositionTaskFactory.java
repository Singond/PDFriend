package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.book.FlipDirection;
import com.github.singond.pdfriend.book.OneSidedBook;
import com.github.singond.pdfriend.book.TwoSidedBook;
import com.github.singond.pdfriend.document.VirtualDocument;

class ImpositionTaskFactory {

	private ImpositionTaskFactory() {
		throw new AssertionError("This is a non-instantiable class");
	}
	
	static ImpositionTask oneSided(Imposable<? extends OneSidedBook> imposable) {
		return new OneSidedImpositionTask(imposable);
	}
	
	static ImpositionTask twoSided(Imposable<? extends TwoSidedBook> imposable,
	                               FlipDirection flip) {
		return new TwoSidedImpositionTask(imposable, flip);
	}
	
	/**
	 * An imposition task which treats documents as one-sided.
	 */
	private static class OneSidedImpositionTask implements ImpositionTask {

		private final Imposable<? extends OneSidedBook> imposable;
		
		private OneSidedImpositionTask
				(Imposable<? extends OneSidedBook> imposable) {
			this.imposable = imposable;
		}

		@Override
		public String getName() {
			return imposable.getName();
		}

		@Override
		public VirtualDocument process(List<VirtualDocument> sources) {
			return imposable.impose(sources).renderOneSided();
		}
	}
	
	/**
	 * An imposition task which treats documents as two-sided.
	 */
	private static class TwoSidedImpositionTask implements ImpositionTask {

		private final Imposable<? extends TwoSidedBook> imposable;
		private final FlipDirection flip;
		
		private TwoSidedImpositionTask(
				Imposable<? extends TwoSidedBook> imposable,
				FlipDirection flip) {
			this.imposable = imposable;
			this.flip = flip;
		}

		@Override
		public String getName() {
			return imposable.getName();
		}

		@Override
		public VirtualDocument process(List<VirtualDocument> sources) {
			return imposable.impose(sources).renderTwoSided(flip);
		}
	}
}
