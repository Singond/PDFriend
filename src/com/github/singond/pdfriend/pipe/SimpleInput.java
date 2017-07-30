package com.github.singond.pdfriend.pipe;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ImportException;
import com.github.singond.pdfriend.format.ImportManager;
import com.github.singond.pdfriend.io.Input;
import com.github.singond.pdfriend.io.InputException;
import com.github.singond.pdfriend.modules.ModuleData;
import com.github.singond.pdfriend.modules.ModuleDataFactory;

/**
 * A pipe input provider which sends all input for processing as one unit.
 * @author Singon
 */
class SimpleInput implements PipeInput {
	private Input input;
	private boolean consumed = false;
	private final ImportManager imgr = new ImportManager();
	
	SimpleInput(Input input) {
		this.input = input;
	}
	
	@Override
	public PipeData getPipeData() throws PipeException {
		if (consumed) {
			throw new IllegalStateException("This input has already been consumed");
		}
		try {
			List<VirtualDocument> docs = imgr.importAsDocuments(input);
			ModuleData md = ModuleDataFactory.of(docs);
			PipeData pd = new PipeData(md);
			consumed = true;
			return pd;
		} catch (InputException | ImportException e) {
			throw new PipeException(e);
		}
	}

	@Override
	public boolean hasMore() {
		return !consumed;
	}

}
