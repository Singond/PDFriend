package com.github.singond.pdfriend.book;

/**
 * A Visitor for Page objects.
 *
 * @author Singon
 *
 * @param <R> Return type of the visitor.
 * @param <P> Parameter type for the vistor.
 * @param <E> Exception type thrown by the visitor.
 */
public interface PageVisitor<R, P, E extends Throwable> {

	public R visit(SinglePage p, P param) throws E;
	public R visit(MultiPage p, P param) throws E;
	public R visit(GridPage p, P param) throws E;
	public R visit(LayeredPage p, P param) throws E;
}
