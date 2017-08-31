package com.github.singond.pdfriend.imposition;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.imposition.Preprocessor;
import com.github.singond.pdfriend.modules.Impose;

public class PreprocessDocument {
	
	private static final LengthUnit PT = LengthUnits.POINT_POSTSCRIPT;
	private static final LengthUnit MM = LengthUnits.MILLIMETRE;

	public static void main(String[] args) {
		File input = new File("test/lorem-letter.pdf");
		
		try {
			/* Input Document */
			VirtualDocument inDoc = new PDFParser().parseDocument(Files.readAllBytes(input.toPath()));
			
			Preprocessor.Settings settings = new Preprocessor.Settings();
			settings.setScale(2);
//			settings.setCellDimensions(new Dimensions(200, 100, MM));
			Preprocessor pp = new Preprocessor(Arrays.asList(inDoc), settings);
			
			/* Output */
			VirtualDocument.Builder outDoc = new VirtualDocument.Builder();
			for (VirtualPage pg : inDoc.getPages()) {
				outDoc.addPage(pp.process(pg));
			}
			
			PDDocument out = new PDFRenderer().render(outDoc.build());
			out.save(new File("test/preprocessed-doc.pdf"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
		
	}

}
