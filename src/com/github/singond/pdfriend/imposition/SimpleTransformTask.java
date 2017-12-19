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
import com.github.singond.pdfriend.book.SinglePage;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.GeometryUtils;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.CommonSettings.MarginSettings;
import com.github.singond.pdfriend.imposition.Preprocessor.Settings;

/**
 * A document consisting of layered pages.
 *
 * @author Singon
 */
public class SimpleTransformTask extends AbstractImposable implements Imposable {

	/** The internal name of this imposable document type */
	private static final String NAME = "simple transformation";
	/** Logger */
	private static ExtendedLogger logger = Log.logger(SimpleTransformTask.class);

	private final Preprocessor.Settings preprocess;
	private final CommonSettings common;
	private final LengthUnit unit = Imposition.LENGTH_UNIT;


	private SimpleTransformTask(Preprocessor.Settings preprocess, CommonSettings common) {
		if (preprocess == null)
			throw new IllegalArgumentException("Preprocessor settings must not be null");
		if (common == null)
			throw new IllegalArgumentException("Common settings must not be null");
		
		this.preprocess = preprocess.copy();
		this.common = common;
	}
	
	
	/**
	 * Imposes the given virtual document into a list of grid pages
	 * according to the current settings of this {@code Overlay} object.
	 */
	private List<SinglePage> imposeAsPages(VirtualDocument doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("imposition_preprocessSettings", preprocess);
			logger.debug("imposition_commonSettings", common);
//			logger.debug("imposition_imposableSettings", NAME, );
		}
		
		// Select a use case and run it
		if (common.getPageSize() == DimensionSettings.AUTO
				&& common.getSheetSize() == DimensionSettings.AUTO) {
			// Case A
			return casePageSize(doc);
		} else if (preprocess.isAutoSize()) {
			// Case C
			return caseContentSize(doc);
		} else if (common.getMargins() == MarginSettings.AUTO) {
			// Case B
			return caseMargins(doc);
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
	private List<SinglePage> casePageSize(VirtualDocument doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("simple_casePageSize");
		}
		
		Margins margins = resolveAutoMargins(common.getMargins());
		preprocess.setCellMargins(margins);
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		Dimensions contentSize = preprocessor.getResolvedCellDimensions();
		Dimensions pageSize;
		pageSize = GeometryUtils.rectanglePlusMargins(contentSize, margins);
		SinglePage template = new SinglePage(pageSize.width().in(unit),
		                                     pageSize.height().in(unit));
		
		doc = preprocessor.processAll();
		List<SinglePage> pages = buildPages(common, template, doc);
		return pages;
		
//		PageSource pageSrc = pageSourceBuilder(common, doc).build();
////		int pageCount = resolvePageCount(common, doc);
//		int pageCount = pageSrc.size();
//		List<SinglePage> pages = buildEmptyPages(template, pageCount);
//		PageFillers.fillSequentially(pages, pageSrc);
//		fillPages(docs, pages);
//		return pages;
	}
	
	/**
	 * Builds the list of pages, determining the width of margins from
	 * remaining parameters.
	 * 
	 * @param doc the documents to be imposed
	 * @return the imposed document as a list of pages
	 */
	private List<SinglePage> caseMargins(VirtualDocument doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("simple_caseMargins");
		}
		
		Dimensions pageSize = resolvePageAndSheetSize
				(common.getPageSize(), common.getSheetSize());
		
		// Helper preprocessor to resolve cell size
		Preprocessor helper = new Preprocessor(doc, preprocess);
		Dimensions contentSize = helper.getResolvedCellDimensions();
		Length oneHorizontalMargin = Length.subtract(
				pageSize.width(), contentSize.width()).times(1d/2);
		Length oneVerticalMargin = Length.subtract(
				pageSize.height(), contentSize.height()).times(1d/2);
		Margins margins = new Margins(oneHorizontalMargin, oneVerticalMargin);
		preprocess.setCellDimensions(DimensionSettings.of(pageSize));
		preprocess.setCellMargins(margins);
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		pageSize = preprocessor.getResolvedCellDimensions();
		SinglePage template = new SinglePage(pageSize.width().in(unit),
		                                     pageSize.height().in(unit));
		
//		int pageCount = resolvePageCount(common, doc);
//		doc = preprocessDocuments(doc, preprocessor, pageCount);
//		List<LayeredPage> pages = buildEmptyPages(template, pageCount);
//		fillPages(doc, pages);
//		return pages;
		
		doc = preprocessor.processAll();
		List<SinglePage> pages = buildPages(common, template, doc);
		return pages;
	}
	
	/**
	 * Builds the list of pages, determining content size from remaining parameters.
	 * 
	 * @param doc the documents to be imposed
	 * @return the imposed document as a list of pages
	 */
	private List<SinglePage> caseContentSize(VirtualDocument doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("simple_caseContentSize");
		}
		
		Dimensions pageSize = resolvePageAndSheetSize
				(common.getPageSize(), common.getSheetSize());
		
		Margins margins = resolveAutoMargins(common.getMargins());
		Dimensions contentSize = GeometryUtils.rectangleMinusMargins(pageSize, margins);
		preprocess.setCellDimensions(DimensionSettings.of(contentSize));
		preprocess.setCellMargins(margins);
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		pageSize = preprocessor.getResolvedCellDimensions();
		SinglePage template = new SinglePage(pageSize.width().in(unit),
		                                     pageSize.height().in(unit));
		
