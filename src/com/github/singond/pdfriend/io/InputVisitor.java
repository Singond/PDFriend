package com.github.singond.pdfriend.io;

/**
 * A visitor for Input objects.
 *
 * @author Singon
 *
 * @param <T> the return type of the visitor
 * @param <P> the parameter type for the visitor
 */
@Deprecated
interface InputVisitor<T, P> {

	public T visit(FileInput input, P param) throws InputException;
}
