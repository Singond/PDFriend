package cz.slanyj.pdfriend.test;

//import org.apache.pdfbox.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectForm;
import org.apache.pdfbox.util.LayerUtility;

public class Basic {

	public static void main (String[] args) {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage(page);
		
		PDFont font = PDType1Font.HELVETICA_BOLD;
		PDPageContentStream contents;
		
		try {
			PDDocument layer = PDDocument.load(new File("1.pdf"));
//			PDPage out = new PDPage();
//			doc.addPage(out);

//			contents = new PDPageContentStream(doc, page);
			
/*			contents.beginText();
			contents.setFont(font, 12);
			contents.moveTextPositionByAmount(100, 500);
			contents.drawString("Aiya Ambar!");
			contents.endText();
			contents.close();*/
			
			LayerUtility lu = new LayerUtility(doc);
			PDXObjectForm obj = lu.importPageAsForm(layer, 0);
			lu.appendFormAsLayer(page, obj, new AffineTransform(), "layer");
			
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		try {
			doc.save("blank.pdf");
			doc.close();
		} catch (COSVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
