package cz.slanyj.pdfriend.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Converts the virtual document into a document of a specific file format.
 * The file format of the output document depends on the Renderer subclass.
 * @author Singon
 *
 */
public abstract class Renderer {

	/**
	 * Renders the given virtual document into a real document.
	 */
	public abstract byte[] render(Document document);
	
	/**
	 * Renders the virtual document into a real document and saves
	 * it to the given file.
	 * @throws IOException if an I/O error occurs writing to or creating the file.
	 */
	public void renderAndSave(Document document, File output) throws IOException {
		byte[] data = render(document);
		Files.write(output.toPath(), data);
	}
}
