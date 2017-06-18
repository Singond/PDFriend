package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;

import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.book.model.SinglePage;
import com.github.singond.pdfriend.document.ImportException;
import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.format.process.PDFRenderer;

public class PrintPage {

	public static void main(String[] args) throws ImportException, RenderingException, IOException {
		Page page1 = new SinglePage(612, 792);
		File srcFile = new File("test/lorem-letter.pdf");
		File output = new File("test/printed-page.pdf");
		VirtualDocument source = new PDFImporter(srcFile).importDocument();
		new SequentialSourceProvider(source).setSourceTo(page1);
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		doc.addPage(page1.render());
		new PDFRenderer().renderAndSave(doc.build(), output);
	}
}
