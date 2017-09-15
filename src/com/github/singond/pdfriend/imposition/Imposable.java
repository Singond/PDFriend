package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A represetation of a document produced by imposition.
 * @author Singon
 */
public interface Imposable {

	/**
	 * Returns a name of this imposition task.
	 * This should be a short and simple description to be used internally
	 * and for logging, not a pretty name meant for end-users.
	 */
	public String getName();
	
	/**
	 * Sets the parameters to be used when pre-processing the pages.
	 * @param settings
	 */
	public void acceptPreprocessSettings(Preprocessor.Settings settings);
	
	/**
	 * Sets the common imposition settings to be used.
	 * @param settings
	 */
	public void acceptCommonSettings(CommonSettings settings);
	
	/**
	 * Returns whether this imposable prefers the input to be multiple
	 * documents instead of a single document.
	 * This effectively means that the {@code Imposable} prefers to be
	 * called using {@code impose(List<VirtualDocument>)} rather than
	 * {@code impose(VirtualDocument)}.
	 * @return {@code true} if this imposition type makes (more) sense when
	 *         applied to multiple document instead of multiple ones
	 */
	public boolean prefersMultipleInput();
	
	/**
	 * Imposes the given single source document into a new virtual document.
	 * @param source the document to be imposed
	 * @return the imposed document
	 * @throws UnsupportedOperationException if this imposition type does
	 *        not support imposing a single document
	 */
	public VirtualDocument impose(VirtualDocument source);
	
	/**
	 * Imposes the given multiple source documents into a new virtual document.
	 * @param sources the list of documents to be imposed
	 * @return the document resulting from imposing all the input documents
	 * @throws UnsupportedOperationException if this imposition type does
	 *        not support imposing multiple documents
	 */
	public VirtualDocument impose(List<VirtualDocument> sources);
}
