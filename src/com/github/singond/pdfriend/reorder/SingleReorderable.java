package com.github.singond.pdfriend.reorder;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * Skeletal implementation of a {@code Reorderable} which prefers
 * a single {@code VirtualDocument} argument.
 *
 * @author Singon
 */
abstract class SingleReorderable implements Reorderable {

	@Override
	public VirtualDocument reorder(List<VirtualDocument> sources) {
		return reorder(VirtualDocument.concatenate(sources));
	}
	
}
