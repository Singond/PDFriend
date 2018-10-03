package com.github.singond.pdfriend.pipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.io.Input;
import com.github.singond.pdfriend.io.Output;
import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleException;

public class Pipe {

	private static ExtendedLogger logger = Log.logger(Pipe.class);

	private final List<Operation> operations;
	private PipeInput inputProvider;
	private PipeOutput outputConsumer;
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
	
	public void setInput(Input input) {
		if (executed) {
			throw new IllegalStateException("This pipe has already been executed");
		}
		inputProvider = new SimpleInput(input);
	}
	
	public void setOutput(Output output) {
		if (executed) {
			throw new IllegalStateException("This pipe has already been executed");
		}
		outputConsumer = new SimpleOutput(output);
	}
	
	@Deprecated
	public List<VirtualDocument> getOutput() {
		if (!executed) {
			throw new IllegalStateException("This pipe has not been executed yet");
		}
		return output;
	}
	
	public void execute() throws ModuleException, PipeException {
		executed = true;
		PipeData data = inputProvider.getPipeData();
		for (Operation op : operations) {
			data = op.process(data);
		}
		outputConsumer.consumePipeData(data);
		try {
			inputProvider.close();
		} catch (IOException e) {
			logger.error("Pipe input could not be closed", e);
			throw new PipeException("Pipe input could not be closed", e);
		}
	}
}