//		int pageCount = resolvePageCount(common, doc);
//		doc = preprocessDocuments(doc, preprocessor, pageCount);
//		List<LayeredPage> pages = buildEmptyPages(template, pageCount);
//		fillPages(doc, pages);
//		return pages;
		
		doc = preprocessor.processAll();
		List<SinglePage> pages = buildPages(common, template, doc);
		return pages;
	}
	
	/**
	 * Resolves margins object into a valid value.
	 * 
	 * @param margins margins object to be resolved
	 * @return the argument, if it is a valid value, and the default value
	 *         of (0, 0, 0, 0) otherwise
	 */
	private Margins resolveAutoMargins(MarginSettings margins) {
		if (margins.isValue()) {
			return margins.value();
		} else {
			// Resolve automatic margins
			Margins m = new Margins(0, 0, 0, 0, LengthUnits.METRE);
			logger.verbose("overlay_marginsResolveAuto", m);
			return m;
		}
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
	 * @throws IllegalArgumentException if both sizes are {@code auto}
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
		} else {
			if (pageSize == DimensionSettings.AUTO)
				throw new IllegalArgumentException
						("Both page size and sheet size are auto. Cannot resolve final size.");
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
	 * @param doc document used in automatic determining the page count
	 * @return the given page count, or the length of the longest document
	 *         in {@code docs} if page count was not set (was negative)
	 */
//	private int resolvePageCount(CommonSettings common, VirtualDocument doc) {
//		int pageCount = common.getPageCount();
//		if (pageCount < 0) {
//			pageCount = doc.getLength();
//			pageCount = pageCount * common.getRepeatPage() * common.getRepeatDocument();
//			logger.verbose("overlay_pageCountAll", pageCount);
//		} else {
//			logger.verbose("overlay_pageCountPartial", pageCount);
//		}
//		return pageCount;
//	}
	
	/**
	 * Builds a new list of pages.
	 *
	 * @param common common imposition settings
	 * @param template the page to be used as template, with correct size
	 *        and margins
	 * @param doc the document to be transformed
	 * @return a list containing the new pages
	 */
	private List<SinglePage> buildPages(CommonSettings common, SinglePage template, VirtualDocument doc) {
		PageSource pageSrc = pageSourceBuilder(common, doc).build();
		int pageCount = pageSrc.size();
		List<SinglePage> pages = buildEmptyPages(template, pageCount);
		PageFillers.fillSequentially(pages, pageSrc);
		return pages;
	}
	
	/**
	 * Builds a new list of blank pages.
	 * 
	 * @param template the page to be used as template, with correct size
	 *        and margins
	 * @param pageCount the number of pages to be built
	 * @return a list containing the new pages
	 */
	private List<SinglePage> buildEmptyPages(SinglePage template, int pageCount) {
		logger.verbose("simple_constructing", pageCount);
		List<SinglePage> pageList = new ArrayList<>(pageCount);
		int pageNumber = 0;
		while(pageList.size() < pageCount) {
			SinglePage page = new SinglePage(template);
			page.setNumber(++pageNumber);
			pageList.add(page);
		}
		return pageList;
	}


//	/**
//	 * Fills the given target pages with pages of the given source documents.
//	 * @param source the documents to be used as the source of the pages
//	 * @param target the pages to be filled with the source pages
//	 */
//	private void fillPages(List<VirtualDocument> source, List<SinglePage> target) {
//		logger.verbose("overlay_filling");
//		LayeredPageFiller lsp = new LayeredPageFiller(source);
//		lsp.setSourceTo(target);
//	}

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
		return false;
	}

	@Override
	public Book impose(VirtualDocument source) {
		return new LoosePages(imposeAsPages(source));
	}

	@Override
	public Book impose(List<VirtualDocument> sources) {
		return impose(VirtualDocument.concatenate(sources));
	}

	/**
	 * Builds instances of {@code Overlay} objects.
	 *
	 * @author Singon
	 */
	public static final class Builder implements ImposableBuilder<SimpleTransformTask> {
		private Preprocessor.Settings preprocess = Preprocessor.Settings.auto();
		private CommonSettings common = CommonSettings.auto();
		

		@Override
		public ImposableBuilder<SimpleTransformTask> acceptPreprocessSettings(Settings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Preprocess settings cannot be null");
			this.preprocess = settings;
			return this;
		}

		@Override
		public ImposableBuilder<SimpleTransformTask> acceptCommonSettings(CommonSettings settings) {
			if (settings == null)
				throw new IllegalArgumentException("Settings cannot be null");
			this.common = settings;
			return this;
		}

		@Override
		public SimpleTransformTask build() {
			return new SimpleTransformTask(preprocess, common);
		}
	}
}
