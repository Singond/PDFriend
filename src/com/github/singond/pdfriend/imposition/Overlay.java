package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.LayerSourceProvider;
import com.github.singond.pdfriend.book.LayeredPage;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.imposition.Preprocessor.Settings;

/**
 * A document consisting of layered pages.
 *
 * @author Singon
 */
public class Overlay implements Imposable {

	/** The internal name of this imposable document type */
	private static final String NAME = "overlay";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(Overlay.class);

	/** The pages in the document */
	private final List<LayeredPage> pages;
	
	
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
	
//	@Override
	@Deprecated
	public VirtualDocument getDocument() {
		VirtualDocument.Builder doc = new VirtualDocument.Builder();
		for (Page page : pages) {
			doc.addPage(page.render());
		}
		return doc.build();
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void acceptPreprocessSettings(Settings settings) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public void acceptCommonSettings(CommonSettings settings) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Returns true, because Overlay, by its nature, requires at least two
	 * documents in order for it to have any effect.
	 * @return always the value of {@code true}
	 */
	@Override
	public boolean prefersMultipleInput() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This is the trivial case with only one layer.
	 * This imposition task has has no effect and returns the unchanged
	 * input document.
	 * @return the unchanged document given as {@code source}
	 */
	@Override
	public VirtualDocument impose(VirtualDocument source) {
		return source;
	}

	@Override
	public VirtualDocument impose(List<VirtualDocument> sources) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
