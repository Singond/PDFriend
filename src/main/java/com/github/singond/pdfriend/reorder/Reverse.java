package com.github.singond.pdfriend.reorder;

import java.util.Collections;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

public class Reverse implements Reorderable {

	/** The internal name of this reordering task */
	private static final String NAME = "reverse";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public VirtualDocument reorder(List<VirtualDocument> sources) {
		VirtualDocument.Builder output = new VirtualDocument.Builder();
		for (VirtualDocument doc : sources) {
			output.appendDocument(doc);
		}
		Collections.reverse(output.getPages());
		return output.build();
	}

}
