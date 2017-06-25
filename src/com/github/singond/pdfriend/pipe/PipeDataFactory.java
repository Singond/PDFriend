package com.github.singond.pdfriend.pipe;

import com.github.singond.pdfriend.document.VirtualDocument;

public class PipeDataFactory {

	public static PipeData of(VirtualDocument doc) {
		return new SingleDocPipeData(doc);
	}
}
