package com.github.singond.pdfriend.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

	/**
	 * @throws FileNotFoundException if the file does not exist or if its
	 *         status cannot be checked
	 * @throws IOException if an error occurs when reading the file
	 */
	@Override
	public InputStream getInputStream() throws FileNotFoundException, IOException {
		if (Files.exists(file)) {
			// The file is verified to exist
			logger.info("readFile", file);
			return Files.newInputStream(file);
		} else if (Files.notExists(file)) {
			// The file is verified not to exist
			logger.error("File not found: {}", file);
			throw new FileNotFoundException("Cannot find file " + file);
		} else {
			// The file's status is unknown
			logger.error("Cannot check file status: {}", file);
			throw new IOException("Cannot check the status of file " + file);
		}
	}

	@Override
	public String getName() {
		return FileNameHandler.normalizeFileName(file.getFileName().toString());
	}
}
