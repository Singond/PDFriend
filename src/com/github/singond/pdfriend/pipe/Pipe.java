package com.github.singond.pdfriend.pipe;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleDataFactory;
import com.github.singond.pdfriend.modules.ModuleException;

public class Pipe {

	private final List<Operation> operations;
	private PipeData input;
	private List<VirtualDocument> output;
	private boolean executed = false;
	
	public Pipe() {
		this.operations = new ArrayList<>();
	}
	
	public void addOperation(Module module) {
		if (executed) {
			throw new IllegalStateException("This pipe has already been executed");
		}
		operations.add(new Operation(module));
	}
	
	public void setInput(VirtualDocument doc) {
		if (executed) {
			throw new IllegalStateException("This pipe has already been executed");
		}
		input = new PipeData(ModuleDataFactory.of(doc));
	}
	
	public void setInput(List<VirtualDocument> docs) {
		if (executed) {
			throw new IllegalStateException("This pipe has already been executed");
		}
		input = new PipeData(ModuleDataFactory.of(docs));
	}
	
	public List<VirtualDocument> getOutput() {
		if (!executed) {
			throw new IllegalStateException("This pipe has not been executed yet");
		}
		return output;
	}
	
	public void execute() throws ModuleException {
		executed = true;
		PipeData data = input;
		for (Operation op : operations) {
			data = op.process(data);
		}
		output = data.getModuleData().asMultipleDocuments();
	}
}
