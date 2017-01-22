package cz.slanyj.pdfriend.test;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

public class DocMismatch {

	public static void main(String[] args) {
		try {
			PDDocument aDoc = new PDDocument();
			PDDocument bDoc = new PDDocument();
			PDPage aPage = new PDPage();

			// get the page crop box. Will be used later to place the
			// imported page.
			PDRectangle cropBox = aPage.getCropBox();

			aDoc.addPage(aPage);

			PDDocument toBeImported = PDDocument.load(new File("Fotky.pdf"));
			LayerUtility layerUtility = new LayerUtility(bDoc);
			PDFormXObject mountable = layerUtility.importPageAsForm(toBeImported, 0);

			PDPageContentStream contentStream = new PDPageContentStream(
				aDoc, aPage, PDPageContentStream.AppendMode.APPEND, true);

			contentStream.saveGraphicsState();
			// Rotate pi/2 CCW, scale by 0.5, and move width/2 to right  
			AffineTransform transform = new AffineTransform(0, 0.5, -0.5, 0, cropBox.getWidth(), 0);
			contentStream.transform(new Matrix(transform));
			contentStream.drawForm(mountable);
			
			contentStream.restoreGraphicsState();
			contentStream.close();

			// close the imported document
			toBeImported.close();
			
			aDoc.save("mismatched.pdf");
			aDoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
