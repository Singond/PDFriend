package com.github.singond.pdfriend.pipe;

import com.github.singond.pdfriend.modules.ModuleData;

/**
 * Data passed between operations of the pipe.
 * This is basically a wrapper around ModuleData.
 */
class PipeData {
	private final ModuleData moduleData;
	
	PipeData(ModuleData md) {
		this.moduleData = md;
	}

	public ModuleData getModuleData() {
		return moduleData;
	}
}
