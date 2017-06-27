package com.github.singond.pdfriend.io;

public interface InputVisitor {

	public <T, P, E extends Throwable> T visit(Input input, P param) throws E;
}
