package com.github.singond.pdfriend.document;

import com.github.singond.pdfriend.format.content.PDFPage;
/**
 * An abstract implementation of a ContentVisitor.
 * Enables overriding selected methods only.
 * @param <T> Return type of the visitor.
 * @param <P> Parameter type for the vistor.
 * @param <E> Exception type thrown by the visitor.
 */
public abstract class AContentVisitor<T, P, E extends Throwable> implements ContentVisitor<T, P, E> {

	@Override
	public T visit(PDFPage c, P param) throws E {
		throw new UnsupportedOperationException("This ContentVisitor has not been implemented for "+c.getClass());
	}

}
