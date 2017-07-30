package com.github.singond.pdfriend.pipe;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.RenderingManager;
import com.github.singond.pdfriend.io.Output;
import com.github.singond.pdfriend.io.OutputException;

/**
 * A pipe output provider which accepts all data as one unit.
 * @author Singon
 */
class SimpleOutput implements PipeOutput {
	private Output output;
	private boolean written = false;
	private RenderingManager rmgr = new RenderingManager();
	
	SimpleOutput(Output output) {
		this.output = output;
	}

	@Override
	public void consumePipeData(PipeData data) throws PipeException {
		if (written) {
			throw new IllegalStateException("This output has already been written out");
		}
		List<VirtualDocument> docs = data.getModuleData().asMultipleDocuments();
		try {
			rmgr.renderDocuments(docs, output);
		} catch (OutputException | RenderingException e) {
			throw new PipeException(e);
		}
	}
}
