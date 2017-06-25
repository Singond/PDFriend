package com.github.singond.pdfriend.pipe;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * The documents flowing between modules in the pipe.
 *
 * @author Singon
 *
 */
public interface PipeData {

	public VirtualDocument asSingleDocument();
	
	public List<VirtualDocument> asMultipleDocuments();
}
