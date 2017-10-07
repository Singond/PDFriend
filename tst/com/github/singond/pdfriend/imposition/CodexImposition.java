package com.github.singond.pdfriend.imposition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;

public class CodexImposition {

	@Test
	public void imposeCodexTest() {
		Codex codex = new Codex.Builder()
				.foldHorizontallyDown()
				.foldVerticallyDown()
				.setSheetsInSignature(2)
				.build();
		
		File input = new File("test/lorem-letter-bg.pdf");
		File output = new File("test/imposed-codex.pdf");
		
		try {
			VirtualDocument inDoc = new PDFParser().parseDocument(Files.readAllBytes(input.toPath()));
			VirtualDocument outDoc = codex.imposeAndRender(inDoc);
			new PDFRenderer().renderAndSave(outDoc, output);
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RenderingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
