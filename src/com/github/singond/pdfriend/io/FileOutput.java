package com.github.singond.pdfriend.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Data output to a file.
 * @author Singon
 */
public class FileOutput implements Output {
	private final Path file;
	
	public FileOutput(Path file) {
		this.file = file;
	}
	
	public FileOutput(String file) {
		this(Paths.get(file));
	}

	@Override
	public void acceptBytes(byte[] bytes) throws OutputException {
		try {
			Files.write(file, bytes);
		} catch (IOException e) {
			throw new OutputException("Error when writing to output file "
					+ file.toAbsolutePath(), this, e);
		}
	}

	@Override
	public <T, P> T invite(OutputVisitor<T, P> visitor, P param) throws OutputException {
		return visitor.visit(this, param);
	}
}
