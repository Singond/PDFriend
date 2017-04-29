package cz.slanyj.pdfriend.format.output;

import java.awt.geom.AffineTransform;

import java.io.IOException;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import cz.slanyj.pdfriend.Bundle;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.book.Leaf;
import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.ContentVisitor;
import cz.slanyj.pdfriend.document.DocPage;
import cz.slanyj.pdfriend.document.Document;
import cz.slanyj.pdfriend.document.NoException;
import cz.slanyj.pdfriend.document.Renderer;
import cz.slanyj.pdfriend.format.content.PDFPage;

public class PDFRenderer extends Renderer {

	@Override
	public byte[] render(Document document) {
		PDDocument targetDoc = new PDDocument();
		LayerUtility lutil = new LayerUtility(targetDoc);
		DocumentController docCtrl = new DocumentController(targetDoc, lutil);
		
		for (DocPage pg : document.getPages()) {
			renderPage(pg, docCtrl);
		}
		return null;
	}

	
	private PDPage renderPage(DocPage page, DocumentController docCtrl) {
		//Log.verbose(Bundle.console, "sheet_renderingFront", this);
		PDPage targetPage = new PDPage();
		targetPage.setMediaBox(new PDRectangle((float) page.getWidth(), (float) page.getHeight()));
		ContentRenderer contentRndr = new ContentRenderer();
		
		try {
			PDPageContentStream content = new PDPageContentStream(docCtrl.doc, targetPage);
			PageController pageCtrl = new PageController(docCtrl, targetPage, content);
			
			for (Content c : page.getContent()) {
				c.invite(contentRndr, pageCtrl);
			}
			content.close();
		} catch (IOException e) {
			// TODO Rethrow
		}
		return targetPage;
	}
	
	private static class ContentRenderer implements ContentVisitor<Void, PageController, NoException> {
		
		@Override
		public Void visit(PDFPage source, PageController controller) {
			LayerUtility layerUtility = controller.doc.layerUtility;
			PDPageContentStream content = controller.cs;
			AffineTransform trMatrix = source.getPosition();
			
			try {
				PDFormXObject form = layerUtility.importPageAsForm(source.getDoc(), source.getPage());
				content.saveGraphicsState();
				content.transform(new Matrix(trMatrix));
				content.drawForm(form);
				content.restoreGraphicsState();
			} catch (IOException e) {
				Log.error("An I/O Exception occured when imposing PDFPage %s onto target page %s.",
				          source, controller.page);
				e.printStackTrace();
				// TODO Rethrow as RenderingException
			}
			
			return null;
		}
		
		@Override
		public Void visit(Content c, PageController v) {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Container aggregating objects necessary for document rendering.
	 * Makes no guarantee regarding the objects' compatibility.
	 */
	private static class DocumentController {
		
		/** The document being rendered */
		private final PDDocument doc;
		
		/** Layer utility of the document */
		private final LayerUtility layerUtility;
		
		private DocumentController(PDDocument document,
		                           LayerUtility layerUtility) {
			this.doc = document;
			this.layerUtility = layerUtility;
		}
	}
	
	/**
	 * Container aggregating objects necessary for page rendering.
	 * Makes no guarantee regarding the objects' compatibility.
	 */
	private static class PageController {
		
		/** The controller of the document being rendered */
		private final DocumentController doc;
		
		/** The page being rendered */
		private final PDPage page;
		
		/** Content stream of the rendered page */
		private final PDPageContentStream cs;
		
		private PageController(DocumentController document,
		                       PDPage page,
		                       PDPageContentStream stream) {
			this.doc = document;
			this.page = page;
			this.cs = stream;
		}
	}
}
