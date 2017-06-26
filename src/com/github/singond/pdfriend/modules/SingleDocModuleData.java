package com.github.singond.pdfriend.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

class SingleDocModuleData implements ModuleData {

	/** The sole document in this unit of pipe data */
	private final VirtualDocument document;
	
	/** The document viewed as a list */
	private List<VirtualDocument> asList;

	SingleDocModuleData(VirtualDocument document) {
		this.document = document;
	}

	@Override
	public VirtualDocument asSingleDocument() {
		return document;
	}

	@Override
	public List<VirtualDocument> asMultipleDocuments() {
		if (asList == null) {
			List<VirtualDocument> basicList = new ArrayList<>(1);
			basicList.add(document);
			List<VirtualDocument> unmodifiableList = Collections.unmodifiableList(basicList);
			asList = unmodifiableList;
			return unmodifiableList;
		} else {
			return asList;
		}
	}
	
	
}
