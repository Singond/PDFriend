package com.github.singond.pdfriend.document;

import com.github.singond.pdfriend.format.content.PDFPage;

/**
 * A Visitor class for the Content type.
 * 
 * @author Singon
 *
 * @param <T> Return type of the visitor.
 * @param <P> Parameter type for the vistor.
 * @param <E> Exception type thrown by the visitor.
 */
public interface ContentVisitor<T, P, E extends Throwable> {

	public T visit(PDFPage c, P param) throws E;
}
