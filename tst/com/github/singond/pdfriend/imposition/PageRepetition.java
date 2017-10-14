package com.github.singond.pdfriend.imposition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.process.PDFParser;

public class PageRepetition {

	Path input = Paths.get("test/lorem-letter-bg.pdf");
	
	@Test
	public void repeatPage() throws ParsingException, IOException {
		VirtualDocument inDoc = new PDFParser().parseDocument(Files.readAllBytes(input));
		int repeatPage = 3;
		int repeatDoc = 2;
		
		PageSource pageSource = PageSource.of(inDoc)
		                                  .withPageRepeated(repeatPage)
		                                  .withDocRepeated(repeatDoc)
		                                  .build();
		int pageNumber = 0;
		for (VirtualPage page : pageSource) {
			System.out.println("Page " + ++pageNumber + ": " + page);
		}
		int pagesExpected = inDoc.getLength() * repeatPage * repeatDoc;
		Assert.assertEquals(pagesExpected, pageNumber);
	}
	
}
