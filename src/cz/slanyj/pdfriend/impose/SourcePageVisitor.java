package cz.slanyj.pdfriend.impose;

import cz.slanyj.pdfriend.impose.formats.PDFSourcePage;

/**
 * 
 * @author Singon
 *
 * @param <T> Return type of the visitor.
 * @param <P> Parameter type for the vistor.
 */
public interface SourcePageVisitor<T, P> {

	public T visit(PDFSourcePage page);
}
