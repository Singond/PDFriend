package com.github.singond.pdfriend.modules;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

class MultiDocModuleData implements ModuleData {
	
	/** The list of documents */
	private final List<VirtualDocument> documents;
	
	MultiDocModuleData(List<VirtualDocument> docs) {
		this.documents = new ArrayList<>(docs);
	}
	
	MultiDocModuleData(VirtualDocument document) {
		List<VirtualDocument> list = new ArrayList<>();
		list.add(document);
		this.documents = list;
	}

	@Override
	public VirtualDocument asSingleDocument() {
		if (documents.size() == 1)
			return documents.get(0);
		else
			return VirtualDocument.concatenate(documents);
	}

	@Override
	public List<VirtualDocument> asMultipleDocuments() {
		return new ArrayList<>(documents);
	}
}
