package com.github.singond.pdfriend.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;

/**
 * Data output to a file.
 * @author Singon
 */
class FileOutput implements OutputElement {
	private final Path file;
	private static ExtendedLogger logger = Log.logger(FileOutput.class);

	public FileOutput(Path file) {
		this.file = file;
	}

	public FileOutput(String file) {
		this(Paths.get(file));
	}

	@Override
	public OutputStream getOutputStream() throws OutputException {
		OutputStream out;
		try {
			logger.debug("outputFileStream", file);
			out = Files.newOutputStream(file);
		} catch (IOException e) {
			throw new OutputException("Error when obtaining output file "
					+ file.toAbsolutePath(), this, e);
		}
		return out;
	}

	@Override
	public String toString() {
		return "Output to " + file;
	}
}
