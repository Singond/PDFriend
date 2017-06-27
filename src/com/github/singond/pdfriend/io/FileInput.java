package com.github.singond.pdfriend.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An input to a pipe from a file.
 * @author Singon
 */
public class FileInput implements Input {
	private final Path file;
	
	public FileInput(Path file) {
		this.file = file;
	}
	
	public FileInput(String file) {
		this(Paths.get(file));
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
	public <T, P, E extends Throwable> T invite(InputVisitor visitor, P param) throws E {
		return visitor.visit(this, param);
	}
}
