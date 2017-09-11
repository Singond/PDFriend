package com.github.singond.pdfriend.imposition;

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
	
	/** Renders this imposed document into a new virtual document. */
//	public VirtualDocument impose(VirtualDocument source);
}
