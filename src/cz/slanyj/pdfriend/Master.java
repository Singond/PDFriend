package cz.slanyj.pdfriend;

import java.awt.print.PrinterException;
import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class Master {
	
	
	
	public static void main(String[] args) {
		GUI.open();
		Document doc = new Document();
		Sheet.Real s = new Sheet.Real(doc);
		s.setDimensions(612, 792);
//		s.setBleeds(20);
//		s.setOverbleeds(50);
		doc.add(s);
//		Page.Real page = new Page.Real(s);
		Page.Real page;
		try {
			PDDocument source = PDDocument.load("Fotky.pdf");
//			page = new Page.Real((PDPage)(source.getDocumentCatalog().getAllPages().get(0)));
			page = new Page.Real();
			source.close();
//			page.setBleeds(10);
//			page.setRotation(Page.ROTATED_LEFT);
//			page.setPosition(350, 500);
			page.setScale(0.9);
			s.add(page);
//			doc.write("new.pdf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/* catch (COSVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		javax.swing.SwingUtilities.invokeLater(()->GUI.openDoc(doc));
		System.out.println("Finished");
	}
}
