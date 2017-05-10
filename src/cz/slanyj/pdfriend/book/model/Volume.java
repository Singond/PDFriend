package cz.slanyj.pdfriend.book.model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.Bundle;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.process.PDFRenderer;

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
	 * Uses a list of VirtualPages to provide contents for all Leaves in
	 * this Volume.
	 */
//	public void setSource(List<VirtualPage> pagesList) {
//		signatures.stream()
//			.flatMap(sig -> sig.getSheets().stream())
//			.flatMap(sh -> sh.getLeaves().stream())
//			.forEach(l -> {
//				l.getFrontPage().setSourceFrom(pagesList);
//				l.getBackPage().setSourceFrom(pagesList);
//			});
//	}
	
	/**
	 * Uses a VirtualDocument to provide contents for all Leaves in
	 * this Volume.
	 */
//	public void setSource(VirtualDocument document) {
//		signatures.stream()
//			.flatMap(sig -> sig.getSheets().stream())
//			.flatMap(sh -> sh.getLeaves().stream())
//			.forEach(l -> {
//				l.getFrontPage().setSourceFrom(document);
//				l.getBackPage().setSourceFrom(document);
//			});
//	}
	
	/**
	 * Renders this Volume as a new PDDocument.
	 */
	public VirtualDocument renderDocument() {
		Log.info(Bundle.console, "volume_rendering", this);
		VirtualDocument.Builder document = new VirtualDocument.Builder();
		for (Signature s : signatures) {
			s.renderAllSheets(document);
		}
		return document.build();
	}
	
	/**
	 * Saves the given document as a new file.
	 * @deprecated Output to VirtualDocument and render that.
	 */
	@Deprecated
	private void saveDocument(PDDocument doc, File target) throws IOException {
		Log.info(Bundle.console, "volume_saving", this, target.getAbsoluteFile());
		doc.save(target);
		doc.close();
	}
	
	/**
	 * Renders this Volume and saves it as a new PDF file.
	 * @throws IOException
	 * @throws RenderingException
	 * @deprecated Output to VirtualDocument and render that.
	 */
	@Deprecated
	public void renderAndSaveDocument(File target) throws RenderingException, IOException {
		VirtualDocument thisDoc = renderDocument();
		PDDocument doc = new PDFRenderer().render(thisDoc);
		saveDocument(doc, target);
	}
	
	@Override
	public String toString() {
		return "Volume@" + hashCode();
	}
}
