package com.github.singond.pdfriend.io;

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
	public void acceptBytes(byte[] bytes) throws OutputException {
		output.acceptBytes(bytes);
	}
}
