package com.github.singond.pdfriend.reorder;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

public class Compact implements Reorderable {

	/** The internal name of this reordering task */
	private static final String NAME = "compact";

	private final Compacter<VirtualDocument> compacter;

	Compact(int size) {
		compacter = new OptimizingFixedSlotCompacter<>(size);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public VirtualDocument reorder(List<VirtualDocument> sources) {
		List<VirtualDocument> compacted = compacter.process(sources, d -> d.getLength());
		return VirtualDocument.concatenate(compacted);
	}

}
