package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.Book;
import com.github.singond.pdfriend.book.LayerSourceProvider;
import com.github.singond.pdfriend.book.LayeredPage;
import com.github.singond.pdfriend.book.LoosePages;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.imposition.Preprocessor.Settings;

/**
 * A document consisting of layered pages.
 *
 * @author Singon
 */
public class Overlay extends AbstractImposable implements Imposable {

	/** The internal name of this imposable document type */
	private static final String NAME = "overlay";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(Overlay.class);

	private final Preprocessor.Settings preprocess;
	private final CommonSettings common;
	private final LengthUnit unit = Imposition.LENGTH_UNIT;
	
	/** The pages in the document */
	@Deprecated
	private final List<LayeredPage> pages = Collections.emptyList();
	
	private Overlay(Preprocessor.Settings preprocess, CommonSettings common) {
		if (preprocess == null)
			throw new IllegalArgumentException("Preprocessor settings must not be null");
		if (common == null)
			throw new IllegalArgumentException("Common settings must not be null");
		
		this.preprocess = preprocess.copy();
		this.common = common;
	}
	
	@Deprecated // Build document when invoking imposition
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
//		this.pages = pageList;
		// FIXME: Dummy values to silence compiler errors
		this.preprocess = null;
		this.common = null;
	}
	
	@Deprecated
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
	
	private List<LayeredPage> imposeAsPages(List<VirtualDocument> docs) {
		double[] dims = VirtualDocument.maxPageDimensions(docs);
		int pages = VirtualDocument.maxLength(docs);
		int layers = docs.size();

		logger.verbose("overlay_constructing", pages, layers);
		LayeredPage template = new LayeredPage(dims[0], dims[1], layers);
		List<LayeredPage> pageList = new ArrayList<>(pages);
		int pageNumber = 0;
		while(pageList.size() < pages) {
			LayeredPage page = new LayeredPage(template);
			page.setNumber(++pageNumber);
			pageList.add(page);
		}
		
		logger.verbose("overlay_filling");
		LayerSourceProvider lsp = new LayerSourceProvider(docs);
		lsp.setSourceTo(pageList);
//		logger.verbose("overlay_setupFinished");
		return pageList;
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

	/**
	 * Returns true, because Overlay, by its nature, requires at least two
	 * documents in order for it to have any effect.
	 * @return always the value of {@code true}
	 */
	@Override
	public boolean prefersMultipleInput() {
		return true;
	}

	@Override
	public Book impose(VirtualDocument source) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException
				("Not implemented yet: Cannot create a Book of one layer only");
	}

	@Override
	public Book impose(List<VirtualDocument> sources) {
		return new LoosePages(imposeAsPages(sources));
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
	public VirtualDocument imposeAndRender(VirtualDocument source) {
		return source;
	}
	
	public static final class Builder implements ImposableBuilder<Overlay> {
		private Preprocessor.Settings preprocess = Preprocessor.Settings.auto();
		private CommonSettings common = CommonSettings.auto();
		
		@Override
		public void acceptPreprocessSettings(Settings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Preprocess settings cannot be null");
			this.preprocess = settings;
		}

		@Override
		public void acceptCommonSettings(CommonSettings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Settings cannot be null");
			this.common = settings;
		}

		@Override
		public Overlay build() {
			return new Overlay(preprocess, common);
		}
		
	}
}
