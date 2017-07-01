package com.github.singond.pdfriend.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Data input from a file.
 * @author Singon
 */
public class FileInput implements InputElement {
	private final Path file;
	
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
			return Files.readAllBytes(file);
		} catch (IOException e) {
			throw new InputException("Error when reading input file "
					+ file.toAbsolutePath(), this, e);
		}
	}

	@Override
	public <T, P> T invite(InputVisitor<T, P> visitor, P param) throws InputException {
		return visitor.visit(this, param);
	}
}
