package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.LayerSourceProvider;
import com.github.singond.pdfriend.book.model.LayeredPage;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A document consisting of layered pages. 
 *
 * @author Singon
 */
public class Overlay implements Imposable {

	/** The pages in the document */
	private final List<LayeredPage> pages;
	
	private static ExtendedLogger logger = Log.logger(Overlay.class);
	
	public Overlay(double width, double height, int pages, int layers) {
		logger.verbose("overlay_constructing", pages, layers);
		LayeredPage template = new LayeredPage(width, height, layers);
		List<LayeredPage> pageList = new ArrayList<>(pages);
		int pageNumber = 0;
		while(pageList.size() < pages) {
			LayeredPage page = new LayeredPage(template);
			page.setNumber(++pageNumber);
			pageList.add(page);
		}
		this.pages = pageList;
	}
	
	public static Overlay from(List<VirtualDocument> docs) {
		double[] dims = VirtualDocument.maxPageDimensions(docs);
		int pages = VirtualDocument.maxLength(docs);
		int layers = docs.size();
		Overlay result = new Overlay(dims[0], dims[1], pages, layers);
		
		logger.verbose("overlay_filling");
		LayerSourceProvider lsp = new LayerSourceProvider(docs);
		lsp.setSourceTo(result.pages);
		logger.verbose("overlay_setupFinished");
		return result;
	}
	
	@Override
	public VirtualDocument getDocument() {
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		for (Page page : pages) {
			doc.addPage(page.render());
		}
		return doc.build();
	}

}
