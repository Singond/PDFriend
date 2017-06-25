package com.github.singond.pdfriend.pipe;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

class MultiDocPipeData implements PipeData {
	
	/** The list of documents */
	private final List<VirtualDocument> documents;
	
	MultiDocPipeData(List<VirtualDocument> docs) {
		this.documents = new ArrayList<>(docs);
	}
	
	MultiDocPipeData(VirtualDocument document) {
		List<VirtualDocument> list = new ArrayList<>();
		list.add(document);
		this.documents = list;
	}

	@Override
	public VirtualDocument asSingleDocument() {
		return VirtualDocument.concatenate(documents);
	}

	@Override
	public List<VirtualDocument> asMultipleDocuments() {
		return new ArrayList<>(documents);
	}
}
