package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.Book;
import com.github.singond.pdfriend.book.LayeredPage;
import com.github.singond.pdfriend.book.LoosePages;
import com.github.singond.pdfriend.book.MultiPage.PageletView;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.GeometryUtils;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;
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
	/**
	 * When a document in a layer has no more pages, repeat this document
	 * (without affecting the total page count).
	 */
	private final boolean repeatInLayer;


	private Overlay(Preprocessor.Settings preprocess, CommonSettings common, boolean repeatInLayer) {
		if (preprocess == null)
			throw new IllegalArgumentException("Preprocessor settings must not be null");
		if (common == null)
			throw new IllegalArgumentException("Common settings must not be null");
		
		this.preprocess = preprocess.copy();
		this.common = common;
		this.repeatInLayer = repeatInLayer;
	}
	
	
	/**
	 * Imposes the given virtual document into a list of grid pages
	 * according to the current settings of this {@code Overlay} object.
	 */
	private List<LayeredPage> imposeAsPages(List<VirtualDocument> docs) {
		if (logger.isDebugEnabled()) {
			logger.debug("imposition_preprocessSettings", preprocess);
			logger.debug("imposition_commonSettings", common);
//			logger.debug("imposition_imposableSettings", NAME, );
		}
		
		// Select a use case and run it
		if (common.getPageSize() == DimensionSettings.AUTO
				&& common.getSheetSize() == DimensionSettings.AUTO) {
			// Case A
			return casePageSize(docs);
		} else if (preprocess.isAutoSize()) {
			// Case C
			return caseContentSize(docs);
		} else if (common.getMargins() == CommonSettings.AUTO_MARGINS) {
			// Case B
			return caseMargins(docs);
		} else {
			// All are set, a conflict
			logger.verbose("overlay_caseConflict");
			throw new IllegalStateException
					("Conflicting settings: page size, margins and content size are all set");
		}
	}
	

	/**
	 * Builds the list of pages, determining page size from remaining parameters.
	 * 
	 * @param docs the documents to be imposed
	 * @return the imposed document as a list of pages
	 */
	private List<LayeredPage> casePageSize(List<VirtualDocument> docs) {
		if (logger.isDebugEnabled()) {
			logger.debug("overlay_casePageSize");
		}
		
		Dimensions pageSize = resolvePageAndSheetSize
				(common.getPageSize(), common.getSheetSize());
		
		Margins margins = resolveAutoMargins(common.getMargins());
		preprocess.setCellMargins(margins);
		Preprocessor preprocessor = new Preprocessor(docs, preprocess);
		Dimensions contentSize = preprocessor.getResolvedCellDimensions();
		pageSize = GeometryUtils.rectanglePlusMargins(contentSize, margins);
		int layerCount = docs.size();
		LayeredPage template = new LayeredPage(pageSize.width().in(unit),
		                                       pageSize.height().in(unit),
		                                       layerCount);
		
		int pageCount = resolvePageCount(common, docs);
		docs = preprocessDocuments(docs, preprocessor, pageCount);
		List<LayeredPage> pages = buildPages(template, pageCount);
		fillPages(docs, pages);
		return pages;
	}
	
	/**
	 * Builds the list of pages, determining the width of margins from
	 * remaining parameters.
	 * 
	 * @param docs the documents to be imposed
	 * @return the imposed document as a list of pages
	 */
	private List<LayeredPage> caseMargins(List<VirtualDocument> docs) {
		if (logger.isDebugEnabled()) {
			logger.debug("overlay_caseMargins");
		}
		
		Dimensions pageSize = resolvePageAndSheetSize
				(common.getPageSize(), common.getSheetSize());
		
		// Helper preprocessor to resolve cell size
		Preprocessor helper = new Preprocessor(docs, preprocess);
		Dimensions contentSize = helper.getResolvedCellDimensions();
		Length oneHorizontalMargin = Length.subtract(
				pageSize.width(), contentSize.width()).times(1d/2);
		Length oneVerticalMargin = Length.subtract(
				pageSize.height(), contentSize.height()).times(1d/2);
		Margins margins = new Margins(oneHorizontalMargin, oneVerticalMargin);
		preprocess.setCellDimensions(DimensionSettings.of(pageSize));
		preprocess.setCellMargins(margins);
		Preprocessor preprocessor = new Preprocessor(docs, preprocess);
		pageSize = preprocessor.getResolvedCellDimensions();
		int layerCount = docs.size();
		LayeredPage template = new LayeredPage(pageSize.width().in(unit),
		                                       pageSize.height().in(unit),
		                                       layerCount);
		
		int pageCount = resolvePageCount(common, docs);
		docs = preprocessDocuments(docs, preprocessor, pageCount);
		List<LayeredPage> pages = buildPages(template, pageCount);
		fillPages(docs, pages);
		return pages;
	}
	
	/**
	 * Builds the list of pages, determining content size from remaining parameters.
	 * 
	 * @param docs the documents to be imposed
	 * @return the imposed document as a list of pages
	 */
	private List<LayeredPage> caseContentSize(List<VirtualDocument> docs) {
		if (logger.isDebugEnabled()) {
			logger.debug("overlay_caseContentSize");
		}
		
		Dimensions pageSize = resolvePageAndSheetSize
				(common.getPageSize(), common.getSheetSize());
		
		Margins margins = resolveAutoMargins(common.getMargins());
		Dimensions contentSize = GeometryUtils.rectangleMinusMargins(pageSize, margins);
		preprocess.setCellDimensions(DimensionSettings.of(contentSize));
		preprocess.setCellMargins(margins);
		Preprocessor preprocessor = new Preprocessor(docs, preprocess);
		pageSize = preprocessor.getResolvedCellDimensions();
		int layerCount = docs.size();
		LayeredPage template = new LayeredPage(pageSize.width().in(unit),
		                                       pageSize.height().in(unit),
		                                       layerCount);
		
		int pageCount = resolvePageCount(common, docs);
		docs = preprocessDocuments(docs, preprocessor, pageCount);
		List<LayeredPage> pages = buildPages(template, pageCount);
		fillPages(docs, pages);
		return pages;
	}
	
	/**
	 * Resolves margins object into a valid value.
	 * 
	 * @param margins margins object to be resolved
	 * @return the argument, if it is a valid value, and the default value
	 *         of (0, 0, 0, 0) otherwise
	 */
	private Margins resolveAutoMargins(Margins margins) {
		// Resolve automatic margins
		if (margins == CommonSettings.AUTO_MARGINS) {
			margins = new Margins(0, 0, 0, 0, LengthUnits.METRE);
			logger.verbose("overlay_marginsResolveAuto", margins);
		}
		return margins;
	}

	/**
	 * Resolves the page size and sheet size to a single value, which
	 * becomes the size of the page.
	 * 
	 * In the output document of an "overlay" imposition, the page and the
	 * sheet are the same thing. This implies that page size and sheet
	 * size should both resolve to the same value.
	 * If both are explicitly set to a different value, an exception
	 * will be thrown to indicate this.
	 * If only sheet size is given, use it as page size.
	 * In any case, only the page size will be used afterwards and the
	 * sheet size can be discarded.
	 * 
	 * @param pageSize the size of the page
	 * @param sheetSize the size of the sheet
	 * @return the page size
	 */
	private Dimensions resolvePageAndSheetSize(DimensionSettings pageSize,
	                                           DimensionSettings sheetSize) {
		if (sheetSize != DimensionSettings.AUTO) {
			assert sheetSize.isValue();
			if (pageSize == DimensionSettings.AUTO) {
				logger.verbose("overlay_pageSizeToSheetSize");
				pageSize = sheetSize;
			} else {
				assert pageSize.isValue();
				if (!pageSize.equals(sheetSize)) {
					// The page size and sheet size are in conflict.
					throw new IllegalStateException
						("Sheet size and page size are set to a different value");
				}
			}
		}
		// Otherwise just leave pageSize as it is
		assert pageSize.isValue();
		return pageSize.value();
	}
	
	/**
	 * Resolves page count into a valid value.
	 * If the number of pages is unset, calculates the number of pages
	 * necessary to fit all given documents; otherwise uses the given value.
	 * 
	 * @param pageCount the declared page count
	 * @param docs documents used in automatic determining the page count
	 * @return the given page count, or the length of the longest document
	 *         in {@code docs} if page count was not set (was negative)
	 */
	private int resolvePageCount(CommonSettings common, List<VirtualDocument> docs) {
		int pageCount = common.getPageCount();
		if (pageCount < 0) {
			pageCount = VirtualDocument.maxLength(docs);
			pageCount = pageCount * common.getRepeatPage() * common.getRepeatDocument();
			logger.verbose("overlay_pageCountAll", pageCount);
		} else {
			logger.verbose("overlay_pageCountPartial", pageCount);
		}
		return pageCount;
	}
	
	// TODO Check page++
	private List<VirtualDocument> preprocessDocuments(
			List<VirtualDocument> docs, Preprocessor preprocessor, int pageCount) {
		List<VirtualDocument> processedDocs = new ArrayList<>(pageCount);
		int page = 0;
		for (VirtualDocument doc : docs) {
			processedDocs.add(preprocessor.processDocument(doc));
			if (page++ >= pageCount) break;
		}
		return processedDocs;
	}
	
	/**
	 * Builds a new list of layered pages.
	 * 
	 * @param template the page to be used as template, with correct size,
	 *        margins and number of layers
	 * @param pageCount the resolved number of pages to be built
	 * @return a list containing the new layered pages
	 */
	private List<LayeredPage> buildPages(LayeredPage template, int pageCount) {
		logger.verbose("overlay_constructing", pageCount, template.numberOfLayers());
//		LayeredPage template = new LayeredPage(dims[0], dims[1], layerCount);
		List<LayeredPage> pageList = new ArrayList<>(pageCount);
		int pageNumber = 0;
		while(pageList.size() < pageCount) {
			LayeredPage page = new LayeredPage(template);
			page.setNumber(++pageNumber);
			pageList.add(page);
		}
		return pageList;
	}
	
	/**
	 * Fills the given target pages with pages of the given source documents.
	 * @param source the documents to be used as the source of the pages
	 * @param target the pages to be filled with the source pages
	 */
	private void fillPages(List<VirtualDocument> source, List<LayeredPage> target) {
		logger.verbose("overlay_filling");
		LayeredPageFiller lsp = new LayeredPageFiller(source);
		lsp.setSourceTo(target);
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
		logger.warn("overlay_singleFile");
		return impose(Collections.singletonList(source));
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
	
	/**
	 * Builds instances of {@code Overlay} objects.
	 *
	 * @author Singon
	 */
	public static final class Builder implements ImposableBuilder<Overlay> {
		private Preprocessor.Settings preprocess = Preprocessor.Settings.auto();
		private CommonSettings common = CommonSettings.auto();
		private boolean repeatInLayer = false;
		
		
		public void setRepeatInLayer(boolean repeatInLayer) {
			this.repeatInLayer = repeatInLayer;
		}

		@Override
		public ImposableBuilder<Overlay> acceptPreprocessSettings(Settings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Preprocess settings cannot be null");
			this.preprocess = settings;
			return this;
		}

		@Override
		public ImposableBuilder<Overlay> acceptCommonSettings(CommonSettings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Settings cannot be null");
			this.common = settings;
			return this;
		}

		@Override
		public Overlay build() {
			return new Overlay(preprocess, common, repeatInLayer);
		}
	}
	
	/**
	 * Fills layered pages with content.
	 */
	private class LayeredPageFiller {
		
		/**
		 * List of page source objects which provide the iterators for
		 * {@link #pageIterators}
		 * The layers are filled in the order of these objects.
		 */
		private final List<PageSource> pageSources;
		/**
		 * Queues of pages, each of which will fill one layer.
		 * The layers are filled in the order of these queues.
		 */
		private final List<Iterator<VirtualPage>> pageIterators;
		/** The cached number of layers */
		private final int layers;
		/** Stores information whether empty warning has been issued for ith layer */
		private final boolean[] queueEmptyWarningIssued;
		
		LayeredPageFiller(List<VirtualDocument> documents) {
			// Each document is fed into one layer
			layers = documents.size();
			queueEmptyWarningIssued = new boolean[layers];
			List<PageSource> srcList = new ArrayList<>(layers);
			List<Iterator<VirtualPage>> iterList = new ArrayList<>(layers);
			
			for (VirtualDocument doc : documents) {
				PageSource ps = pageSourceBuilder(common, doc).build();
				srcList.add(ps);
				iterList.add(ps.iterator());
			}
			this.pageSources = srcList;
			this.pageIterators = iterList;
		}

		void setSourceTo(Iterable<LayeredPage> pages) {
			for (LayeredPage pg : pages) {
				setSourceTo(pg);
			}
			
		}

		void setSourceTo(LayeredPage page) {
			int layersInPage = page.numberOfLayers();
			if (layersInPage > layers) {
				logger.debug("overlay_tooManyLayersInPage", layersInPage, layers);
			}
			int layerNo = 0;
			for (PageletView layer : page.getLayers()) {
				if (layerNo < layers) {
					Iterator<VirtualPage> pageIter = pageIterators.get(layerNo);
					if (pageIter.hasNext()) {
						layer.setSource(pageIter.next());
					} else if (repeatInLayer) {
						// If there are no more pages, yet repeating is desired
						// replace the iterator with a new instance
						logger.verbose("overlay_repeating", layerNo, page);
						Iterator<VirtualPage> newIter = pageSources.get(layerNo).iterator();
						pageIterators.set(layerNo, newIter);
						if (newIter.hasNext()) {
							layer.setSource(newIter.next());
						}
					} else if (!queueEmptyWarningIssued[layerNo]) {
						logger.warn("overlay_queueEmpty", layerNo, page);
						queueEmptyWarningIssued[layerNo] = true;
					}
				}
				layerNo++;
			}
		}
	}
}
