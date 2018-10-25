package com.github.singond.pdfriend.io;

import java.nio.file.Path;
import java.util.List;

public class OutputFactory {

	/** Non-instantiable class */
	private OutputFactory() {};
	
	public static Output of(List<Path> files) {
		if (files.size() == 0) {
			throw new IllegalArgumentException("The list of output files is empty");
		} else if (files.size() == 1) {
			return of(files.get(0));
		} else {
//    		List<InputElement> fileList = files.stream()
//    				.map(FileInput::new)
//    				.collect(Collectors.toList());
//    		return new MultiInput(fileList);
			throw new UnsupportedOperationException("Multiple output is not suported yet");
		}
	}
	
	public static Output of(Path file) {
		return new SingleOutput(new FileOutput(file));
	}
}
