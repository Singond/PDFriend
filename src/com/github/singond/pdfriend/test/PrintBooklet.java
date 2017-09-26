package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.book.SourceProvider;
import com.github.singond.pdfriend.book.Volume;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.imposition.Booklet;
import com.github.singond.pdfriend.imposition.Booklet.Binding;
import com.github.singond.pdfriend.imposition.CommonSettings;

public class PrintBooklet {
	
	private static ExtendedLogger logger = Log.logger(PrintBooklet.class);

	public static void main(String[] args) {
		File sourceFile = new File("test/lorem-letter.pdf");
		File targetFile = new File("test/printed-booklet.pdf");
		VirtualDocument source;
		
		try {
			source = new PDFParser().parseDocument(Files.readAllBytes(sourceFile.toPath()));
			Booklet booklet = new Booklet();
			booklet.setBinding(Binding.TOP);
			booklet.setVersoOpposite(true);
			booklet.acceptCommonSettings(CommonSettings.auto());
			VirtualDocument doc = booklet.imposeAsDocument(source);
			new PDFRenderer().renderAndSave(doc, targetFile);
			logger.info("Finished writing document");
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
