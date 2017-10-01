package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

public abstract class AbstractImposable implements Imposable {

	@Override
	public VirtualDocument imposeAndRender(VirtualDocument source) {
		return impose(source).renderDocument();
	}

	@Override
	public VirtualDocument imposeAndRender(List<VirtualDocument> sources) {
		return impose(sources).renderDocument();
	}

}
