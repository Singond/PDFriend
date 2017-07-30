package com.github.singond.pdfriend.io;

import java.io.IOException;
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
	public void acceptBytes(byte[] bytes) throws OutputException {
		try {
			logger.info("write-file", file);
			Files.write(file, bytes);
			logger.info("write-file-done", file);
		} catch (IOException e) {
			throw new OutputException("Error when writing to output file "
					+ file.toAbsolutePath(), this, e);
		}
	}
}
