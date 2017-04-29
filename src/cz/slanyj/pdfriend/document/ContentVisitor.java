package cz.slanyj.pdfriend.document;

import cz.slanyj.pdfriend.format.content.PDFPage;

/**
 * A Visitor class for the Content type.
 * 
 * @author Singon
 *
 * @param <T> Return type of the visitor.
 * @param <P> Parameter type for the vistor.
 */
public interface ContentVisitor<T, P> {

	public T visit(Content c, P param);
	public T visit(PDFPage c, P param);
}
