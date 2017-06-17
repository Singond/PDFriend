package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.process.PDFRenderer;

public class EmptyPages {
	public static void main(String[] args) throws RenderingException, IOException {
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		doc.addPage(new VirtualPage(595, 842));
		doc.addPage(new VirtualPage(595, 842));
		doc.addPage(new VirtualPage(595, 842));
		
		File output = new File("test/blank.pdf");
		new PDFRenderer().renderAndSave(doc.build(), output);
	}
}
