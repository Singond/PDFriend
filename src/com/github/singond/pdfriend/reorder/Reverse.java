package com.github.singond.pdfriend.reorder;

import java.util.ListIterator;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;

public class Reverse extends SingleReorderable implements Reorderable {

	/** The internal name of this reordering task */
	private static final String NAME = "reverse";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public VirtualDocument reorder(VirtualDocument source) {
		VirtualDocument.Builder output = new VirtualDocument.Builder();
		ListIterator<VirtualPage> iter = source.iterator(source.getLength());
		while (iter.hasPrevious()) {
			output.addPage(iter.previous());
		}
		return output.build();
	}

}
