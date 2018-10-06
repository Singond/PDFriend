package com.github.singond.pdfriend.format;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * Converts the virtual document into a document of a specific file format.
 * The file format of the output document depends on the Renderer subclass.
 * @param <T> Type of the output document.
 *
 * @author Singon
 *
 */
public abstract class Renderer<T> {

	private static ExtendedLogger logger = Log.logger(Renderer.class);

	/**
	 * Renders the given virtual document into a real document.
	 *
	 * @param document the virtual document to be rendered
	 * @return the new document object
	 * @throws RenderingException if an error occurs during rendering
	 */
	public abstract T render(VirtualDocument document)
			throws RenderingException;

	/**
	 * Renders the given virtual document into a real document
	 * and returns its binary data.
	 *
	 * @param document the virtual document to be rendered
	 * @return the document as an array of bytes
	 * @throws RenderingException if an error occurs during rendering
	 */
	public abstract byte[] renderBinary(VirtualDocument document)
			throws RenderingException;

	/**
	 * Renders the given virtual document into a real document.
	 *
	 * @param document the document to be rendered
	 * @param out the output stream to write into
	 * @throws RenderingException if an error occurs during rendering
	 */
	public abstract void render(VirtualDocument document, OutputStream out)
			throws RenderingException;

	/**
	 * Renders the virtual document into a real document and saves
	 * it to the given file.
	 *
	 * @param document the document to be rendered
	 * @param output the file to be saved into
	 * @throws RenderingException if an error occurs during rendering
	 * @throws IOException if an I/O error occurs writing to or creating the file.
	 */
	public void renderAndSave(VirtualDocument document, File output)
			throws RenderingException, IOException {
		byte[] data = renderBinary(document);
		Files.write(output.toPath(), data);
		logger.info("writeFile_done", output);
	}
}
