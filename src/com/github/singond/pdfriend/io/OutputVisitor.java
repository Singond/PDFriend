package com.github.singond.pdfriend.io;

public interface OutputVisitor {

	public <T, P> T visit(Output output, P param) throws OutputException;
}
