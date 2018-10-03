package com.github.singond.pdfriend.modules;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * The data being passed between modules.
 *
 * @author Singon
 *
 */
public interface ModuleData {

	public VirtualDocument asSingleDocument();
	
	public List<VirtualDocument> asMultipleDocuments();
}
