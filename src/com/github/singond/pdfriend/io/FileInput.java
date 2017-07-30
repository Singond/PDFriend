package com.github.singond.pdfriend.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;

/**
 * Data input from a file.
 * @author Singon
 */
class FileInput implements InputElement {
	private final Path file;
	private static ExtendedLogger logger = Log.logger(FileInput.class);
	
	public FileInput(Path file) {
		this.file = file;
	}
	
	public FileInput(String file) {
		this(Paths.get(file));
	}
	
	/** Returns the input file */
	public Path getFile() {
		return file;
	}

	@Override
	public byte[] getBytes() throws InputException {
		try {
			logger.info("readFile", file);
			return Files.readAllBytes(file);
		} catch (IOException e) {
			throw new InputException("Error when reading input file "
					+ file.toAbsolutePath(), this, e);
		}
	}
}
