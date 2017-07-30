package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.GridPage;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.book.model.SinglePage;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ImportException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.format.process.PDFRenderer;

public class PrintPage {

	public static void main(String[] args) throws ImportException, RenderingException, IOException {
		File srcFile = new File("test/lorem-letter.pdf");
		File output = new File("test/printed-page.pdf");
		VirtualDocument source = new PDFImporter().importDocument(Files.readAllBytes(srcFile.toPath()));

		SinglePage page1 = new SinglePage(612, 792);
		GridPage page2 = new GridPage(2, 2, 306, 396);
		page2.fitPages();
		
		SourceProvider<Page> sp = new SequentialSourceProvider(source);
		sp.setSourceTo(page1);
		sp.setSourceTo(page2);
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		doc.addPage(page1.render());
		doc.addPage(page2.render());
		new PDFRenderer().renderAndSave(doc.build(), output);
	}
}
