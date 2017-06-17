package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;

import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.process.PDFRenderer;

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
