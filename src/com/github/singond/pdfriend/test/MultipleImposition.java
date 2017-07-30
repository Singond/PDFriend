package com.github.singond.pdfriend.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.Order;
import com.github.singond.pdfriend.book.model.FlipDirection;
import com.github.singond.pdfriend.book.model.Leaf;
import com.github.singond.pdfriend.book.model.Sheet;
import com.github.singond.pdfriend.book.model.Signature;
import com.github.singond.pdfriend.book.model.SinglePage;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.book.model.Leaf.Orientation;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;

public class MultipleImposition {
	
	private static ExtendedLogger logger = Log.logger(MultipleImposition.class);

	public static void main(String[] args) throws ParsingException, RenderingException, IOException {
		File srcFile = new File("test/lorem-letter.pdf");
		VirtualDocument source = new PDFParser().parseDocument(Files.readAllBytes(srcFile.toPath()));
		VirtualPage page = source.getPage(1);
		
		/* First imposition */
		
		Leaf leaf = new Leaf(612, 792);
		leaf.setAsFrontPosition(new Leaf.Position(153, 396, 0));
		leaf.setOrientation(Orientation.RECTO_UP);
		leaf.setFlipDirection(FlipDirection.AROUND_Y);
		leaf.numberPagesFrom(1);
		((SinglePage) leaf.getRecto()).setSource(page);
		
		Sheet sheet = new Sheet(612, 792);
		sheet.addLeaf(leaf);
		
		Signature signature = new Signature();
		signature.add(sheet);
		signature.setLeafOrder(new Order<Leaf>());
		
		Volume volume = new Volume();
		volume.add(signature);
		VirtualDocument doc = volume.renderDocument();
		VirtualPage page2 = doc.getPage(1);
		
		/* Second imposition */
		
		Leaf leaf2 = new Leaf(612, 792);
		leaf2.setAsFrontPosition(new Leaf.Position(306, 396, 0));
		leaf2.setOrientation(Orientation.RECTO_UP);
		leaf2.setFlipDirection(FlipDirection.AROUND_Y);
		leaf2.numberPagesFrom(1);
		((SinglePage) leaf2.getRecto()).setSource(page2);
		
		Sheet sheet2 = new Sheet(1224, 792);
		sheet2.addLeaf(leaf2);
		
		Signature signature2 = new Signature();
		signature2.add(sheet2);
		signature2.setLeafOrder(new Order<Leaf>());
		
		Volume volume2 = new Volume();
		volume2.add(signature2);
		VirtualDocument doc2 = volume2.renderDocument();
		
		/* Output */
		new PDFRenderer().renderAndSave(doc2, new File("test/imposed-multiple.pdf"));
		logger.info("Finished writing document");
	}
}
