package com.github.singond.pdfriend.modules;

import java.util.Arrays;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;

public class ModuleDataFactory {

	public static ModuleData of(VirtualDocument doc) {
		return new SingleDocModuleData(doc);
	}
	
	public static ModuleData of(VirtualDocument... docs) {
		return new MultiDocModuleData(Arrays.asList(docs));
	}
	
	public static ModuleData of(List<VirtualDocument> docs) {
		return new MultiDocModuleData(docs);
	}
}
