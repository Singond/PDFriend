package com.github.singond.pdfriend.format.process;

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

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.Util;
import com.github.singond.pdfriend.document.AContentVisitor;
import com.github.singond.pdfriend.document.Content;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.Renderer;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.content.PDFPage;

public class PDFRenderer extends Renderer<PDDocument> {
	
	private static ExtendedLogger logger = Log.logger(PDFRenderer.class);

	@Override
	public PDDocument render(VirtualDocument document) throws RenderingException {
		logger.verbose("render_doc", document);
		
		PDDocument targetDoc = new PDDocument();
		LayerUtility lutil = new LayerUtility(targetDoc);
		DocumentController docCtrl = new DocumentController(targetDoc, lutil);
		
		for (VirtualPage pg : document.getPages()) {
			targetDoc.addPage(renderPage(pg, docCtrl));
		}
		return targetDoc;
	}
	
	@Override
	public byte[] renderRaw(VirtualDocument document) throws RenderingException {
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


	private PDPage renderPage(VirtualPage page, DocumentController docCtrl) throws RenderingException {
		logger.verbose("render_page", page);
		PDPage targetPage = new PDPage();
		targetPage.setMediaBox(new PDRectangle((float) page.getWidth(), (float) page.getHeight()));
		ContentRenderer contentRndr = new ContentRenderer();
		
		if (page.getContents().isEmpty()) {
			logger.debug("render_pageBlank", page);
			return targetPage;
		}
		try {
			PDPageContentStream content = new PDPageContentStream(docCtrl.doc, targetPage);
			PageController pageCtrl = new PageController(docCtrl, targetPage, content);
			
			if (logger.isDebugEnabled()) {
				logger.debug("render_content", page.getContents().get().size(), page);
			}
			for (Content c : page.getContents().get()) {
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
			
			logger.debug("render_pdf_matrix", source, Util.toString(trMatrix));
			PDPage page = source.getPage();
			PDRectangle box = PDFSettings.getBox(page);
			/*
			 * HACK: Apparently, imposing a page with 90 or 270 degree rotation
			 * stretches the page to fit the non-rotated rectangle, effectively
			 * swapping height for width and vice versa. The following is
			 * a hack to overcome this limitation of PDFBox.
			 */
			int rotation = page.getRotation();
			if (rotation % 180 == 90) {
				logger.debug("render_pdf_workaround", source, rotation);
				float w = box.getWidth();
				float h = box.getHeight();
				trMatrix.scale(h/w, w/h);
			} // End HACK
			// Move to the box position
			trMatrix.translate(box.getLowerLeftX(), box.getLowerLeftY());
			
			try {
				PDFormXObject form = layerUtility.importPageAsForm(source.getDoc(), page);
				content.saveGraphicsState();
				content.transform(new Matrix(trMatrix));
				content.drawForm(form);
				content.restoreGraphicsState();
			} catch (IOException e) {
				logger.error("render_pdf_ioException", source, controller.page);
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
