package cz.slanyj.pdfriend.imposition;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.SourceDocument;
import cz.slanyj.pdfriend.book.Volume;

/**
 * A common parent for all types of imposable documents.
 * Extending classes need to provide the volume() method, which
 * returns the fully imposed Volume without content.
 * @author Singon
 *
 */
@Deprecated
public abstract class Document {
	
	abstract Volume volume();

	/**
	 * Returns a PDDocument created by filling the volume with content
	 * loaded from the given PDF file.
	 * @param sourceFile A PDF file with source pages.
	 * @return A new PDDocument.
	 * @throws IOException
	 */
	public PDDocument impose(File sourceFile) throws IOException {
		PDDocument sourceDocument = PDDocument.load(sourceFile);
		SourceDocument source = new SourceDocument(sourceDocument);
		volume().setSource(source.getAllPages());
		return volume().renderDocument();
	}
	
	/**
	 * Populates this Document with content and saves it to a file.
	 * @param sourceFile A PDF file with source pages.
	 * @param target A file to save to.
	 * @return A new PDDocument.
	 * @throws IOException
	 */
	public void imposeTo(File sourceFile, File target) throws IOException {
		PDDocument doc = impose(sourceFile);
		doc.save(target);
		doc.close();
	}
}
