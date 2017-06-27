package com.github.singond.pdfriend.io;

/**
 * A visitor for Input objects.
 *
 * @author Singon
 *
 * @param <T> the return type of the visitor
 * @param <P> the parameter type for the visitor
 */
public interface OutputVisitor <T, P> {

	public T visit(FileOutput output, P param) throws OutputException;
}
