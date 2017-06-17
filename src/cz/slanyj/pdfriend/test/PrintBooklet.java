package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.book.control.SequentialSourceProvider;
import cz.slanyj.pdfriend.book.control.SourceProvider;
import cz.slanyj.pdfriend.book.model.Volume;
import cz.slanyj.pdfriend.document.ImportException;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.format.process.PDFImporter;
import cz.slanyj.pdfriend.format.process.PDFRenderer;
import cz.slanyj.pdfriend.imposition.Booklet;
import cz.slanyj.pdfriend.imposition.Booklet.Binding;

public class PrintBooklet {
	
	private static final ExtendedLogger logger = Log.logger(PrintBooklet.class);

	public static void main(String[] args) {
		File sourceFile = new File("test/lorem-letter.pdf");
		File targetFile = new File("test/printed-booklet.pdf");
		VirtualDocument source;
		try {
			source = new PDFImporter(sourceFile).importDocument();
			Booklet booklet = Booklet.from(source, Binding.BOTTOM, false);
			Volume volume = booklet.volume();
			SourceProvider sp = new SequentialSourceProvider(source);
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
