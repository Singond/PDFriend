package cz.slanyj.pdfriend.format.output;

import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.document.AContentVisitor;
import cz.slanyj.pdfriend.document.Content;
import cz.slanyj.pdfriend.document.DocPage;
import cz.slanyj.pdfriend.document.Document;
import cz.slanyj.pdfriend.document.Renderer;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.format.content.PDFPage;

public class PDFRenderer extends Renderer<PDDocument> {

	@Override
	public PDDocument render(Document document) throws RenderingException {
		PDDocument targetDoc = new PDDocument();
		LayerUtility lutil = new LayerUtility(targetDoc);
		DocumentController docCtrl = new DocumentController(targetDoc, lutil);
		
		for (DocPage pg : document.getPages()) {
			renderPage(pg, docCtrl);
		}
		return targetDoc;
	}
	
	@Override
	public byte[] renderRaw(Document document) throws RenderingException {
		PDDocument doc = render(document);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
    		doc.save(bytes);
    		doc.close();
		} catch (IOException e) {
			throw new RenderingException("Error when converting the PDDocument to a byte array", e);
		}
		return bytes.toByteArray();
	}


	private PDPage renderPage(DocPage page, DocumentController docCtrl) throws RenderingException {
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
			throw new RenderingException("Error when writing the output stream for page "+targetPage, e);
		}
		return targetPage;
	}
	
	/**
	 * A Content Visitor providing the actual imposing logic.
	 * @author Singon
	 *
	 */
	private static class ContentRenderer extends AContentVisitor<Void, PageController, RenderingException> {
		
		@Override
		public Void visit(PDFPage source, PageController controller) throws RenderingException {
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
				throw new RenderingException("Error when writing the contents of page "+source, e);
			}
			
			return null;
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
