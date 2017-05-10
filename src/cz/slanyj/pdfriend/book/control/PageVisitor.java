package cz.slanyj.pdfriend.book.control;

import cz.slanyj.pdfriend.book.model.SinglePage;

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
}
