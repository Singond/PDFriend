package cz.slanyj.pdfriend.book;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.SourcePage;

/**
 * The whole text block of a document, made by arranging several Signatures
 * in a sequence.
 * @author Singon
 *
 */
public class Volume {

	/**
	 * Signatures sorted in their proper order in the finished volume.
	 */
	private List<Signature> signatures;
	
	public Volume() {
		signatures = SetUniqueList.setUniqueList(new LinkedList<Signature>());
	}
	
	/**
	 * Inserts the given Signature into this Volume.
	 * @param signature
	 */
	public boolean add(Signature signature) {
		return signatures.add(signature);
	}
	
	/**
	 * Sets the source document to provide contents for all Leaves in
	 * this Volume.
	 */
	public void setSource(List<SourcePage> pagesList) {
		signatures.stream()
			.flatMap(sig -> sig.getSheets().stream())
			.flatMap(sh -> sh.getLeaves().stream())
			.forEach(l->l.setContent(pagesList));
	}
	
	/**
	 * Renders this Volume as a new PDDocument.
	 */
	public PDDocument renderDocument() throws IOException {
		PDDocument document = new PDDocument();
		for (Signature s : signatures) {
			s.renderAllSheets(document);
		}
		return document;
	}
	
	/**
	 * Saves the given document as a new file.
	 */
	private void saveDocument(PDDocument doc, File target) throws IOException {
		doc.save(target);
		doc.close();
	}
	
	/**
	 * Renders this Volume and saves it as a new PDF file.
	 * @throws IOException 
	 */
	public void renderAndSaveDocument(File target) throws IOException {
		PDDocument doc = renderDocument();
		saveDocument(doc, target);
	}
}
