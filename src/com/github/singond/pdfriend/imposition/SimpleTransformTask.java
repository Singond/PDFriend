package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.Book;
import com.github.singond.pdfriend.book.LoosePages;
import com.github.singond.pdfriend.book.OneSidedBook;
import com.github.singond.pdfriend.book.SinglePage;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.CommonSettings.MarginSettings;
import com.github.singond.pdfriend.imposition.Preprocessor.Resizing;

/**
 * A document consisting of layered pages.
 *
 * @author Singon
 */
public class SimpleTransformTask extends AbstractImposable<LoosePages>
		implements Imposable<LoosePages> {

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
	 * Imposes the given virtual document into a list of pages
	 * according to the current settings of this {@code SimpleTransformTask} object.
	 */
	private List<SinglePage> imposeAsPages(VirtualDocument doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("imposition_preprocessSettings", preprocess);
			logger.debug("imposition_commonSettings", common);
//			logger.debug("imposition_imposableSettings", NAME, );
		}
		
		if (common.getPageSize() != DimensionSettings.AUTO
				|| common.getSheetSize() != DimensionSettings.AUTO) {
			DimensionSettings pageSize = common.getPageSize();
			DimensionSettings sheetSize = common.getSheetSize();
			Dimensions resolvedDims = resolvePageAndSheetSize(pageSize, sheetSize);
			preprocess.setCellDimensions(resolvedDims);
		}
		
		Margins margins = resolveAutoMargins(common.getMargins());
		preprocess.setCellMargins(margins);
		
		// Use "resize to fit" as the default
		if (preprocess.getResizing() == Resizing.AUTO) {
			preprocess.setResizing(Resizing.FIT);
		}
		
		Preprocessor preprocessor = new Preprocessor(doc, preprocess);
		Dimensions pageSize = preprocessor.getResolvedCellDimensions();
		SinglePage template = new SinglePage(pageSize.width().in(unit),
		                                     pageSize.height().in(unit));
		
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
	 * In the output document of simple transformation, the page and the
	 * sheet are the same thing. This implies that page size and sheet
	 * size should both resolve to the same value.
	 * If both are explicitly set to a different value, an exception
	 * will be thrown to indicate this.
	 * If only sheet size is given, it is used as the page size,
	 * and if only page size is given, it is used directly.
	 * 
	 * @param pageSize the size of the page
	 * @param sheetSize the size of the sheet
	 * @return the resulting page size
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


	@Override
	public String getName() {
		return NAME;
	}

//	@Override
	public LoosePages impose(VirtualDocument source) {
		return new LoosePages(imposeAsPages(source));
	}

	@Override
	public LoosePages impose(List<VirtualDocument> sources) {
		return impose(VirtualDocument.concatenate(sources));
	}

	/**
	 * Builds instances of {@code SimpleTransformTask} objects.
	 *
	 * @author Singon
	 */
	public static final class Builder extends AbstractImposableBuilder<SimpleTransformTask> {
		@Override
		public SimpleTransformTask build() {
			return new SimpleTransformTask(preprocess, common);
		}

		@Override
		public ImpositionTask buildTask() {
			return ImpositionTaskFactory.oneSided(build());
		}
	}
}
