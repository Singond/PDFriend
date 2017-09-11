package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.singond.pdfriend.book.GridPage;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.book.SinglePage;
import com.github.singond.pdfriend.book.SourceProvider;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;

@SuppressWarnings("deprecation")
public class PrintPage {

	public static void main(String[] args) throws ParsingException, RenderingException, IOException {
		File srcFile = new File("test/lorem-letter.pdf");
		File output = new File("test/printed-page.pdf");
		VirtualDocument source = new PDFParser().parseDocument(Files.readAllBytes(srcFile.toPath()));

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
