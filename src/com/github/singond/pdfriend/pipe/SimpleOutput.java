package com.github.singond.pdfriend.pipe;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.io.Output;
import com.github.singond.pdfriend.io.OutputException;

/**
 * A pipe output provider which accepts all data as one unit.
 * @author Singon
 */
class SimpleOutput implements PipeOutput {
	private Output output;
	private boolean written = false;
	
	SimpleOutput(Output output) {
		this.output = output;
	}

	@Override
	public void consumePipeData(PipeData data) throws PipeException {
		if (written) {
			throw new IllegalStateException("This output has already been written out");
		}
		VirtualDocument doc = data.getModuleData().asSingleDocument();
		try {
			output.acceptBytes(new PDFRenderer().renderRaw(doc));
			written = true;
		} catch (OutputException | RenderingException e) {
			throw new PipeException(e);
		}
	}
}
