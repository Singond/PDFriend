package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ImportException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFImporter;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.imposition.Booklet;
import com.github.singond.pdfriend.imposition.Booklet.Binding;

public class PrintBooklet {
	
	private static ExtendedLogger logger = Log.logger(PrintBooklet.class);

	public static void main(String[] args) {
		File sourceFile = new File("test/lorem-letter.pdf");
		File targetFile = new File("test/printed-booklet.pdf");
		VirtualDocument source;
		try {
			source = new PDFImporter().importDocument(Files.readAllBytes(sourceFile.toPath()));
			Booklet booklet = Booklet.from(source, Binding.BOTTOM, false);
			Volume volume = booklet.volume();
			SourceProvider<Page> sp = new SequentialSourceProvider(source);
			sp.setSourceTo(volume.pages());
			VirtualDocument doc = volume.renderDocument();
			new PDFRenderer().renderAndSave(doc, targetFile);
			logger.info("Finished writing document");
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
