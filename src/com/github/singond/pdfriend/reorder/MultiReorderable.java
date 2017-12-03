package com.github.singond.pdfriend.reorder;

import java.util.Collections;
import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * Skeletal implementation of a {@code Reorderable} which prefers
 * multiple {@code VirtualDocument} arguments.
 *
 * @author Singon
 */
abstract class MultiReorderable implements Reorderable {

	@Override
	public VirtualDocument reorder(VirtualDocument source) {
		return reorder(Collections.singletonList(source));
	}
	
}
