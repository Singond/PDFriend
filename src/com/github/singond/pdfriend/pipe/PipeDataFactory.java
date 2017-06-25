package com.github.singond.pdfriend.pipe;

import java.util.Arrays;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

public class PipeDataFactory {

	public static PipeData of(VirtualDocument doc) {
		return new SingleDocPipeData(doc);
	}
	
	public static PipeData of(VirtualDocument... docs) {
		return new MultiDocPipeData(Arrays.asList(docs));
	}
	
	public static PipeData of(List<VirtualDocument> docs) {
		return new MultiDocPipeData(docs);
	}
}
