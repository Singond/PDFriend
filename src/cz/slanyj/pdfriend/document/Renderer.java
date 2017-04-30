package cz.slanyj.pdfriend.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Converts the virtual document into a document of a specific file format.
 * The file format of the output document depends on the Renderer subclass.
 * @param <T> Type of the output document.
 * 
 * @author Singon
 *
 */
public abstract class Renderer<T> {

	/**
	 * Renders the given virtual document into a real document.
	 * @return The new document object.
	 */
	public abstract T render(VirtualDocument document) throws RenderingException;
	
	/**
	 * Renders the given virtual document into a real document.
	 * @return The document as a byte stream suitable for saving.
	 */
	public abstract byte[] renderRaw(VirtualDocument document) throws RenderingException;
	
	/**
	 * Renders the virtual document into a real document and saves
	 * it to the given file.
	 * @throws IOException if an I/O error occurs writing to or creating the file.
	 */
	public void renderAndSave(VirtualDocument document, File output) throws RenderingException, IOException {
		byte[] data = renderRaw(document);
		Files.write(output.toPath(), data);
	}
}
