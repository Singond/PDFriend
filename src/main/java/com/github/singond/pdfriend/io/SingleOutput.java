package com.github.singond.pdfriend.io;

import java.io.OutputStream;

/**
 * A single output element.
 * @author Singon
 */
class SingleOutput implements Output {
	private final OutputElement output;

	SingleOutput(OutputElement output) {
		this.output = output;
	}

	@Override
	public OutputStream getOutputStream() throws OutputException {
		return output.getOutputStream();
	}
}
