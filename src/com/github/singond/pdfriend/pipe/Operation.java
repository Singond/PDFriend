package com.github.singond.pdfriend.pipe;

import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleData;
import com.github.singond.pdfriend.modules.ModuleException;

/**
 * A single filter in the pipe.
 */
public class Operation {
	/** The module performing the action on the data */
	private final Module module;
	
	Operation(Module module) {
		this.module = module;
	}
	
	PipeData process(PipeData data) throws ModuleException {
		ModuleData mdata = module.process(data.getModuleData());
		return new PipeData(mdata);
	}
}
