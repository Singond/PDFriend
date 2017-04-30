package cz.slanyj.pdfriend.test;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;
import cz.slanyj.pdfriend.format.content.PDFPage;
import cz.slanyj.pdfriend.format.output.PDFRenderer;

public class RenderPDF {

	public static void main(String[] args) {

		try {
			/* Source */
			PDDocument source = PDDocument.load(new File("test/lorem-letter.pdf"));
			
			/* Virtual Document */
			VirtualDocument.Builder doc = new VirtualDocument.Builder();
			VirtualPage.Builder pg1 = new VirtualPage.Builder();
			pg1.setWidth(612);
			pg1.setHeight(792);
			Content src1 = new PDFPage(source, source.getPage(0), AffineTransform.getRotateInstance(0.06));
			pg1.addContent(src1.transform(AffineTransform.getTranslateInstance(15, -30)));
			doc.addPage(pg1.build());
			VirtualPage.Builder pg2 = new VirtualPage.Builder();
			pg2.setWidth(612);
			pg2.setHeight(792);
			pg2.addContent(new PDFPage(source, source.getPage(1), AffineTransform.getTranslateInstance(-30, 30)));
			doc.addPage(pg2.build());
			
			/* Output */
			VirtualDocument document = doc.build();
			PDDocument out = new PDFRenderer().render(document);
			out.save(new File("test/rendered-pdf.pdf"));
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RenderingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
