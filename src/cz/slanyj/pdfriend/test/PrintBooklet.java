package cz.slanyj.pdfriend.test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.SourceDocument;
import cz.slanyj.pdfriend.imposition.Booklet;
import cz.slanyj.pdfriend.imposition.Booklet.Binding;

public class PrintBooklet {

	public static void main(String[] args) throws IOException {
		File sourceFile = new File("test/lorem-letter.pdf");
		File targetFile = new File("test/printed-booklet.pdf");
		PDDocument sourceDoc = PDDocument.load(new File("test/lorem-letter.pdf"));
		SourceDocument source = new SourceDocument(sourceDoc);
		Booklet booklet = Booklet.from(source);
		booklet.imposeTo(sourceFile, targetFile);
	}
}
