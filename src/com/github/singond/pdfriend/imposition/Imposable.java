package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.book.Book;
import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A represetation of a document produced by imposition.
 * @author Singon
 * @param <T> the type of output document model
 */
public interface Imposable<T extends Book> {

	/**
	 * Returns a name of this imposition task.
	 * This should be a short and simple description to be used internally
	 * and for logging, not a pretty name meant for end-users.
	 */
	public String getName();
	
	/**
	 * Imposes the given multiple source documents into a new document model.
	 * @param sources the documents to be imposed
	 * @return the document resulting from imposing the input documents
	 */
	public T impose(List<VirtualDocument> sources);
}
