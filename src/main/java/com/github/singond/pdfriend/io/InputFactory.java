package com.github.singond.pdfriend.io;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class InputFactory {

	/** Non-instantiable class */
	private InputFactory() {};
	
	public static Input of(List<Path> files) {
		List<InputElement> fileList = files.stream()
				.map(FileInput::new)
				.collect(Collectors.toList());
		return new MultiInput(fileList);
	}
}
